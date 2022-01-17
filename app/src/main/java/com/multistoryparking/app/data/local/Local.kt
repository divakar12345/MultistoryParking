package com.multistoryparking.app.data.local

import androidx.lifecycle.MutableLiveData
import com.multistoryparking.app.data.model.UserInfoModel

object Local {
    var userModel: MutableLiveData<UserInfoModel> = MutableLiveData()
}