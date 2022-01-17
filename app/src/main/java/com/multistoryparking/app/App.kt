package com.multistoryparking.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.multistoryparking.app.di.AppContractor

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        contractor = AppContractor(this)
        FirebaseApp.initializeApp(this)
    }

    companion object {
        private lateinit var contractor: AppContractor

        val appContractor by lazy {
            contractor
        }
    }
}