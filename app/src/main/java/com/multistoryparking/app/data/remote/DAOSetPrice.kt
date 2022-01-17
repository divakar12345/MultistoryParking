package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.SetPriceModel

class DAOSetPrice {

    var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(SetPriceModel::class.java.simpleName)
    }

    fun add(setPriceModel: SetPriceModel): Task<Void> {
        return databaseReference.child(setPriceModel.vehicleType).setValue(setPriceModel)
    }

    fun get(): Query {
        return databaseReference.orderByKey()
    }
}