package com.multistoryparking.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.multistoryparking.app.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
object Toast {
    private lateinit var context: Context

    operator fun invoke(activity: App) {
        context = activity
    }

    fun success(message: String, length: Int = Toast.LENGTH_SHORT) =
        show(State.SUCCESS, message, length)

    fun error(message: String, length: Int = Toast.LENGTH_SHORT) =
        show(State.ERROR, message, length)

    fun error(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) =
        show(State.ERROR, message, length)

    private fun show(state: State, @StringRes message: Int, length: Int = Toast.LENGTH_SHORT) =
        show(state, context.getString(message), length)

    private fun show(state: State, message: String, length: Int = Toast.LENGTH_SHORT) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                val msg = message.ifEmpty { "No message found" }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    enum class State {
        ERROR,
        SUCCESS
    }
}

