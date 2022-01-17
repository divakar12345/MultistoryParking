package com.multistoryparking.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.SetPriceModel
import com.multistoryparking.app.databinding.ItemSetPaymentBinding
import com.multistoryparking.app.view.viewmodel.SetPricingViewModel

class SetPriceAdapter(var viewModel: SetPricingViewModel) :
    ListAdapter<SetPriceModel, SetPriceAdapter.ViewHolder>(PostHomeComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_set_payment,
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

    inner class ViewHolder(private val binding: ItemSetPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SetPriceModel) {
            with(binding) {
                this.basePayEdittext.setText(item.basePrice.toString())
                this.additionalPayEdittext.setText(item.additionalPrice.toString())
                this.reservationEdittext.setText(item.reservationPrice.toString())
                this.titleText.text = "Set Pricing for ${item.vehicleType}"
                this.submitButton.setOnClickListener {
                    viewModel.setPricing(
                        SetPriceModel(
                            vehicleType = item.vehicleType,
                            basePrice = this.basePayEdittext.text.toString().toDouble(),
                            additionalPrice = this.additionalPayEdittext.text.toString().toDouble(),
                            reservationPrice = this.reservationEdittext.text.toString().toDouble()
                        )
                    )
                }
            }
        }
    }

    object PostHomeComparator : DiffUtil.ItemCallback<SetPriceModel>() {
        override fun areItemsTheSame(oldItem: SetPriceModel, newItem: SetPriceModel) =
            oldItem.vehicleType == newItem.vehicleType

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SetPriceModel, newItem: SetPriceModel) =
            oldItem == newItem
    }
}