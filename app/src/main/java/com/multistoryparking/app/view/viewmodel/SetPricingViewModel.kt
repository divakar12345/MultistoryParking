package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.model.FloorDetailsModel
import com.multistoryparking.app.data.model.SetPriceModel
import com.multistoryparking.app.data.remote.DAOSetPrice
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import kotlinx.coroutines.launch

class SetPricingViewModel : BaseViewModel() {

    private var daoSetPricing = DAOSetPrice()
    var setPriceLiveData = MutableLiveData<ArrayList<SetPriceModel>>()
    private var setPricingArrayList = ArrayList<SetPriceModel>()

    fun setPricing(setPriceModel: SetPriceModel) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoSetPricing.add(setPriceModel)
                .addOnSuccessListener {
                    MainActivity.progressbarVisible(false)
                    Toast.success("Price updated successfully!!")
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun getPricing() {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
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
}