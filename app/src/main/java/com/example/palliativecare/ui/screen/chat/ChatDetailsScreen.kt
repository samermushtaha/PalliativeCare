package com.example.palliativecare.ui.screen.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.controller.chat.ChatDetailsController
import com.example.palliativecare.model.Comment
import com.example.palliativecare.ui.LoadingScreen
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailsScreen(
    navController: NavController,
    chatController: ChatDetailsController,
    name: String?,
    phone: String?,
) {
    var messages by remember { mutableStateOf(emptyList<ChatDetailsController.Message>()) }
    val isLoading = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        chatController.observeMessages(
            messagesObserver = { updatedMessages ->
                messages = updatedMessages
            },
            newMessageObserver = { newMessage ->
                messages += newMessage
            }
        )
        isLoading.value = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
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
        LoadingScreen(visibility = isLoading.value)


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

    val dateFormat = SimpleDateFormat("h:mm a", Locale("ar"))
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
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)
                )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.Black,
                    fontSize = 14.sp
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

    val dateFormat = SimpleDateFormat("h:mm a", Locale("ar"))
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
                    color = Color.White,
                    fontSize = 14.sp
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
    val message= remember { mutableStateOf("") }

    TextField(
        value = message.value,
        onValueChange = { message.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = CircleShape,
        colors = TextFieldDefaults.textFieldColors(
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = {
            onMessageSent(message.value)
            message.value = ""
        }),
        leadingIcon = {
            IconButton(
                onClick = {
                    onMessageSent(message.value)
                    message.value = ""
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send Message"
                )
            }
        }
    )
}