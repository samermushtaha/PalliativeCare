package com.example.palliativecare.controller.article

import android.net.Uri
import com.example.palliativecare.model.Article
import com.example.palliativecare.model.Category
import com.example.palliativecare.model.ChatUser
import com.example.palliativecare.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ArticleController {
    val db = Firebase.firestore

    fun addArticle(article: Article, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (article.title.isBlank() || article.description.isBlank()) {
            return
        } else {
            db.collection("article")
                .add(article)
                .addOnSuccessListener { documentReference ->
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e.message.toString())
                }
        }
    }

    fun updateArticle(article: Article, id: String, onSuccess: () -> Unit, onFailure: (String) -> Unit){
        if (article.title.isBlank() || article.description.isBlank()) {
            return
        } else {
            val docRef = db.collection("article").document(id)

            val newArticle = hashMapOf<String, Any>()
            newArticle["description"] = article.description
            newArticle["title"] = article.title
            newArticle["categoryId"] = article.categoryId
            newArticle["picture"] = article.picture

            docRef.update(newArticle)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e.message.toString())
                }
        }
    }

    fun deleteArticle(id: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("article")
            .document(id)
            .delete()
            .addOnSuccessListener { documentReference ->
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message.toString())
            }
    }

    fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        if (imageUri == Uri.EMPTY) {
            onFailure(IllegalArgumentException("imageUri cannot be null or empty"))
            return
        }
        val storageRef =
            FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    suspend fun getAllArticle(): List<Article> {
        return db.collection("article").get().await().documents.map { documentSnapshot ->
            val article = documentSnapshot.toObject(Article::class.java)!!
            Article(
                title = article.title,
                picture = article.picture,
                description = article.description,
                doctorId = article.doctorId,
                categoryId = article.categoryId,
                createdAt = article.createdAt,
                id = documentSnapshot.id
            )
        }
    }

    suspend fun getArticleByCategory(categoryId: String): List<Article> {
        return db.collection("article").get().await().documents
            .filter { documentSnapshot ->
                documentSnapshot["categoryId"] == categoryId
            }.map { documentSnapshot ->
                val article = documentSnapshot.toObject(Article::class.java)!!
                Article(
                    title = article.title,
                    picture = article.picture,
                    description = article.description,
                    doctorId = article.doctorId,
                    categoryId = article.categoryId,
                    createdAt = article.createdAt,
                    id = documentSnapshot.id
                )
            }
    }

    suspend fun getMyArticle(doctorId: String): List<Article> {
        return db.collection("article").get().await().documents
            .filter { documentSnapshot ->
                documentSnapshot["doctorId"] == doctorId
            }.map { documentSnapshot ->
                val article = documentSnapshot.toObject(Article::class.java)!!
                Article(
                    title = article.title,
                    picture = article.picture,
                    description = article.description,
                    doctorId = article.doctorId,
                    categoryId = article.categoryId,
                    createdAt = article.createdAt,
                    id = documentSnapshot.id
                )
            }
    }

    suspend fun getArticleByID(id: String): List<Article> {
        return db.collection("article").get().await().documents
            .filter { documentSnapshot ->
                documentSnapshot.id == id
            }.map { documentSnapshot ->
                val article = documentSnapshot.toObject(Article::class.java)!!
                Article(
                    title = article.title,
                    picture = article.picture,
                    description = article.description,
                    doctorId = article.doctorId,
                    categoryId = article.categoryId,
                    createdAt = article.createdAt,
                    id = documentSnapshot.id
                )
            }
    }
}