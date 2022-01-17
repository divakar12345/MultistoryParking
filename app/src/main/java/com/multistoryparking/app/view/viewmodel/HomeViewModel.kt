package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.data.model.LotDetailsModel
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.data.model.SetPriceModel
import com.multistoryparking.app.data.model.UserInfoModel
import com.multistoryparking.app.data.remote.DAOLotDetails
import com.multistoryparking.app.data.remote.DAOPaymentDetails
import com.multistoryparking.app.data.remote.DAOSetPrice
import com.multistoryparking.app.data.remote.DAOUserInfo
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.utils.getCurrentDateTime
import com.multistoryparking.app.utils.toString
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    private val daoUserInfo = DAOUserInfo()
    private val user = FirebaseAuth.getInstance().currentUser
    var userUpdateCheckLiveData = MutableLiveData<Boolean>()
    var userInfoModelArrayList = ArrayList<UserInfoModel>()
    private val daoLotDetails = DAOLotDetails()
    private var lotDetailsArrayList = ArrayList<LotDetailsModel>()
    private var currentDateTime = ""
    var fetchVM = false
    private var bookingType = ""
    var indicator: GenericTypeIndicator<ArrayList<LotDetailsModel>> =
        object : GenericTypeIndicator<ArrayList<LotDetailsModel>>() {}
    var callFloorMap = MutableLiveData<Boolean>()
    private var daoSetPricing = DAOSetPrice()
    var setPriceLiveData = MutableLiveData<ArrayList<SetPriceModel>>()
    private var setPricingArrayList = ArrayList<SetPriceModel>()
    private var daoPaymentDetails = DAOPaymentDetails()
    var setVehicleResponse = MutableLiveData<Boolean>()

    fun addUser(vehicleNumber: String, vehicleType: String) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            val userInfoModel = UserInfoModel(
                user!!.uid,
                preference.get(SharedPrefs.EMAIL, ""),
                vehicleNumber,
                vehicleType,
                null,
                null,
                null,
                null
            )
            daoUserInfo.add(userInfoModel)
                .addOnSuccessListener {
                    preference.put(SharedPrefs.VEHICLE_NUMBER, vehicleNumber)
                    preference.put(SharedPrefs.VEHICLE_TYPE, vehicleType)
                    Toast.success("User details added successfully!!")
                    setVehicleResponse.value = true
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun checkUser() {
        viewModelScope.launch {
            daoUserInfo.checkUser().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        data.getValue(UserInfoModel::class.java)
                            ?.let { userInfoModelArrayList.add(it) }
                    }
                    if (userInfoModelArrayList.size > 0) {
                        preference.put(
                            SharedPrefs.VEHICLE_NUMBER,
                            userInfoModelArrayList[0].vehicleNumber
                        )
                        preference.put(
                            SharedPrefs.VEHICLE_TYPE,
                            userInfoModelArrayList[0].vehicleType
                        )

                        userInfoModelArrayList[0].myFloor?.let {
                            preference.put(
                                SharedPrefs.MY_FLOOR,
                                it
                            )
                        }
                        userInfoModelArrayList[0].myLot?.let {
                            preference.put(
                                SharedPrefs.MY_LOT,
                                it
                            )
                        }
                        userInfoModelArrayList[0].date?.let {
                            preference.put(
                                SharedPrefs.DATE_TIME,
                                it
                            )
                        }
                        userInfoModelArrayList[0].bookingType?.let {
                            preference.put(
                                SharedPrefs.BOOKING_TYPE,
                                it
                            )
                        }
                    }
                    userUpdateCheckLiveData.value = snapshot.hasChildren()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }

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
                        if (type == "Start")
                            checkAvailability(lotDetailsArrayList)
                        else if (type == "End")
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
            bookingType = "Spot"
            currentDateTime = getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")
            daoLotDetails.updateLot(
                LotDetailsModel(
                    lotDetails.floorName,
                    lotDetails.lotName,
                    lotDetails.lotType,
                    1,
                    preference.get(SharedPrefs.VEHICLE_NUMBER, ""),
                    currentDateTime,
                    bookingType,
                    preference.get(SharedPrefs.EMAIL, "")
                ),
                lotNumber.toString()
            )
                .addOnSuccessListener {
                    updateUser(lotDetails.lotName, lotDetails.floorName, currentDateTime)
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun getMyLot(lotDetailsArrayList: ArrayList<LotDetailsModel>) {
        viewModelScope.launch {
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

    fun getPricing() {
        viewModelScope.launch {
            daoSetPricing.get().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    setPriceLiveData.value?.clear()
                    for (data in snapshot.children) {
                        data.getValue(SetPriceModel::class.java)
                            ?.let { setPricingArrayList.add(it) }
                    }
                    setPriceLiveData.value = setPricingArrayList
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }

    fun storePaymentDetails(paymentDetailsModel: PaymentDetailsModel) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoPaymentDetails.add(paymentDetailsModel)
                .addOnSuccessListener {
                    getAllLot(true, "End")
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }
}