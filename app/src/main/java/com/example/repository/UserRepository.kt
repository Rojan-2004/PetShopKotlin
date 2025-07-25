package com.example.petshop.repository

import android.provider.ContactsContract.CommonDataKinds.Email
import com.example.petshop.model.UserModule
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    //Login
    //register
    //getCurrentUser
    //updateProfile
    //addUserToDatabase
    //logout
    //
    fun login(email: String, password : String,
              callback:(Boolean,String) -> Unit)
    //Authentication
    fun register(email: String, password : String,
                 callback:(Boolean,String,String) -> Unit)
    fun forgetPassword(
        email: String, callback: (Boolean, String) -> Unit
    )
    fun getCurrentUser() :FirebaseUser?
    fun updateProfile(userId: String, data: MutableMap<String,Any?>,
                      callback :(Boolean,String)->Unit)
    fun addUserToDatabase(userId:String, mode: UserModule,
                          callback: (Boolean, String) -> Unit)
    fun logout(callback: (Boolean, String) -> Unit)

    fun getUserById(userId: String, callback: (UserModule?, Boolean, String) -> Unit)
}