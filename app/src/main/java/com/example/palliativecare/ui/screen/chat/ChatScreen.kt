package com.example.palliativecare.ui.screen.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.controller.chat.ChatController
import com.example.palliativecare.model.ChatUser
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatController: ChatController

) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val users = remember { mutableStateListOf<ChatUser>() }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val fetchedUsers = currentUser?.let { chatController.getAllUsers(it.uid) }
        if (fetchedUsers != null) {
            users.addAll(fetchedUsers)
        }
    }

//    val currentUser = FirebaseAuth.getInstance().currentUser
//    val users = remember { mutableStateListOf<ChatUser>() }
//    val searchQuery = remember { mutableStateOf("") }
//
//    LaunchedEffect(Unit) {
//        val fetchedUsers = currentUser?.let { chatController.getAllUsers(it.uid) }
//        if (fetchedUsers != null) {
//            val currentUserType = fetchedUsers.firstOrNull()?.userType
//            val filteredUsers = if (currentUserType == "مريض") {
//                fetchedUsers.filter { it.userType == "مريض" }
//            } else {
//                fetchedUsers.filter { it.userType == "طبيب" }
//            }
//            users.addAll(filteredUsers)
//        }
//    }


    val filteredUsers = users.filter {
        it.name.contains(searchQuery.value, ignoreCase = true) ||
                it.phone.contains(searchQuery.value, ignoreCase = true)
    }

    Column {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(min = 48.dp),
            placeholder = {
                Text(
                    text = "بحث ...",
                    color = Color.Gray,
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Clear",
                        tint = Color.Gray
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { }
            ),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredUsers) { user ->
                UserListItem(user = user, navController)
            }
        }
    }
}

@Composable
fun UserListItem(user: ChatUser, navController: NavController) {
    Row(
        modifier = Modifier.clickable {
            navController.navigate(
                "chat_details_screen/${user.id}" +
                        "?name=${user.name}" +
                        "&phone=${user.phone}" +
                        "&userType=${user.userType}" +
                        "&image=${user.image}"

            )
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user.image),
            contentDescription = "User Image",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = user.lastMessageDate,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.phone,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = user.userType,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}
