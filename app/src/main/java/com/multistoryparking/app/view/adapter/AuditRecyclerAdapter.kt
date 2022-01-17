package com.multistoryparking.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.databinding.ItemAuditBinding

class AuditRecyclerAdapter :
    ListAdapter<PaymentDetailsModel, AuditRecyclerAdapter.ViewHolder>(PostHomeComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_audit,
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

    class ViewHolder(private val binding: ItemAuditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: PaymentDetailsModel) {
            with(binding) {
                this.emailText.text = item.email
                this.dateAndTypeText.text = "${item.dateTime} - ${item.bookingType}"
                this.amountText.text = "$${item.amountPaid}"
            }
        }
    }

    object PostHomeComparator : DiffUtil.ItemCallback<PaymentDetailsModel>() {
        override fun areItemsTheSame(oldItem: PaymentDetailsModel, newItem: PaymentDetailsModel) =
            oldItem.vehicleType == newItem.vehicleType

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PaymentDetailsModel, newItem: PaymentDetailsModel) =
            oldItem == newItem
    }
}