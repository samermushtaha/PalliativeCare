package com.example.palliativecare.controller.category

import android.util.Log
import com.example.palliativecare.model.Category
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
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

    fun addCategorySubscribersInFireStore(
        categoryId: String,
        newToken: String,
        callback: (Boolean) -> Unit,
    ) {
        val db = Firebase.firestore
        val auth = Firebase.auth
        val userId = auth.currentUser?.uid
        val categoryRef = db.collection("category").document(categoryId)
        val newSubscriber = hashMapOf(userId.toString() to newToken)
        categoryRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val categorySubscribersRef = document.getDocumentReference("subscribers")
                    if (categorySubscribersRef == null) {
                        db.collection("categorySubscribers").add(newSubscriber)
                            .addOnSuccessListener { newDocument ->
                                callback(true)
                                categoryRef.update("subscribers", newDocument)
                            }.addOnFailureListener {
                                callback(false)
                            }
                    } else {
                        categorySubscribersRef.update(newSubscriber as Map<String, String>)
                    }
                }
            }
        }


    }

    fun getCategorySubscribers(categoryId: String, callback: (HashMap<String,String>) -> Unit) {
        val db = Firebase.firestore
        db.collection("category").document(categoryId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val categorySubscribersRef = document.getDocumentReference("subscribers")
                    categorySubscribersRef?.let {ref->
                        ref.get().addOnSuccessListener {
                            it.data?.let {map->
                                callback(map as HashMap<String, String>)
                            }
                        }
                    }
                }
            }

        }

    }
}