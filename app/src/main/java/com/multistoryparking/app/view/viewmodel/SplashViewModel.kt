package com.multistoryparking.app.view.viewmodel

import android.view.View
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.multistoryparking.app.data.local.SharedPrefs.LOGGED_IN
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.fragment.SplashFragmentDirections
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {

    val splashState = flow {
        delay(3_000)
        emit(Unit)
    }.asLiveData()

    fun navigate(view: View) {
        viewModelScope.launch {
            if (preference.get(LOGGED_IN, 0) == 0) {
                view.findNavController()
                    .navigate(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
            } else {
                MainActivity.mainParams(true)
                view.findNavController()
                    .navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
            }
        }
    }
}