package com.multistoryparking.app.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.multistoryparking.app.data.model.UserInfoModel

class DAOUserInfo {
    var databaseReference: DatabaseReference
    private val user = FirebaseAuth.getInstance().currentUser

    init {
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference(UserInfoModel::class.java.simpleName)
    }

    fun add(userInfo: UserInfoModel): Task<Void> {
        return databaseReference.child(user!!.uid).setValue(userInfo)
    }

    fun checkUser(): Query {
        return databaseReference.orderByKey().equalTo(user?.uid)
    }

    fun updateUser(userInfo: UserInfoModel): Task<Void> {
        return databaseReference.child(user!!.uid).setValue(userInfo)
    }
}