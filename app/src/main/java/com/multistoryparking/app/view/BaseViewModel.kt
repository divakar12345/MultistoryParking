package com.multistoryparking.app.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multistoryparking.app.App
import com.multistoryparking.app.di.AppContractor
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    private val TAG = "BaseViewModel"

    private val medium: AppContractor by lazy { App.appContractor }

    val preference get() = medium.preference

}