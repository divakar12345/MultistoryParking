package com.multistoryparking.app.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.multistoryparking.app.R
import com.multistoryparking.app.data.model.SetDiscountModel
import com.multistoryparking.app.databinding.DiscountAlertdialogBinding
import com.multistoryparking.app.databinding.FragmentSetDiscountBinding
import com.multistoryparking.app.utils.RecyclerViewItemDecoration
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.adapter.CouponAdapter
import com.multistoryparking.app.view.viewmodel.SetDiscountViewModel

class SetDiscountFragment : Fragment() {

    private lateinit var binding: FragmentSetDiscountBinding
    private val viewModel: SetDiscountViewModel by viewModels()
    private lateinit var couponAdapter: CouponAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_set_discount, container, false)
        config()

        viewModel.getAllCoupons()
        binding.floatingActionButton.setOnClickListener {
            setDiscountAlertDialog()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        couponAdapter = CouponAdapter("Admin", viewModel, this)
        binding.couponRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.couponRecycler.addItemDecoration(
            RecyclerViewItemDecoration(
                requireContext(),
                R.drawable.divider
            )
        )
        binding.couponRecycler.adapter = couponAdapter
        viewModel.setDiscountLiveData.observe(this, {
            MainActivity.progressbarVisible(false)
            couponAdapter.submitList(it.orEmpty().toMutableList())
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

    fun config() {
        MainActivity.mainParams()
        binding.viewModel = viewModel
    }

    private fun setDiscountAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding: DiscountAlertdialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.discount_alertdialog, null, false)

        builder.setView(dialogBinding.root)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.card_background)
        alertDialog.show()
        alertDialog.setCancelable(true)

        with(dialogBinding) {
            this.submitButton.setOnClickListener {
                when {
                    this.titleEdittext.text.isEmpty() -> Toast.error("Title cannot be empty!!")
                    this.couponPerUserEdittext.text.isEmpty() -> Toast.error("Please enter the number of coupons per user!!")
                    this.firstDiscountEdittext.text.isEmpty() -> Toast.error("First hour discount cannot be empty!!")
                    this.extraDiscountEdittext.text.isEmpty() -> Toast.error("Extra hours discount cannot be empty!!")
                    else -> {
                        viewModel.addDiscount(
                            SetDiscountModel(
                                this.titleEdittext.text.toString().trim(),
                                this.firstDiscountEdittext.text.toString().trim().toInt(),
                                this.extraDiscountEdittext.text.toString().trim().toInt(),
                                this.couponPerUserEdittext.text.toString().trim().toInt()
                            )
                        )
                        alertDialog.dismiss()
                    }
                }
            }
        }
    }
}