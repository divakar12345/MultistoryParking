package com.multistoryparking.app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.Size
import androidx.core.content.edit
import com.multistoryparking.app.BuildConfig

class PreferenceHelper(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            BuildConfig.APPLICATION_ID.replaceBefore(".", "").replaceAfter(
                ".",
                ""
            ), Context.MODE_PRIVATE
        )
    }

    fun <T> get(@Size(min = 2) name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }!!
        @Suppress("UNCHECKED_CAST")
        res as T
    }

    fun <T> put(@Size(min = 2) name: String, value: T?) = prefs.edit(commit = true) {
        if (BuildConfig.DEBUG) Log.i("PREFERENCE", "$name => $value")
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }
    }

    fun wipe() = prefs.edit().clear().apply()
}