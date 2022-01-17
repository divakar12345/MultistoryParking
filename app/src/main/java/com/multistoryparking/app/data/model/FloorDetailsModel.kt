package com.multistoryparking.app.data.model

class FloorDetailsModel {
    var floorId: Int? = 0
    var floorName: String = ""
    var floorSpace: Int = 0
    var bikeLotCount: Int = 0
    var carLotCount: Int = 0
    var busLotCount: Int = 0
    var lotDetailsArrayList = ArrayList<LotDetailsModel>()

    constructor(
        floorId: Int?,
        floorName: String,
        floorSpace: Int,
        bikeLotCount: Int,
        carLotCount: Int,
        busLotCount: Int
    ) {
        this.floorId = floorId
        this.floorName = floorName
        this.floorSpace = floorSpace
        this.bikeLotCount = bikeLotCount
        this.carLotCount = carLotCount
        this.busLotCount = busLotCount
    }

    constructor(
        floorId: Int?,
        floorName: String,
        floorSpace: Int,
        bikeLotCount: Int,
        carLotCount: Int,
        busLotCount: Int,
        lotDetailsArrayList: ArrayList<LotDetailsModel>
    ) {
        this.floorId = floorId
        this.floorName = floorName
        this.floorSpace = floorSpace
        this.bikeLotCount = bikeLotCount
        this.carLotCount = carLotCount
        this.busLotCount = busLotCount
        this.lotDetailsArrayList = lotDetailsArrayList
    }

    constructor()


}