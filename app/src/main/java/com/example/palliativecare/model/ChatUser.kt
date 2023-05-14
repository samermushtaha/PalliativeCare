package com.example.palliativecare.model

data class ChatUser(
    val image: String,
    val name: String,
    val phone: String,
    val userType: String,
    val lastMessageDate: String = "",
    val id: String
)