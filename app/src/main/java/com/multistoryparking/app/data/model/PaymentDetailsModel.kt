package com.multistoryparking.app.data.model

class PaymentDetailsModel(
    var email: String = "",
    var bookingType: String? = null,
    var amountPaid: Double = 0.0,
    var discountApplied: Double = 0.0,
    var vehicleType: String = "",
    var dateTime: String = ""
)