package com.example.palliativecare.model

data class User(
    val image: String,
    val name: String,
    val phone: String,
    val lastMessageDate: String = "12:00"
)