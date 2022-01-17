package com.multistoryparking.app.view.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.multistoryparking.app.data.local.SharedPrefs.EMAIL
import com.multistoryparking.app.data.local.SharedPrefs.LOGGED_IN
import com.multistoryparking.app.data.local.SharedPrefs.UID
import com.multistoryparking.app.utils.Toast
import com.multistoryparking.app.utils.isValidEmail
import com.multistoryparking.app.utils.isValidPassword
import com.multistoryparking.app.utils.toNormal
import com.multistoryparking.app.view.BaseViewModel
import com.multistoryparking.app.view.MainActivity
import com.multistoryparking.app.view.fragment.SignUpFragmentDirections
import kotlinx.coroutines.launch

class SignUpViewModel : BaseViewModel() {

    private lateinit var mAuth: FirebaseAuth
    val email = ObservableField("")
    val password = ObservableField("")
    val confirmPassword = ObservableField("")

    fun signUp(view: View) {
        viewModelScope.launch {
            if (email.isValidEmail())
                return@launch
            if (password.isValidPassword())
                return@launch
            if (password.toNormal() == confirmPassword.toNormal()) {
                mAuth = FirebaseAuth.getInstance()
                MainActivity.progressbarVisible()
                mAuth.createUserWithEmailAndPassword(email.toNormal(), password.toNormal())
                    .addOnCompleteListener { task ->
                        MainActivity.progressbarVisible(false)
                        if (task.isSuccessful) {
                            preference.put(EMAIL, task.result.user?.email)
                            preference.put(UID, task.result.user?.uid)
                            preference.put(LOGGED_IN, 1)
                            MainActivity.mainParams(true)
                            view.findNavController()
                                .navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
                        } else {
                            Toast.error(task.exception?.message.toString())
                        }
                    }
            } else {
                Toast.error("The password and the confirm password must be same!!")
            }
        }
    }
}