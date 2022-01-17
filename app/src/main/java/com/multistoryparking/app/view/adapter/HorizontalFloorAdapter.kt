package com.multistoryparking.app.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.FloorDetailsModel
import com.multistoryparking.app.data.model.LotDetailsModel
import com.multistoryparking.app.databinding.ItemFloorBinding

class HorizontalFloorAdapter :
    ListAdapter<FloorDetailsModel, HorizontalFloorAdapter.ViewHolder>(PostHomeComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_floor,
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

    class ViewHolder(private val binding: ItemFloorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var bikeLotArrayList = ArrayList<LotDetailsModel>()
        var carLotArrayList = ArrayList<LotDetailsModel>()
        var busLotArrayList = ArrayList<LotDetailsModel>()
        var bikeAdapter = LotRecyclerAdapter()
        var carAdapter = LotRecyclerAdapter()
        var busAdapter = LotRecyclerAdapter()

        @SuppressLint("SetTextI18n")
        fun bind(item: FloorDetailsModel) {
            with(binding) {
                this.floorName.text = item.floorName
                this.bikeRecycler.layoutManager = GridLayoutManager(this.bikeRecycler.context, 5)
                this.carRecycler.layoutManager = GridLayoutManager(this.carRecycler.context, 3)
                this.busRecycler.layoutManager = GridLayoutManager(this.busRecycler.context, 2)

                this.bikeRecycler.adapter = bikeAdapter
                this.carRecycler.adapter = carAdapter
                this.busRecycler.adapter = busAdapter

                for (data in item.lotDetailsArrayList) {
                    when (data.lotType) {
                        "Bike" -> bikeLotArrayList.add(data)
                        "Car" -> carLotArrayList.add(data)
                        "Bus" -> busLotArrayList.add(data)
                    }
                }

                bikeAdapter.submitList(bikeLotArrayList.toMutableList())
                carAdapter.submitList(carLotArrayList.toMutableList())
                busAdapter.submitList(busLotArrayList.toMutableList())
            }
        }
    }

    object PostHomeComparator : DiffUtil.ItemCallback<FloorDetailsModel>() {
        override fun areItemsTheSame(oldItem: FloorDetailsModel, newItem: FloorDetailsModel) =
            oldItem.floorName == newItem.floorName

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: FloorDetailsModel, newItem: FloorDetailsModel) =
            oldItem == newItem
    }
}