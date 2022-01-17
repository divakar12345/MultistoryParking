package com.multistoryparking.app.di

import android.content.Context
import com.multistoryparking.app.App
import com.multistoryparking.app.data.local.PreferenceHelper
import com.multistoryparking.app.utils.ThemeHelper
import com.multistoryparking.app.utils.Toast

class AppContractor(app: App) {

    /* Application Level Context */
    val context: Context by lazy {
        app.applicationContext
    }

    /* Application Level Preference Manager */
    val preference by lazy {
        PreferenceHelper(app)
    }

    /* Application Level Theme Helper */
    val themeHelper by lazy {
        ThemeHelper(context)
    }

    init {
        /* Toast Init */
        Toast(app)
    }
}