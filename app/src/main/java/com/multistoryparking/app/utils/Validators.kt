package com.multistoryparking.app.utils

import android.util.Patterns
import androidx.annotation.FloatRange
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.regex.Pattern

// For Validators
fun String.hasValidLength(length: Int = 6) = this.length < length

const val STRING_HAS_NOT_VALID_LENGTH = "Password should be minimum 8 characters long"

// Validator Extensions

fun ObservableField<String>.isValidEmail(message: String = "Please enter valid email") =
    when (Patterns.EMAIL_ADDRESS.matcher(get().orEmpty()).matches()) {
        true -> false
        else -> {
            Toast.error(message)
            true
        }
    }

fun ObservableField<String>.isValidPassword(
    message: String = "Please enter valid password",
): Boolean {
    val value = get().orEmpty()
    return when {
        value.isEmpty() -> true.also { Toast.error(message) }
        value.hasValidLength() -> true.also { Toast.error(STRING_HAS_NOT_VALID_LENGTH) }
        else -> false
    }
}