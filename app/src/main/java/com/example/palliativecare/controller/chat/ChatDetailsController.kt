package com.example.palliativecare.controller.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatDetailsController(
    val currentUser: User,
    private val selectedUser: User

) {
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val messagesCollectionRef = firestore.collection("messages")

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>>
        get() = _messages

    // Sends a message to the selected user
    fun sendMessage(messageText: String) {
        val message = Message(
            senderId = currentUser.id,
            receiverId = selectedUser.id,
            text = messageText,
            timestamp = Timestamp.now()
        )

        messagesCollectionRef.add(message)
            .addOnSuccessListener { documentRef ->
                documentRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        val sentMessage = documentSnapshot.toObject(Message::class.java)
                        sentMessage?.let {
                            val currentMessages = _messages.value.orEmpty().toMutableList()
                            currentMessages.add(it)
                            _messages.value = currentMessages.toList()
                        }
                    }
            }
    }


    fun observeMessages(
        messagesObserver: (List<Message>) -> Unit,
        newMessageObserver: (Message) -> Unit
    ) {
        getMessages().addOnSuccessListener { querySnapshots ->
            val messages = mutableListOf<Message>()

            for (querySnapshot in querySnapshots) {
                for (document in querySnapshot) {
                    val message = document.toObject(Message::class.java)
                    messages.add(message)
                }
            }

            messagesObserver(messages)
        }.addOnFailureListener { exception ->
            // Handle the failure case here
            // For example, display an error message or log the error
        }

        // Observe for new messages
        messagesCollectionRef.addSnapshotListener { querySnapshots, exception ->
            if (exception != null) {
                // Handle the error case here
                // For example, display an error message or log the error
                return@addSnapshotListener
            }

            querySnapshots?.let { snapshots ->
                for (documentChange in snapshots.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val newMessage = documentChange.document.toObject(Message::class.java)
                        newMessageObserver(newMessage)
                    }
                }
            }
        }
    }


    private fun getMessages(): Task<MutableList<QuerySnapshot>> {
        val currentUserMessagesQuery = messagesCollectionRef
            .whereEqualTo("senderId", currentUser.id)
            .whereEqualTo("receiverId", selectedUser.id)

        val selectedUserMessagesQuery = messagesCollectionRef
            .whereEqualTo("senderId", selectedUser.id)
            .whereEqualTo("receiverId", currentUser.id)

        // Combine the queries using a logical OR operation
        val currentUserMessagesTask = currentUserMessagesQuery.get()
        val selectedUserMessagesTask = selectedUserMessagesQuery.get()

        return Tasks.whenAllSuccess<QuerySnapshot>(
            currentUserMessagesTask,
            selectedUserMessagesTask
        )
            .addOnSuccessListener { results ->
                val messages = mutableListOf<Message>()

                for (result in results) {
                    val documents = result as QuerySnapshot
                    for (document in documents) {
                        val message = document.toObject(Message::class.java)
                        messages.add(message)
                    }
                }

                _messages.value = messages
            }
    }


    data class User(val id: String, val name: String, val phone: String, val image: String)

    data class Message(
        val senderId: String = "",
        val receiverId: String = "",
        val text: String = "",
        val timestamp: Timestamp = Timestamp.now()
    )
}
