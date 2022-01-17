package com.multistoryparking.app.data.model

class LotDetailsModel(
    var floorName: String = "",
    var lotName: String = "",
    var lotType: String = "",
    var lotStatus: Int = 0, //0 for not occupied, 1 for occupied
    var vehicleNumber: String? = null,
    var dateTime: String? = null,
    var bookingType: String? = null,
    var userEmail: String? = null
)