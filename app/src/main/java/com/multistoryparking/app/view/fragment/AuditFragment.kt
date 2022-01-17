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
import com.google.android.material.tabs.TabLayoutMediator
import com.multistoryparking.app.R
import com.multistoryparking.app.databinding.FragmentAuditBinding
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.adapter.ChildFragmentStateAdapter
import com.multistoryparking.app.view.viewmodel.AuditViewModel

class AuditFragment : Fragment() {

    private lateinit var binding: FragmentAuditBinding
    private val viewModel: AuditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audit, container, false)
        config()
        viewModel.getAllPaymentDetails()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        val tabLayout = binding.tabLayout

        val viewPager = binding.viewPager

        viewPager.adapter = ChildFragmentStateAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Bike"
                1 -> tab.text = "Car"
                2 -> tab.text = "Bus"
            }
        }.attach()
    }

    fun config() {
        MainActivity.mainParams()
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
}