package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.model.FloorDetailsModel
import com.multistoryparking.app.data.model.LotDetailsModel
import com.multistoryparking.app.data.remote.DAOFloorDetails
import com.multistoryparking.app.data.remote.DAOLotDetails
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.BaseViewModel
import kotlinx.coroutines.launch

class FloorMapViewModel : BaseViewModel() {

    private val daoLotDetails = DAOLotDetails()
    private var lotDetailsArrayList = ArrayList<LotDetailsModel>()
    var fetchVM = false
    var indicator: GenericTypeIndicator<ArrayList<LotDetailsModel>> =
        object : GenericTypeIndicator<ArrayList<LotDetailsModel>>() {}
    private val daoFloorDetails = DAOFloorDetails()
    var floorDetailsLiveData = MutableLiveData<ArrayList<FloorDetailsModel>>()
    val floorDetailsArrayList = ArrayList<FloorDetailsModel>()
    var lotDetailsLiveData = MutableLiveData<ArrayList<LotDetailsModel>>()

    fun getAllFloors() {
        viewModelScope.launch {
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

    fun getAllLot(fetch: Boolean) {
        viewModelScope.launch {
            fetchVM = fetch
            daoLotDetails.get().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (fetchVM) {
                        for (data in snapshot.children) {
                            lotDetailsArrayList.addAll(data.getValue(indicator)!!)
                        }
                        lotDetailsLiveData.value = lotDetailsArrayList
                        fetchVM = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }
}