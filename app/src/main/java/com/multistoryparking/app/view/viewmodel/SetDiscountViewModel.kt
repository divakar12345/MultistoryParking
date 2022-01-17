package com.multistoryparking.app.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.multistoryparking.app.data.model.AppliedCouponModel
import com.multistoryparking.app.data.model.ApplyCouponModel
import com.multistoryparking.app.data.model.CouponModel
import com.multistoryparking.app.data.model.SetDiscountModel
import com.multistoryparking.app.data.remote.DAOAppliedCoupon
import com.multistoryparking.app.data.remote.DAOSetDiscount
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import kotlinx.coroutines.launch

class SetDiscountViewModel : BaseViewModel() {

    var daoSetDiscount = DAOSetDiscount()
    var setDiscountArrayList = ArrayList<SetDiscountModel>()
    var setDiscountLiveData = MutableLiveData<ArrayList<SetDiscountModel>>()
    var daoAppliedCoupon = DAOAppliedCoupon()
    var appliedCouponArrayList = ArrayList<AppliedCouponModel>()
    var appliedCouponLiveData = MutableLiveData<CouponModel>()
    var applyCouponLiveData = MutableLiveData<ApplyCouponModel>()
    var couponResponseLiveData = MutableLiveData<Boolean>()

    fun addDiscount(setDiscountModel: SetDiscountModel) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoSetDiscount.add(setDiscountModel)
                .addOnSuccessListener {
                    MainActivity.progressbarVisible(false)
                    Toast.success("Discount added successfully!!")
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun applyDiscount(appliedCouponModel: AppliedCouponModel) {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoAppliedCoupon.add(appliedCouponModel)
                .addOnSuccessListener {
                    couponResponseLiveData.value = true
                }.addOnFailureListener {
                    Toast.error(it.message.toString())
                }
        }
    }

    fun getAllCoupons() {
        viewModelScope.launch {
            MainActivity.progressbarVisible()
            daoSetDiscount.get().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    setDiscountLiveData.value?.clear()
                    for (data in snapshot.children) {
                        data.getValue(SetDiscountModel::class.java)
                            ?.let { setDiscountArrayList.add(it) }
                    }
                    setDiscountLiveData.value = setDiscountArrayList
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.error(error.message)
                }
            })
        }
    }

    fun getMyAppliedCoupons() {
        daoAppliedCoupon.get().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appliedCouponLiveData.value?.appliedCouponArrayList?.clear()
                appliedCouponLiveData.value?.observer = false
                for (data in snapshot.children) {
                    data.getValue(AppliedCouponModel::class.java)
                        ?.let { appliedCouponArrayList.add(it) }
                }
                appliedCouponLiveData.value = CouponModel(appliedCouponArrayList, true)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}