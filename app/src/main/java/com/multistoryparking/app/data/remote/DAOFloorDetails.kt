package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.FloorDetailsModel

class DAOFloorDetails {

    var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(FloorDetailsModel::class.java.simpleName)
    }

    fun add(floorDetails: FloorDetailsModel, floorName: String): Task<Void> {
        return databaseReference.child(floorName).setValue(floorDetails)
    }

    fun get(): Query {
        return databaseReference.orderByKey()
    }
}