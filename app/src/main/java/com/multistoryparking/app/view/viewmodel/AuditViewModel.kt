package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.data.remote.DAOPaymentDetails
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import kotlinx.coroutines.launch

class AuditViewModel : BaseViewModel() {

    var daoPaymentDetails = DAOPaymentDetails()
    var paymentDetailsArrayList = ArrayList<PaymentDetailsModel>()
    var paymentDetailsLiveData = MutableLiveData<ArrayList<PaymentDetailsModel>>()

    fun getAllPaymentDetails() {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoPaymentDetails.get().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    paymentDetailsLiveData.value?.clear()
                    for (data in snapshot.children) {
                        data.getValue(PaymentDetailsModel::class.java)
                            ?.let { paymentDetailsArrayList.add(it) }
                    }
                    paymentDetailsLiveData.value = paymentDetailsArrayList
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }
}