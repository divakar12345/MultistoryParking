package com.multistoryparking.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.multistoryparking.app.App
import com.multistoryparking.app.R
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.databinding.FragmentProfileBinding
import com.multistoryparking.app.di.AppContractor
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.viewmodel.ProfileViewModel
import com.multistoryparking.app.view.viewmodel.SignInViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        config()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.emailText.text = MainActivity.preference.get(SharedPrefs.EMAIL, "")
        binding.vehicleNumberText.text = MainActivity.preference.get(SharedPrefs.VEHICLE_NUMBER, "")
        binding.vehicleTypeText.text = MainActivity.preference.get(SharedPrefs.VEHICLE_TYPE, "")
    }

    private fun config() {
        binding.viewModel = viewModel
    }
}