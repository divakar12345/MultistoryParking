package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.LotDetailsModel

class DAOLotDetails {

    var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(LotDetailsModel::class.java.simpleName)
    }

    fun addList(lotDetailsList: ArrayList<LotDetailsModel>, floorName: String): Task<Void> {
        return databaseReference.child(floorName).setValue(lotDetailsList)
    }

    fun get(): Query {
        return databaseReference.orderByKey()
    }

    fun updateLot(lotDetails: LotDetailsModel, lotNumber: String): Task<Void> {
        return databaseReference.child(lotDetails.floorName).child(lotNumber).setValue(lotDetails)
    }
}