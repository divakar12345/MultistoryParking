package com.multistoryparking.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.edit
import com.multistoryparking.app.BuildConfig
import com.multistoryparking.app.utils.ThemeHelper.Companion.Themes.*

class ThemeHelper(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            BuildConfig.APPLICATION_ID.replaceBefore(".", "").replaceAfter(
                ".",
                ""
            ), Context.MODE_PRIVATE
        )
    }

    fun updateForTheme(theme: Themes = currentTheme) {
        if (BuildConfig.DEBUG) Log.i("$THEME  =>", theme.storageKey)
        setDefaultNightMode(
            when (theme) {
                DARK -> MODE_NIGHT_YES
                LIGHT -> MODE_NIGHT_NO
                SYSTEM -> MODE_NIGHT_FOLLOW_SYSTEM
                BATTERY_SAVER -> MODE_NIGHT_AUTO_BATTERY
            }
        )
        currentTheme = theme
    }

    private fun themeFromStorageKey(storageKey: String) =
        values().firstOrNull {it.storageKey == storageKey } ?: LIGHT

    var currentTheme: Themes
        get() = themeFromStorageKey(prefs.getString(THEME, LIGHT.storageKey) ?: LIGHT.storageKey)
        private set(value) = prefs.edit(commit = true) { putString(THEME, value.storageKey) }

    companion object {
        private const val THEME = "THEME"

        enum class Themes(val storageKey: String) {
            LIGHT("LIGHT"),
            DARK("DARK"),
            SYSTEM("SYSTEM"),
            BATTERY_SAVER("BATTERY_SAVER")
        }
    }
}