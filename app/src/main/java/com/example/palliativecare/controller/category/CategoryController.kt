package com.example.palliativecare.controller.category

import com.example.palliativecare.model.Article
import com.example.palliativecare.model.Category
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CategoryController {
    val db = Firebase.firestore

    suspend fun getAllCategory(): List<Category> {
        return db.collection("category").get().await().documents.map { documentSnapshot ->
            val category = documentSnapshot.toObject(Category::class.java)!!
            Category(
                name = category.name,
                id = documentSnapshot.id
            )
        }
    }
}