package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.SetDiscountModel

class DAOSetDiscount {
    var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(SetDiscountModel::class.java.simpleName)
    }

    fun add(setDiscount: SetDiscountModel): Task<Void> {
        return databaseReference.child(setDiscount.discountTitle).setValue(setDiscount)
    }

    fun get(): Query {
        return databaseReference.orderByKey()
    }
}