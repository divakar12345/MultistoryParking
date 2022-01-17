package com.multistoryparking.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.multistoryparking.app.R
import com.multistoryparking.app.databinding.FragmentAdminBinding
import com.multistoryparking.app.databinding.MenuBottomSheetBinding
import com.multistoryparking.app.databinding.NewFloorBottomSheetBinding
import com.multistoryparking.app.utils.RecyclerViewItemDecoration
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.adapter.FloorListRecyclerAdapter
import com.multistoryparking.app.view.viewmodel.AdminViewModel

class AdminFragment : Fragment() {

    private lateinit var binding: FragmentAdminBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var floorListRecyclerAdapter: FloorListRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin, container, false)
        config()
        viewModel.getAllFloors()

        binding.floatingActionButton.setOnClickListener {
            menuBottomSheet()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        floorListRecyclerAdapter = FloorListRecyclerAdapter()
        binding.floorListRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.floorListRecycler.addItemDecoration(
            RecyclerViewItemDecoration(
                requireContext(),
                R.drawable.divider
            )
        )
        binding.floorListRecycler.adapter = floorListRecyclerAdapter
        viewModel.floorDetailsLiveData.observe(this, {
            MainActivity.progressbarVisible(false)
            floorListRecyclerAdapter.submitList(it.orEmpty().toMutableList())
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun config() {
        binding.viewModel = viewModel
    }

    private fun menuBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val binding: MenuBottomSheetBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.menu_bottom_sheet,
            null,
            false
        )

        dialog.setCancelable(true)
        dialog.setContentView(binding.root)
        dialog.show()

        binding.addFloorText.setOnClickListener {
            newFloorBottomSheet()
            dialog.dismiss()
        }
        binding.setPricingText.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToSetPricingFragment())
            dialog.dismiss()
        }
        binding.viewAuditText.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToAuditFragment())
            dialog.dismiss()
        }
        binding.setDiscountText.setOnClickListener {
            findNavController().navigate(AdminFragmentDirections.actionAdminFragmentToSetDiscountFragment())
            dialog.dismiss()
        }
    }

    private fun newFloorBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val binding: NewFloorBottomSheetBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.new_floor_bottom_sheet,
            null,
            false
        )

        dialog.setCancelable(true)
        dialog.setContentView(binding.root)
        dialog.show()

        binding.goButton.setOnClickListener {
            if (binding.areaEdittext.text.isNotEmpty())
                viewModel.addFloor(binding.areaEdittext.text.toString().trim().toInt(), dialog)
            else
                Toast.error("Please enter the area in sqft.")
        }
    }
}