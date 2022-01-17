package com.multistoryparking.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.multistoryparking.app.R
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.data.model.AppliedCouponModel
import com.multistoryparking.app.data.model.ApplyCouponModel
import com.multistoryparking.app.data.model.SetDiscountModel
import com.multistoryparking.app.databinding.ItemAdminCouponBinding
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.viewmodel.SetDiscountViewModel

class CouponAdapter(
    private val type: String,
    private val discountViewModel: SetDiscountViewModel,
    private val myLifeCycle: LifecycleOwner
) :
    ListAdapter<SetDiscountModel, CouponAdapter.ViewHolder>(PostHomeComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_admin_coupon,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { item ->
            with(holder) {
                itemView.tag = item
                bind(item)
            }
        }
    }

    inner class ViewHolder(private val binding: ItemAdminCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SetDiscountModel) {
            with(binding) {
                this.discountTitle.text = item.discountTitle
                this.couponCount.text = "${item.couponPerUser} coupons per user."
                this.firstDiscount.text = "${item.firstHourDiscount}% discount on first hour."
                this.extraDiscount.text = "${item.extraHourDiscount}% discount on extra hours."
                when (type) {
                    "Admin" -> {
                        this.applyText.visibility = View.GONE
                        this.parentLayout.isEnabled = false
                    }
                    "User" -> {
                        this.applyText.visibility = View.VISIBLE
                        this.parentLayout.isEnabled = true
                    }
                }
                this.parentLayout.setOnClickListener {
                    discountViewModel.getMyAppliedCoupons()
                }
            }

            discountViewModel.appliedCouponLiveData.observe(myLifeCycle, {
                if (it.observer) {
                    var checkUsed = false
                    val rawAppliedCouponArrayList: ArrayList<AppliedCouponModel> =
                        it.appliedCouponArrayList
                    val appliedCouponArrayList = ArrayList<AppliedCouponModel>()
                    for (data in rawAppliedCouponArrayList) {
                        if (data.uid == MainActivity.preference.get(SharedPrefs.UID, ""))
                            appliedCouponArrayList.add(data)
                    }
                    if (appliedCouponArrayList.isEmpty()) {
                        discountViewModel.applyCouponLiveData.value = ApplyCouponModel(item, 0)
                    } else {
                        var applyCouponModel = ApplyCouponModel(null, null)
                        for (data in appliedCouponArrayList) {
                            if (data.discountTitle == item.discountTitle && data.usedCount < item.couponPerUser) {
                                applyCouponModel = ApplyCouponModel(item, data.usedCount)
                                checkUsed = true
                            } else checkUsed = false
                        }
                        if (!checkUsed)
                            Toast.error("You have used up your maximum number of coupons!!")
                        else discountViewModel.applyCouponLiveData.value = applyCouponModel
                    }
                    it.observer = false
                }
            })
        }
    }

    object PostHomeComparator : DiffUtil.ItemCallback<SetDiscountModel>() {
        override fun areItemsTheSame(oldItem: SetDiscountModel, newItem: SetDiscountModel) =
            oldItem.discountTitle == newItem.discountTitle

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SetDiscountModel, newItem: SetDiscountModel) =
            oldItem == newItem
    }
}