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
import com.multistoryparking.app.R
import com.multistoryparking.app.databinding.FragmentSetPricingBinding
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.adapter.SetPriceAdapter
import com.multistoryparking.app.view.viewmodel.SetPricingViewModel

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.multistoryparking.app.utils.RecyclerViewItemDecoration

class SetPricingFragment : Fragment() {

    lateinit var binding: FragmentSetPricingBinding
    private val viewModel: SetPricingViewModel by viewModels()
    private lateinit var adapter: SetPriceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_pricing, container, false)
        config()
        viewModel.getPricing()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.setPriceRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = SetPriceAdapter(viewModel)
        binding.setPriceRecycler.adapter = adapter
        viewModel.setPriceLiveData.observe(this, {
            MainActivity.progressbarVisible(false)
            adapter.submitList(it.orEmpty().toMutableList())
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
        MainActivity.mainParams()
        binding.viewModel = viewModel
    }
}