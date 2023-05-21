package com.example.palliativecare.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.controller.comment.CommentController
import com.example.palliativecare.model.Article
import com.example.palliativecare.model.Comment
import com.example.palliativecare.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(navController: NavController, commentController: CommentController, id: String) {
    val comment = remember { mutableStateOf("") }
    val comments = remember { mutableStateListOf<Comment>() }
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    val createdAt = dateFormat.format(currentDate).toString()

    LaunchedEffect(Unit) {
        comments.addAll(commentController.getComments(id))
    }

    Column {
        TopAppBar(
            title = { Text(text = "التعليقات") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }
            }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(comments) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val user = remember { mutableStateOf(User()) }
                    LaunchedEffect(Unit) {
                        user.value = User.getUserByID(it.userId).first()
                    }
                    Image(
                        painter = rememberAsyncImagePainter(model = user.value.image),
                        contentDescription = "User Image",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column() {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.value.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                it.dateTime,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }
        }

        TextField(
            value = comment.value,
            onValueChange = { comment.value = it },
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
            leadingIcon = {
                IconButton(
                    onClick = {
                        commentController.addComment(
                            Comment(
                                title = comment.value,
                                userId = FirebaseAuth.getInstance().currentUser!!.uid,
                                articleId = id,
                                dateTime = createdAt
                            ),
                            {
                            comments.add(it)
                            },
                            {}
                        )
                        comment.value = ""
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
}