package com.multistoryparking.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.databinding.FragmentBikeAuditBinding
import com.multistoryparking.app.utils.RecyclerViewItemDecoration
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.adapter.AuditRecyclerAdapter
import com.multistoryparking.app.view.viewmodel.AuditViewModel

class BikeAuditFragment : Fragment() {

    private lateinit var binding: FragmentBikeAuditBinding
    private val viewModel: AuditViewModel by viewModels()
    private lateinit var adapter: AuditRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bike_audit, container, false)
        viewModel.getAllPaymentDetails()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.auditRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.auditRecycler.addItemDecoration(
            RecyclerViewItemDecoration(
                requireContext(),
                R.drawable.divider
            )
        )
        adapter = AuditRecyclerAdapter()
        binding.auditRecycler.adapter = adapter

        viewModel.paymentDetailsLiveData.observe(this, {
            val bikeArrayList = ArrayList<PaymentDetailsModel>()
            var totalAmount = 0.0
            for (data in it)
                if (data.vehicleType == "Bike") {
                    totalAmount += data.amountPaid
                    bikeArrayList.add(data)}
            MainActivity.progressbarVisible(false)
            if (bikeArrayList.isEmpty()) {
                binding.summaryLayout.visibility = View.GONE
                binding.placeholderText.visibility = View.VISIBLE
            } else{
                binding.totalAmountText.text = "$$totalAmount"
                binding.summaryLayout.visibility = View.VISIBLE
                binding.placeholderText.visibility = View.GONE
                adapter.submitList(bikeArrayList)
            }
        })
    }
}