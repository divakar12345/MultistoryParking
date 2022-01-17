package com.multistoryparking.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.multistoryparking.app.R
import com.multistoryparking.app.databinding.FragmentSignInBinding
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.viewmodel.SignInViewModel

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
        config()

        binding.signupTextButton.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }

        binding.adminAccessButton.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToAdminFragment())
        }

        return binding.root
    }

    private fun config() {
        MainActivity.mainParams()
        binding.viewModel = viewModel
    }
}