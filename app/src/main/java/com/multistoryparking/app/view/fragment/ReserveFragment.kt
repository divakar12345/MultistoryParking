package com.multistoryparking.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.multistoryparking.app.R
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.databinding.FragmentReserveBinding
import com.multistoryparking.app.databinding.ReserveInvoiceBottomSheetBinding
import com.multistoryparking.app.utils.getCurrentDateTime
import com.multistoryparking.app.utils.toString
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.viewmodel.ReserveViewModel

class ReserveFragment : Fragment() {

    private val viewModel: ReserveViewModel by viewModels()
    private lateinit var binding: FragmentReserveBinding
    private var invoiceDialog: BottomSheetDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reserve, container, false)
        config()

        binding.submitButton.setOnClickListener {
            invoiceBottomSheet()
        }

        binding.unreserveButton.setOnClickListener {
            viewModel.getAllLot(true, "Unreserve")
        }

        return binding.root
    }

    private fun config() {
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        updateUI()

        viewModel.callFloorMap.observe(this, {
            MainActivity.progressbarVisible(false)
            updateUI()
            if (it) {
                viewModel.callFloorMap.value = false
                findNavController().navigate(ReserveFragmentDirections.actionReserveFragmentToFloorMapFragment())
            }
        })

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(ReserveFragmentDirections.actionReserveFragmentToFloorMapFragment())
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateUI() {
        invoiceDialog?.dismiss()
        with(binding) {
            when {
                MainActivity.preference.get(SharedPrefs.BOOKING_TYPE, "") == "Reserve" -> {
                    this.floatingActionButton.visibility = View.VISIBLE
                    this.reserveLayout.visibility = View.GONE
                    this.unreserveLayout.visibility = View.VISIBLE
                    this.placeholderText.visibility = View.GONE
                    this.floorText.text =
                        "My Floor: ${MainActivity.preference.get(SharedPrefs.MY_FLOOR, "")}"
                    this.spaceText.text =
                        "My Space: ${MainActivity.preference.get(SharedPrefs.MY_LOT, "")}"
                    val dateTime = MainActivity.preference.get(SharedPrefs.DATE_TIME, "").split(" ")
                    this.dateText.text = "Reserved Date: ${dateTime[0]}"
                }
                MainActivity.preference.get(SharedPrefs.BOOKING_TYPE, "") == "Spot" -> {
                    this.floatingActionButton.visibility = View.GONE
                    this.reserveLayout.visibility = View.GONE
                    this.unreserveLayout.visibility = View.GONE
                    this.placeholderText.visibility = View.VISIBLE
                }
                else -> {
                    this.floatingActionButton.visibility = View.GONE
                    this.reserveLayout.visibility = View.VISIBLE
                    this.unreserveLayout.visibility = View.GONE
                    this.placeholderText.visibility = View.GONE
                    this.reserveText.text = with(MainActivity.preference) {
                        "You will be charged $${
                            this.get(
                                SharedPrefs.MY_VEHICLE_RESERVATION_PRICE,
                                0.0F
                            )
                        } to park a ${
                            this.get(
                                SharedPrefs.VEHICLE_TYPE,
                                ""
                            )
                        } in the given lot."
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun invoiceBottomSheet() {

        val currentDateTime = getCurrentDateTime()
        val vehicleReservePrice =
            MainActivity.preference.get(SharedPrefs.MY_VEHICLE_RESERVATION_PRICE, 0.0F)

        invoiceDialog = BottomSheetDialog(requireContext())

        val binding: ReserveInvoiceBottomSheetBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.reserve_invoice_bottom_sheet,
            null,
            false
        )
        with(binding) {

            this.reservePriceText.text = "$$vehicleReservePrice"
            this.reserveText.text = currentDateTime.toString("yyyy/MM/dd HH:mm:ss")
            this.amountPayableText.text = "$$vehicleReservePrice"
            invoiceDialog?.setCancelable(true)
            invoiceDialog?.setContentView(binding.root)
            invoiceDialog?.show()

            this.payButton.setOnClickListener {
                MainActivity.progressbarVisible()
                viewModel.storePaymentDetails(
                    PaymentDetailsModel(
                        MainActivity.preference.get(SharedPrefs.EMAIL, ""),
                        "Reserve",
                        vehicleReservePrice.toDouble(),
                        0.0,
                        MainActivity.preference.get(SharedPrefs.VEHICLE_TYPE, ""),
                        currentDateTime.toString("yyyy/MM/dd HH:mm:ss")
                    )
                )
            }
        }
    }
}