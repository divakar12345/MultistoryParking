package com.multistoryparking.app.view.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.multistoryparking.app.R
import com.multistoryparking.app.databinding.FragmentSplashBinding
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.viewmodel.SplashViewModel

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        config()
        viewModel.splashState.observe(viewLifecycleOwner) {
            goToNavigation()
        }

        return binding.root
    }

    private fun config() {
        MainActivity.mainParams()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun goToNavigation() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> viewModel.navigate(requireView())
        else -> requireActivity().dialogNotSupported().show()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun FragmentActivity.dialogNotSupported() =
        MaterialAlertDialogBuilder(applicationContext)
            .setTitle("Notice")
            .setMessage("Your device is not supported at the moment. Thank you for your understanding.")
            .setPositiveButton("OK") { _, _ ->
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> finishAndRemoveTask()
                    else -> finishAffinity()
                }
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)
            .create()
}