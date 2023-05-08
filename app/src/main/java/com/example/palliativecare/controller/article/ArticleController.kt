package com.example.palliativecare.controller.article

import android.net.Uri
import com.example.palliativecare.model.Article
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ArticleController {
    val db = Firebase.firestore

    fun addArticle(article: Article, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if(article.image == null || article.title.isBlank() || article.description.isBlank()){
            return
        }else{
            uploadImage(article.image, {}, {})
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


}