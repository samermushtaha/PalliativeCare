package com.example.palliativecare.model

import android.net.Uri

data class Article(
    val id: String = "",
    val description: String = "",
    val title: String = "",
    val createdAt: String = "",
    val doctorId: String = "",
    val categoryId: String = "",
    var picture: String = ""
)
