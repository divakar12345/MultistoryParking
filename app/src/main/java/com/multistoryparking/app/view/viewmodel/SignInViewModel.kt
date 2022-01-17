package com.multistoryparking.app.view.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.multistoryparking.app.data.local.SharedPrefs
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.utils.isValidEmail
import com.multistoryparking.app.utils.isValidPassword
import com.multistoryparking.app.utils.toNormal
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.fragment.SignInFragmentDirections
import kotlinx.coroutines.launch

class SignInViewModel : BaseViewModel() {

    private lateinit var mAuth: FirebaseAuth
    val email = ObservableField("")
    val password = ObservableField("")

    fun signIn(view: View) {
        viewModelScope.launch {
            if (email.isValidEmail())
                return@launch
            if (password.isValidPassword())
                return@launch
            mAuth = FirebaseAuth.getInstance()
            MainActivity.progressbarVisible()
            mAuth.signInWithEmailAndPassword(email.toNormal(), password.toNormal())
                .addOnCompleteListener {
                    MainActivity.progressbarVisible(false)
                    if (it.isSuccessful) {
                        preference.put(SharedPrefs.EMAIL, it.result.user?.email)
                        preference.put(SharedPrefs.UID, it.result.user?.uid)
                        preference.put(SharedPrefs.LOGGED_IN, 1)
                        MainActivity.mainParams(true)
                        view.findNavController()
                            .navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment())
                    } else {
                        Toast.error(it.exception?.message.toString())
                    }
                }
        }
    }
}