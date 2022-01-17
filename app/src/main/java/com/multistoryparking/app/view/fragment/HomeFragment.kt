package com.multistoryparking.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.multistoryparking.app.R
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.data.model.AppliedCouponModel
import com.multistoryparking.app.data.model.ApplyCouponModel
import com.multistoryparking.app.data.model.PaymentDetailsModel
import com.multistoryparking.app.databinding.DiscountCouponAlertdialogBinding
import com.multistoryparking.app.databinding.FragmentHomeBinding
import com.multistoryparking.app.databinding.SpotInvoiceBottomSheetBinding
import com.multistoryparking.app.databinding.VehicleAlertdialogBinding
import com.multistoryparking.app.utils.*
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.adapter.CouponAdapter
import com.multistoryparking.app.view.viewmodel.HomeViewModel
import com.multistoryparking.app.view.viewmodel.SetDiscountViewModel
import java.util.*

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val discountViewModel: SetDiscountViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var couponAdapter: CouponAdapter
    private lateinit var discountAlertDialog: AlertDialog
    private lateinit var applyCoupon: ApplyCouponModel
    private var invoiceDialog: BottomSheetDialog? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        config()

        viewModel.checkUser()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI()

        viewModel.setVehicleResponse.observe(this, {
            if (it) {
                MainActivity.progressbarVisible(false)
                updateUI()
            }
        })
        viewModel.userUpdateCheckLiveData.observe(this, {
            if (!it) setVehicleAlertDialog() else viewModel.getPricing()
        })

        viewModel.callFloorMap.observe(this, {
            MainActivity.progressbarVisible(false)
            updateUI()
            if (it) {
                viewModel.callFloorMap.value = false
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFloorMapFragment())
            }
        })

        viewModel.setPriceLiveData.observe(this, {
            for (data in it) {
                with(MainActivity.preference) {
                    if (data.vehicleType == this.get(SharedPrefs.VEHICLE_TYPE, "")) {
                        this.put(SharedPrefs.MY_VEHICLE_BASE_PRICE, data.basePrice.toFloat())
                        this.put(
                            SharedPrefs.MY_VEHICLE_ADDITIONAL_PRICE,
                            data.additionalPrice.toFloat()
                        )
                        this.put(
                            SharedPrefs.MY_VEHICLE_RESERVATION_PRICE,
                            data.reservationPrice.toFloat()
                        )
                    }
                }
            }
            updateUI()
        })

        binding.submitButton.setOnClickListener {
            viewModel.getAllLot(true, "Start")
        }

        binding.completeButton.setOnClickListener {
            invoiceBottomSheet()
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFloorMapFragment())
        }
    }

    private fun config() {
        binding.viewModel = viewModel
    }

    private fun setVehicleAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding: VehicleAlertdialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.vehicle_alertdialog, null, false)

        builder.setView(dialogBinding.root)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.card_background)
        alertDialog.show()
        alertDialog.setCancelable(false)
        val vehicleTypeList = resources.getStringArray(R.array.vehicle_type)
        dialogBinding.vehicleTypeSpinner.adapter =
            ArrayAdapter(requireContext(), R.layout.item_dropdown, vehicleTypeList)

        var selectedVehicleType = ""
        dialogBinding.vehicleTypeSpinner.setItemSelectedListener { _, selected ->
            selectedVehicleType = selected
        }

        dialogBinding.submitButton.setOnClickListener {
            if (dialogBinding.vehicleNumberEdittext.text.toString().isNotEmpty()) {
                viewModel.addUser(
                    dialogBinding.vehicleNumberEdittext.text.toString().trim(),
                    selectedVehicleType
                )
                alertDialog.dismiss()
            } else
                Toast.error("Please enter the vehicle number!!")
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateUI() {
        invoiceDialog?.dismiss()
        with(binding) {
            when {
                MainActivity.preference.get(SharedPrefs.BOOKING_TYPE, "") == "Spot" -> {
                    this.floatingActionButton.visibility = View.VISIBLE
                    this.spotBookingLayout.visibility = View.GONE
                    this.endBookingLayout.visibility = View.VISIBLE
                    this.placeholderText.visibility = View.GONE
                    this.floorText.text =
                        "My Floor: ${MainActivity.preference.get(SharedPrefs.MY_FLOOR, "")}"
                    this.spaceText.text =
                        "My Space: ${MainActivity.preference.get(SharedPrefs.MY_LOT, "")}"
                    val dateTime = MainActivity.preference.get(SharedPrefs.DATE_TIME, "").split(" ")
                    this.dateText.text = "Check-in Date: ${dateTime[0]}"
                    this.timeText.text = "Check-in Time: ${dateTime[1]}"
                }
                MainActivity.preference.get(SharedPrefs.BOOKING_TYPE, "") == "Reserve" -> {
                    this.floatingActionButton.visibility = View.GONE
                    this.spotBookingLayout.visibility = View.GONE
                    this.endBookingLayout.visibility = View.GONE
                    this.placeholderText.visibility = View.VISIBLE
                }
                else -> {
                    this.floatingActionButton.visibility = View.GONE
                    this.spotBookingLayout.visibility = View.VISIBLE
                    this.endBookingLayout.visibility = View.GONE
                    this.placeholderText.visibility = View.GONE
                    this.baseText.text = with(MainActivity.preference) {
                        "The base price to park a ${
                            this.get(
                                SharedPrefs.VEHICLE_TYPE,
                                ""
                            )
                        } is $${
                            this.get(
                                SharedPrefs.MY_VEHICLE_BASE_PRICE,
                                0.0F
                            )
                        }, which is valid for an hour."
                    }

                    this.extraText.text = with(MainActivity.preference) {
                        "For the additional hours, each hour will be charged $${
                            this.get(
                                SharedPrefs.MY_VEHICLE_ADDITIONAL_PRICE,
                                0.0F
                            )
                        } until the ${
                            this.get(
                                SharedPrefs.VEHICLE_TYPE,
                                ""
                            )
                        } exits the lot."
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun invoiceBottomSheet() {

        val currentDateTime = getCurrentDateTime()
        val vehicleBasePrice = MainActivity.preference.get(SharedPrefs.MY_VEHICLE_BASE_PRICE, 0.0F)
        val vehicleAdditionalPrice = MainActivity.preference.get(
            SharedPrefs.MY_VEHICLE_ADDITIONAL_PRICE,
            0.0F
        )
        var discountedBasePrice: Float
        var discountedExtraPrice: Float
        var amountPayable: Float
        var discountAmount = 0.0F
        val checkInDateTime = MainActivity.preference.get(SharedPrefs.DATE_TIME, "")
        val totalHours =
            ((currentDateTime.time - Date(checkInDateTime).time) / (1000 * 60 * 60)) + 1
        val extraHours = totalHours - 1
        val extraHoursPrice =
            extraHours * vehicleAdditionalPrice
        val totalAmount =
            extraHoursPrice + vehicleBasePrice
        amountPayable = totalAmount
        invoiceDialog = BottomSheetDialog(requireContext())

        val binding: SpotInvoiceBottomSheetBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.spot_invoice_bottom_sheet,
            null,
            false
        )
        with(binding) {
            this.basePriceText.text = "$$vehicleBasePrice"
            this.checkinText.text = checkInDateTime
            this.checkoutText.text = currentDateTime.toString("yyyy/MM/dd HH:mm:ss")
            this.hoursText.text = extraHours.toString()
            this.additionalText.text = "$$extraHoursPrice"
            this.totalPriceText.text = "$$totalAmount"
            this.amountPayableText.text = "$$amountPayable"
            invoiceDialog?.setCancelable(true)
            invoiceDialog?.setContentView(binding.root)
            invoiceDialog?.show()

            this.discountText.setOnClickListener {
                discountCouponsAlertDialog()
            }
        }

        binding.payButton.setOnClickListener {
            MainActivity.progressbarVisible()
            if (discountAmount.toInt() > 0) {
                discountViewModel.applyDiscount(
                    AppliedCouponModel(
                        MainActivity.preference.get(
                            SharedPrefs.UID,
                            ""
                        ),
                        applyCoupon.setDiscountModel?.discountTitle!!,
                        applyCoupon.usedCount!! + 1
                    )
                )
            } else {
                viewModel.storePaymentDetails(
                    PaymentDetailsModel(
                        MainActivity.preference.get(SharedPrefs.EMAIL, ""),
                        MainActivity.preference.get(SharedPrefs.BOOKING_TYPE, ""),
                        amountPayable.toDouble(),
                        discountAmount.toDouble(),
                        MainActivity.preference.get(SharedPrefs.VEHICLE_TYPE, ""),
                        checkInDateTime
                    )
                )
            }
            invoiceDialog!!.dismiss()
        }

        discountViewModel.applyCouponLiveData.observe(this, {
            discountAlertDialog.dismiss()
            discountedBasePrice =
                (vehicleBasePrice * (100 - it.setDiscountModel?.firstHourDiscount!!)) / 100
            binding.basePriceText.text = "$$discountedBasePrice"
            discountedExtraPrice =
                (extraHoursPrice * (100 - it.setDiscountModel?.extraHourDiscount!!)) / 100
            binding.additionalText.text = "$$discountedExtraPrice"
            amountPayable = discountedBasePrice + discountedExtraPrice
            binding.amountPayableText.text = "$$amountPayable"
            discountAmount = totalAmount - amountPayable
            binding.discountText.text = "$$discountAmount"
            applyCoupon = it
            discountViewModel.couponResponseLiveData.observe(this, { boolean ->
                MainActivity.progressbarVisible(false)
                if (boolean) {
                    viewModel.storePaymentDetails(
                        PaymentDetailsModel(
                            MainActivity.preference.get(SharedPrefs.EMAIL, ""),
                            MainActivity.preference.get(SharedPrefs.BOOKING_TYPE, ""),
                            amountPayable.toDouble(),
                            discountAmount.toDouble(),
                            MainActivity.preference.get(SharedPrefs.VEHICLE_TYPE, ""),
                            checkInDateTime
                        )
                    )
                    discountViewModel.couponResponseLiveData.value = false
                }
            })

        })
    }

    private fun discountCouponsAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding: DiscountCouponAlertdialogBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.discount_coupon_alertdialog,
                null,
                false
            )

        builder.setView(dialogBinding.root)
        discountAlertDialog = builder.create()
        discountAlertDialog.window?.setBackgroundDrawableResource(R.drawable.card_background)
        discountAlertDialog.show()
        discountAlertDialog.setCancelable(true)
        discountViewModel.getAllCoupons()
        couponAdapter = CouponAdapter("User", discountViewModel, this)
        with(dialogBinding) {
            this.couponRecycler.layoutManager = LinearLayoutManager(requireContext())
            this.couponRecycler.addItemDecoration(
                RecyclerViewItemDecoration(
                    requireContext(),
                    R.drawable.divider
                )
            )
            this.couponRecycler.adapter = couponAdapter
        }
        discountViewModel.setDiscountLiveData.observe(this, {
            MainActivity.progressbarVisible(false)
            couponAdapter.submitList(it.orEmpty().toMutableList())
        })
    }
}