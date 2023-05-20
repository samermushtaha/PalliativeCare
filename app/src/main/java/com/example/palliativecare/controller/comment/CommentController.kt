package com.example.palliativecare.controller.comment

import com.example.palliativecare.model.Article
import com.example.palliativecare.model.Comment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class CommentController {
    val db = Firebase.firestore

    fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (comment.title.isBlank()) {
            return
        } else {
            db.collection("comment")
                .add(comment)
                .addOnSuccessListener { documentReference ->
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e.message.toString())
                }
        }
    }

    suspend fun getComments(articleId: String): List<Comment> {
        return db.collection("comment").get().await().documents
            .filter { documentSnapshot ->
                documentSnapshot["articleId"] == articleId
            }.map { documentSnapshot ->
                val comment = documentSnapshot.toObject(Comment::class.java)!!
                Comment(
                    title = comment.title,
                    userId = comment.userId,
                    articleId = comment.articleId,
                    dateTime = comment.dateTime
                )
            }
    }
}