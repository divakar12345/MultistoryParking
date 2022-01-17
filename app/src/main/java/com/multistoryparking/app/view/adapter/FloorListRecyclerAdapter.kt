package com.multistoryparking.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.FloorDetailsModel
import com.multistoryparking.app.databinding.ItemFloorDetailsBinding

class FloorListRecyclerAdapter :
    ListAdapter<FloorDetailsModel, FloorListRecyclerAdapter.ViewHolder>(PostHomeComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_floor_details,
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

    class ViewHolder(private val binding: ItemFloorDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FloorDetailsModel) {
            with(binding) {
                this.floorName.text = item.floorName
                this.floorSize.text = "Space: ${item.floorSpace}sqft"
            }
        }
    }

    object PostHomeComparator : DiffUtil.ItemCallback<FloorDetailsModel>() {
        override fun areItemsTheSame(oldItem: FloorDetailsModel, newItem: FloorDetailsModel) =
            oldItem.floorName == newItem.floorName

        override fun areContentsTheSame(oldItem: FloorDetailsModel, newItem: FloorDetailsModel) =
            oldItem == newItem
    }
}