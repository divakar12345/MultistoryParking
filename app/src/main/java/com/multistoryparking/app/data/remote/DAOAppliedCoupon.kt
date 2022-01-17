package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.AppliedCouponModel

class DAOAppliedCoupon {
    var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(AppliedCouponModel::class.java.simpleName)
    }

    fun add(appliedCoupon: AppliedCouponModel): Task<Void> {
        return databaseReference.push().setValue(appliedCoupon)
    }

    fun get(): Query {
        return databaseReference.orderByKey()
    }
}