package com.example.palliativecare.controller.profile

import android.net.Uri
import android.util.Log
import com.example.palliativecare.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class ProfileController {

    fun getCurrentUserFromFirebase(onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            onFailure(Exception("No user currently logged in"))
            return
        }

        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users").document(currentUser.uid)

        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val password = document.getString("password") ?: ""
                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    val address = document.getString("address") ?: ""
                    val birthdate = document.getString("birthdate") ?: ""
                    val image = document.get("image").toString()
                    val userType = document.getString("userType") ?: ""

                    val currentUser = User(
                        name,
                        email,
                        password,
                        phoneNumber,
                        address,
                        birthdate,
                        image,
                        userType
                    )
                    onSuccess(currentUser)
                } else {
                    onFailure(Exception("User not found in Firestore"))
                }
            } else {
                onFailure(task.exception ?: Exception("Unknown error occurred"))
            }
        }
    }

    fun logout(onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().signOut()
        onComplete(true)
    }

        // This function updates the user's profile data in Firestore
        fun updateProfileInFirestore(user: User, callback: (Boolean) -> Unit) {
            val db = Firebase.firestore
            val auth = Firebase.auth

            // Get the ID of the currently authenticated user
            val userId = auth.currentUser?.uid

            if (userId != null) {
                // Get a reference to the user's document in Firestore
                val userRef = db.collection("users").document(userId)

                // Create a map with the updated user data
                val userData = mapOf(
                    "name" to user.name,
                    "email" to user.email,
                    "phoneNumber" to user.phoneNumber,
                    "address" to user.address,
                    "birthdate" to user.birthdate,
                    "image" to user.image,
                    "userType" to user.userType
                )

                // Update the user document in Firestore
                userRef.update(userData)
                    .addOnSuccessListener {
                        callback(true) // update successful
                    }
                    .addOnFailureListener {
                        callback(false) // update failed
                    }
            } else {
                callback(false) // user not authenticated
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