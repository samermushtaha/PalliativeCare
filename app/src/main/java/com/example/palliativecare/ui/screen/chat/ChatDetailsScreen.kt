package com.example.palliativecare.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.palliativecare.controller.chat.ChatDetailsController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailsScreen(
    navController: NavController,
    chatController: ChatDetailsController,
    userId: String?,
    name: String?,
    phone: String?,
    userType: String?,
    image: String?,
) {
    var messages by remember { mutableStateOf(emptyList<ChatDetailsController.Message>()) }

// Observe the messages
    LaunchedEffect(Unit) {
        chatController.observeMessages(
            messagesObserver = { updatedMessages ->
                // Update the state variable with the received messages
                messages = updatedMessages
            },
            newMessageObserver = { newMessage ->
                // Handle the new message
                // For example, add it to the UI or scroll to the bottom
                messages += newMessage // Append the new message to the list of messages
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF333333))
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Image(
//                        painter = rememberAsyncImagePainter(model = image),
//                        contentDescription = "User Image",
//                        modifier = Modifier
//                            .size(40.dp)
//                            .clip(CircleShape)
//                    )
                    Column() {
                        Text(
                            text = name.toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = phone.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            reverseLayout = true
        ) {
            items(messages.sortedByDescending { it.timestamp }) { message ->
                if (message.senderId == chatController.currentUser.id) {
                    ChatBubbleSender(
                        message = message.text,
                        timestamp = message.timestamp.toString()
                    )
                } else {
                    ChatBubbleReceiver(
                        message = message.text,
                        timestamp = message.timestamp.toString()
                    )
                }
            }
        }

//
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(4.dp),
//            reverseLayout = true
//        ) {
//            items(messages.size) { index ->
//                val message = messages[index]
//                if (message.senderId == chatController.currentUser.id) {
//                    ChatBubbleSender(
//                        message = message.text,
//                        timestamp = message.timestamp.toString()
//                    )
//                } else {
//                    ChatBubbleReceiver(
//                        message = message.text,
//                        timestamp = message.timestamp.toString()
//                    )
//                }
//            }
//        }

        MessageInput(onMessageSent = { message ->
            if (message.isNotBlank()) {
                chatController.sendMessage(message)
            }
        })
    }
}

@Composable
fun ChatBubbleReceiver(message: String, timestamp: String) {
    val timestampRegex = "Timestamp\\(seconds=(\\d+),".toRegex()
    val matchResult = timestampRegex.find(timestamp)
    val seconds = matchResult?.groupValues?.getOrNull(1)?.toLongOrNull()

    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTimestamp = if (seconds != null) {
        val date = Date(seconds * 1000) // Convert seconds to milliseconds
        dateFormat.format(date)
    } else {
        ""
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(color = Color.White, shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.Black
                )
                Text(
                    text = formattedTimestamp,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChatBubbleSender(message: String, timestamp: String) {
    val timestampRegex = "Timestamp\\(seconds=(\\d+),".toRegex()
    val matchResult = timestampRegex.find(timestamp)
    val seconds = matchResult?.groupValues?.getOrNull(1)?.toLongOrNull()

    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTimestamp = if (seconds != null) {
        val date = Date(seconds * 1000) // Convert seconds to milliseconds
        dateFormat.format(date)
    } else {
        ""
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
                )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.Black
                )
                Text(
                    text = formattedTimestamp,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    onMessageSent: (String) -> Unit,
) {
    var message by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Gray,
                    cursorColor = Color.Gray,
                    placeholderColor = Color.Gray,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    onMessageSent(message)
                    message = ""
                }),
                placeholder = { Text(text = "اكتب رسالة...") }
            )

            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
                content = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                        content = {
                            IconButton(
                                onClick = {
                                    onMessageSent(message)
                                    message = ""
                                },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }
            )

        }
    }
}