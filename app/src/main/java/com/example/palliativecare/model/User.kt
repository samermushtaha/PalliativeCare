package com.example.palliativecare.model

import android.net.Uri

data class User(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val address: String,
    val birthdate: String,
    val image: Uri?,
    val userType: String
)
data class LoginUser(
    val email: String,
    val password: String
)
