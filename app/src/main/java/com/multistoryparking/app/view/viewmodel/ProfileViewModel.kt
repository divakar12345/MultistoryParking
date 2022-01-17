package com.multistoryparking.app.view.viewmodel

import android.view.View
import androidx.navigation.findNavController
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.fragment.ProfileFragmentDirections

class ProfileViewModel : BaseViewModel() {

    fun logout(view: View) {
        preference.wipe()
        view.findNavController()
            .navigate(ProfileFragmentDirections.actionProfileFragmentToSignInFragment())
    }
}