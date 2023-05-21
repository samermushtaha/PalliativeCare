package com.example.palliativecare.controller.chat

import com.example.palliativecare.model.ChatUser
import com.example.palliativecare.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatController {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllUsers(currentUserId: String): List<ChatUser> {
        val usersCollection = firestore.collection("users")
        val querySnapshot = usersCollection.get().await()
        return querySnapshot.documents
            .filter { it.id != currentUserId } // Filter out the current user
            .map { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)!!
                ChatUser(
                    image = user.image,
                    name = user.name,
                    phone = user.phoneNumber,
                    userType = user.userType,
                    lastMessageDate = "",
                    id = documentSnapshot.id // Retrieve the document ID as the user ID
                )
            }
    }

}