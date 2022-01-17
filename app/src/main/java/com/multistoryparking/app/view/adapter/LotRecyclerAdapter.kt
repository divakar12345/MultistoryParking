package com.multistoryparking.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.LotDetailsModel
import com.multistoryparking.app.databinding.ItemLotBinding

class LotRecyclerAdapter :
    ListAdapter<LotDetailsModel, LotRecyclerAdapter.ViewHolder>(PostHomeComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_lot,
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

    class ViewHolder(private val binding: ItemLotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: LotDetailsModel) {
            with(binding) {
                val layoutParams: ConstraintLayout.LayoutParams =
                    this.image.layoutParams as ConstraintLayout.LayoutParams

                when (item.lotType) {
                    "Bike" -> {
                        this.image.setImageRes(R.drawable.ic_bike)
                        layoutParams.width = 100
                        layoutParams.height = 100
                    }
                    "Car" -> {
                        this.image.setImageRes(R.drawable.ic_car)
                        layoutParams.width = 200
                        layoutParams.height = 200
                    }
                    "Bus" -> {
                        this.image.setImageRes(R.drawable.ic_bus)
                        layoutParams.width = 300
                        layoutParams.height = 300
                    }
                    else -> {}
                }
                this.image.layoutParams = layoutParams

                if (item.lotStatus == 0) {
                    this.image.setColorFilter(
                        ContextCompat.getColor(
                            this.image.context,
                            R.color.yellow
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else if (item.lotStatus == 1) {
                    this.image.setColorFilter(
                        ContextCompat.getColor(
                            this.image.context,
                            R.color.mustard
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }
    }

    object PostHomeComparator : DiffUtil.ItemCallback<LotDetailsModel>() {
        override fun areItemsTheSame(oldItem: LotDetailsModel, newItem: LotDetailsModel) =
            oldItem.floorName == newItem.floorName

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: LotDetailsModel, newItem: LotDetailsModel) =
            oldItem == newItem
    }
}