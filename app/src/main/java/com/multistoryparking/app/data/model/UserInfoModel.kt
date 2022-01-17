package com.multistoryparking.app.data.model

data class UserInfoModel(
    var uid: String = "",
    var email: String = "",
    var vehicleNumber: String = "",
    var vehicleType: String = "",
    var myLot: String? = null,
    var myFloor: String? = null,
    var date: String? = null,
    var bookingType: String? = null
)