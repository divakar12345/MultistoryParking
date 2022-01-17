package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.PaymentDetailsModel

class DAOPaymentDetails {
    var databaseReference: DatabaseReference

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(PaymentDetailsModel::class.java.simpleName)
    }

    fun add(paymentDetails: PaymentDetailsModel): Task<Void> {
        return databaseReference.push().setValue(paymentDetails)
    }

    fun get(): Query {
        return databaseReference.orderByKey()
    }
}