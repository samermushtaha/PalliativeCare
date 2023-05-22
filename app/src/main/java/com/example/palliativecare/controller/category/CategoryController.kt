package com.example.palliativecare.controller.category

import com.example.palliativecare.model.Category
import com.example.palliativecare.model.User
import com.google.firebase.firestore.FieldValue
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

    suspend fun addCategorySubscribersInFireStore(
        categoryId: String,
        userId: String,
        callback: (Boolean) -> Unit,
    ) {
        val db = Firebase.firestore
        val userName = User.getUserByID(userId).first().name
        val categoryRef = db.collection("category").document(categoryId)
        val newSubscriber = hashMapOf(userId to userName)
        categoryRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val categorySubscribersRef = document.getDocumentReference("subscribers")
                    if (categorySubscribersRef == null) {
                        db.collection("categorySubscribers").add(newSubscriber)
                            .addOnSuccessListener { newDocument ->
                                categoryRef.update("subscribers", newDocument)
                                callback(true)
                            }.addOnFailureListener {
                                callback(false)
                            }
                    } else {
                        categorySubscribersRef.update(newSubscriber as Map<String, String>)
                        callback(true)
                    }
                }
            }
        }


    }

    fun removeSubscriberFromFireStore(
        categoryId: String,
        userId: String,
        callback: (Boolean) -> Unit,
    ) {
        val db = Firebase.firestore
        val categoryRef = db.collection("category").document(categoryId)

        categoryRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val categorySubscribersRef = document.getDocumentReference("subscribers")
                    categorySubscribersRef?.let {
                        val updates = hashMapOf<String, Any>(userId to FieldValue.delete())
                        it.update(updates)
                        callback(true)
                    }?: callback(false)
                }
            }
        }
    }

    fun getCategorySubscribers(categoryId: String, callback: (HashMap<String, String>) -> Unit) {
        val db = Firebase.firestore
        db.collection("category").document(categoryId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val categorySubscribersRef = document.getDocumentReference("subscribers")
                    categorySubscribersRef?.let { ref ->
                        ref.get().addOnSuccessListener {
                            it.data?.let { map ->
                                callback(map as HashMap<String, String>)
                            }
                        }
                    }
                }
            }

        }

    }
}