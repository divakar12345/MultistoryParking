package com.multistoryparking.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.FloorDetailsModel
import com.multistoryparking.app.databinding.FragmentFloorMapBinding
import com.multistoryparking.app.view.adapter.HorizontalFloorAdapter
import com.multistoryparking.app.view.viewmodel.FloorMapViewModel

class FloorMapFragment : Fragment() {

    private val viewModel: FloorMapViewModel by viewModels()
    private lateinit var binding: FragmentFloorMapBinding
    private var adapter = HorizontalFloorAdapter()
    var floorDetailsArrayList = ArrayList<FloorDetailsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_floor_map, container, false)

        viewModel.getAllFloors()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.floorRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.floorRecycler.adapter = adapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.floorRecycler)
        viewModel.floorDetailsLiveData.observe(this, {
            floorDetailsArrayList = it
            viewModel.getAllLot(true)
        })

        viewModel.lotDetailsLiveData.observe(this, {
            for (data1 in floorDetailsArrayList) {
                for (data2 in it) {
                    if (data1.floorName == data2.floorName)
                        data1.lotDetailsArrayList.add(data2)
                }
            }
            adapter.submitList(floorDetailsArrayList.toMutableList())
        })
    }
}