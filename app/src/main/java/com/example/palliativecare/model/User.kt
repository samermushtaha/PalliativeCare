package com.example.palliativecare.model

import android.net.Uri

data class User(
    var name: String= "",
    var email: String = "",
    val password: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var birthdate: String = "",
    var image: String = "",
    var userType: String = ""
)


data class LoginUser(
    val email: String,
    val password: String
)
