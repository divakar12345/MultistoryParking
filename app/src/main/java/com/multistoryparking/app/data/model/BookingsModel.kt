package com.multistoryparking.app.data.model

class BookingsModel {
    var lotName: String = ""
    var date: String = ""
    var time: String = ""

    constructor(lotName: String, date: String, time: String) {
        this.lotName = lotName
        this.date = date
        this.time = time
    }
}