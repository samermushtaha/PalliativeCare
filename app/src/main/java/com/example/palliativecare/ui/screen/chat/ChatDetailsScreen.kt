package com.example.palliativecare.ui.screen.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.R
import com.example.palliativecare.controller.chat.ChatDetailsController
import com.example.palliativecare.controller.notifications.NotificationController
import com.example.palliativecare.model.NotificationData
import com.example.palliativecare.model.PushNotification
import com.example.palliativecare.model.User
import com.example.palliativecare.ui.LoadingScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailsScreen(
    navController: NavController,
    chatController: ChatDetailsController,
    id: String?,
    name: String?,
    phone: String?,
) {
    var messages by remember { mutableStateOf(emptyList<ChatDetailsController.Message>()) }
    val isLoading = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val currentUser = remember { mutableStateOf<User?>(null) }
    val receiver = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        chatController.observeMessages(
            messagesObserver = { updatedMessages ->
                messages = updatedMessages
            },
            newMessageObserver = { newMessage ->
                messages += newMessage
            }
        )
        id?.let {
            receiver.value = User.getUserByID(it).first()
        }
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            currentUser.value = User.getUserByID(it).first()
        }
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
                    receiver.value?.let {
                        Image(
                            painter = rememberAsyncImagePainter(model = it.image),
                            contentDescription = "User Image",
                            modifier = Modifier
                                .size(55.dp)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(2.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
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
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 0.2f
                )
            )
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            reverseLayout = true
        ) {
            items(messages.sortedByDescending { it.timestamp }) { message ->
                if (message.senderId == chatController.currentUser.id) {
                    ChatBubbleSender(message)
                } else {
                    ChatBubbleReceiver(message)
                }
            }
        }
        LoadingScreen(visibility = isLoading.value)


        MessageInput(chatController, onMessageSent = { message ->
            if (message.isNotBlank()) {
                chatController.sendMessage(message)
                scope.launch {
                    receiver.value?.let {
                        NotificationController.sendNotification(
                            PushNotification(
                                notification = NotificationData(
                                    title = "رسالة من ${currentUser.value!!.name}",
                                    body = message,
                                    image = currentUser.value?.image
                                ),
                                to = it.token,

                                )
                        )
                    }


                }


            }
        })
    }
}

@Composable
fun ChatBubbleReceiver(message: ChatDetailsController.Message) {
    val timestamp = message.timestamp.toString()
    val messageText = message.text
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
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
                )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = messageText,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End
                )
                if (message.imageUrl.isNotBlank()) {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(0.65f),
                        model = message.imageUrl,
                        contentDescription = "")
                }
                Text(
                    text = formattedTimestamp,
                    color = Color(0xFF333333),
                    fontSize = 10.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun ChatBubbleSender(message: ChatDetailsController.Message) {
    val timestamp = message.timestamp.toString()
    val messageText = message.text
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
//                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)
                )
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = messageText,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.White,
                    fontSize = 14.sp
                )
                if (message.imageUrl.isNotBlank()) {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(0.65f),
                        model = message.imageUrl,
                        contentDescription = "")
                }
                Text(
                    text = formattedTimestamp,
                    color = Color(0xFFE1E4EB),
                    fontSize = 10.sp
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    chatController: ChatDetailsController,
    onMessageSent: (String) -> Unit = {},
) {
    val message = remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { selectedUri ->
            if (selectedUri != null) {
                selectedImageUri.value = selectedUri
            }
        }
    )

    Column {
        AnimatedVisibility(visible = selectedImageUri.value != null) {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 4.dp), contentAlignment = Alignment.CenterEnd) {
                AsyncImage(
                    model = selectedImageUri.value,
                    contentDescription = "",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(modifier= Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),onClick = { selectedImageUri.value = null }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Color.White)
                }
            }

        }
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
            placeholder = {
                Text(
                    text = "اكتب رسالة..",
                    color = Color.Black.copy(alpha = 0.5f),
                    fontSize = 16.sp
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (selectedImageUri.value != null) {
                            selectedImageUri.value?.let { uri ->
                                chatController.uploadImage(uri, {
                                    chatController.sendMessageWithImage(message.value, it)
                                    selectedImageUri.value = null
                                    message.value = ""
                                }, {})
                            }
                        } else {
                            onMessageSent(message.value)
                            message.value = ""
                        }


                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send Message"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_attach),
                        contentDescription = "attachment"
                    )

                }
            }
        )
    }
}
