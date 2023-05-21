package com.example.palliativecare.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class User(
    var name: String= "",
    var email: String = "",
    val password: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var birthdate: String = "",
    var image: String = "",
    var userType: String = "",
    var token:String = ""
){
    companion object {
        suspend fun getUserByID(id: String): List<User> {
            return Firebase.firestore.collection("users").get().await().documents
                .filter { documentSnapshot ->
                    documentSnapshot.id == id
                }.map { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java)!!
                }
        }
    }
}


data class LoginUser(
    val email: String,
    val password: String
)
