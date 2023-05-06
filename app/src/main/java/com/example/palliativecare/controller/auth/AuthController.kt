package com.example.palliativecare.controller.auth

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.palliativecare.model.LoginUser
import com.example.palliativecare.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AuthController {

    fun registerUser(user: User, context: Context) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User created successfully, save the user data in Firestore
                    val userCollection = FirebaseFirestore.getInstance().collection("users")
                    userCollection.document(task.result?.user?.uid ?: "")
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Error saving user data: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun loginUser(user: LoginUser, onComplete: (Boolean, String?) -> Unit) {
        val email = user.email
        val password = user.password

        if (email.isEmpty() && password.isEmpty()) {
            onComplete(false, "Email&Password is required")
            return
        }
        if (email.isEmpty()) {
            onComplete(false, "Email is required")
            return
        }
        if (password.isEmpty()) {
            onComplete(false, "Password is required")
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    onComplete(false, errorMessage)
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

