package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.model.FloorDetailsModel
import com.multistoryparking.app.data.model.LotDetailsModel
import com.multistoryparking.app.data.remote.DAOFloorDetails
import com.multistoryparking.app.data.remote.DAOLotDetails
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import kotlinx.coroutines.launch

class AdminViewModel : BaseViewModel() {

    private val daoFloorDetails = DAOFloorDetails()
    private val daoLotDetails = DAOLotDetails()
    var floorDetailsLiveData = MutableLiveData<ArrayList<FloorDetailsModel>>()
    val floorDetailsArrayList = ArrayList<FloorDetailsModel>()
    private val lotDetailsArrayList = ArrayList<LotDetailsModel>()

    fun addFloor(area: Int, dialog: BottomSheetDialog) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            val busLot: Int =
                ((area * 20) / 100) / (4 * 8) //since I considered space for one bus as 4*8 sqft.

            val carLot: Int =
                ((area * 40) / 100) / (3 * 4) //since I considered space for one car as 3*4 sqft.

            val bikeLot: Int =
                ((area * 40) / 100) / (1 * 2) //since I considered space for one bike as 1*2 sqft.

            val floorId = floorDetailsLiveData.value?.size
            val floorName = "Floor ${floorId?.plus(1)}"
            val floorDetailsModel = FloorDetailsModel(
                floorId,
                floorName,
                area,
                bikeLot,
                carLot,
                busLot
            )
            floorDetailsLiveData.value?.clear()
            daoFloorDetails.add(floorDetailsModel, floorName)
                .addOnSuccessListener {
                    addLot(bikeLot, carLot, busLot, floorName, dialog)
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun getAllFloors() {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoFloorDetails.get().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    floorDetailsLiveData.value?.clear()
                    for (data in snapshot.children) {
                        data.getValue(FloorDetailsModel::class.java)
                            ?.let { floorDetailsArrayList.add(it) }
                    }
                    floorDetailsLiveData.value = floorDetailsArrayList
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }

    private fun addLot(
        bikeLot: Int,
        carLot: Int,
        busLot: Int,
        floorName: String,
        dialog: BottomSheetDialog
    ) {
        viewModelScope.launch {
            lotDetailsArrayList.clear()
            var i = 1
            while (i <= bikeLot) {
                lotDetailsArrayList.add(
                    LotDetailsModel(
                        floorName,
                        "Bike Lot $i",
                        "Bike",
                        0,
                        null,
                        null,
                        null,
                        null
                    )
                )
                i++
            }
            i = 1
            while (i <= carLot) {
                lotDetailsArrayList.add(
                    LotDetailsModel(
                        floorName,
                        "Car Lot $i",
                        "Car",
                        0,
                        null,
                        null,
                        null,
                        null
                    )
                )
                i++
            }
            i = 1
            while (i <= busLot) {
                lotDetailsArrayList.add(
                    LotDetailsModel(
                        floorName,
                        "Bus Lot $i",
                        "Bus",
                        0,
                        null,
                        null,
                        null,
                        null
                    )
                )
                i++
            }
            daoLotDetails.addList(lotDetailsArrayList, floorName).addOnSuccessListener {
                MainActivity.progressbarVisible(false)
                Toast.success("The floor added successfully !!")
                dialog.dismiss()
            }
        }
    }
}