package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.data.model.LotDetailsModel
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.data.model.UserInfoModel
import com.multistoryparking.app.data.remote.DAOLotDetails
import com.multistoryparking.app.data.remote.DAOPaymentDetails
import com.multistoryparking.app.data.remote.DAOUserInfo
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.utils.getCurrentDateTime
import com.multistoryparking.app.utils.toString
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import kotlinx.coroutines.launch

class ReserveViewModel : BaseViewModel() {

    private val daoUserInfo = DAOUserInfo()
    private val daoLotDetails = DAOLotDetails()
    private var lotDetailsArrayList = ArrayList<LotDetailsModel>()
    private var currentDateTime = ""
    var fetchVM = false
    private var bookingType = ""
    var indicator: GenericTypeIndicator<ArrayList<LotDetailsModel>> =
        object : GenericTypeIndicator<ArrayList<LotDetailsModel>>() {}
    var callFloorMap = MutableLiveData<Boolean>()
    private var date = ""
    private var daoPaymentDetails = DAOPaymentDetails()

    fun getAllLot(fetch: Boolean, type: String) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            fetchVM = fetch
            daoLotDetails.get().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (fetchVM) {
                        for (data in snapshot.children) {
                            lotDetailsArrayList.addAll(data.getValue(indicator)!!)
                        }
                        if (type == "Reserve")
                            checkAvailability(lotDetailsArrayList)
                        else if (type == "Unreserve")
                            getMyLot(lotDetailsArrayList)
                        fetchVM = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }

    fun checkAvailability(lotDetailsArrayList: ArrayList<LotDetailsModel>) {
        viewModelScope.launch {
            var i = 0
            for (data in lotDetailsArrayList) {
                if (data.lotType == preference.get(
                        SharedPrefs.VEHICLE_TYPE,
                        ""
                    ) && data.lotStatus == 0
                ) {
                    updateLot(data, i)
                    break
                }
                i++
            }
        }
    }

    private fun updateLot(lotDetails: LotDetailsModel, lotNumber: Int) {
        viewModelScope.launch {
            bookingType = "Reserve"
            currentDateTime = getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")
            val getDate = currentDateTime.split(" ")
            date = getDate[0] + " 00:00:00"
            daoLotDetails.updateLot(
                LotDetailsModel(
                    lotDetails.floorName,
                    lotDetails.lotName,
                    lotDetails.lotType,
                    1,
                    preference.get(SharedPrefs.VEHICLE_NUMBER, ""),
                    date,
                    bookingType,
                    preference.get(SharedPrefs.EMAIL, "")
                ),
                lotNumber.toString()
            )
                .addOnSuccessListener {
                    updateUser(lotDetails.lotName, lotDetails.floorName, date)
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun getMyLot(lotDetailsArrayList: ArrayList<LotDetailsModel>) {
        var i = 0
        for (data in lotDetailsArrayList) {
            if (data.userEmail == preference.get(
                    SharedPrefs.EMAIL,
                    ""
                ) && data.lotStatus == 1
            ) {
                endService(data, i)
                break
            }
            i++
        }
    }

    private fun endService(lotDetails: LotDetailsModel, lotNumber: Int) {
        viewModelScope.launch {
            bookingType = ""
            daoLotDetails.updateLot(
                LotDetailsModel(
                    lotDetails.floorName,
                    lotDetails.lotName,
                    lotDetails.lotType,
                    0,
                    null,
                    null,
                    null,
                    null
                ),
                lotNumber.toString()
            )
                .addOnSuccessListener {
                    updateUser(null, null, null)
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    private fun updateUser(lotName: String?, floorName: String?, currentDateTime: String?) {
        viewModelScope.launch {
            val userInfoModel = UserInfoModel(
                preference.get(SharedPrefs.UID, ""),
                preference.get(SharedPrefs.EMAIL, ""),
                preference.get(SharedPrefs.VEHICLE_NUMBER, ""),
                preference.get(SharedPrefs.VEHICLE_TYPE, ""),
                lotName,
                floorName,
                currentDateTime,
                bookingType
            )
            daoUserInfo.updateUser(userInfoModel)
                .addOnSuccessListener {
                    preference.put(SharedPrefs.MY_FLOOR, floorName ?: "")
                    preference.put(SharedPrefs.MY_LOT, lotName ?: "")
                    preference.put(SharedPrefs.DATE_TIME, currentDateTime ?: "")
                    preference.put(SharedPrefs.BOOKING_TYPE, bookingType)
                    callFloorMap.value = preference.get(SharedPrefs.MY_FLOOR, "").isNotEmpty()
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun storePaymentDetails(paymentDetailsModel: PaymentDetailsModel) {
        viewModelScope.launch {
            daoPaymentDetails.add(paymentDetailsModel)
                .addOnSuccessListener {
                    getAllLot(true, "Reserve")
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }
}