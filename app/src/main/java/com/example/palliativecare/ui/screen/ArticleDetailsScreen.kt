package com.example.palliativecare.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.R
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.controller.category.CategoryController
import com.example.palliativecare.model.Article
import com.example.palliativecare.model.ChatUser
import com.example.palliativecare.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsScreen(
    navController: NavController,
    articleController: ArticleController,
    id: String,
) {
    val image = remember { mutableStateOf<Uri?>(null) }
    val article = remember { mutableStateOf(Article()) }
    val doctor = remember { mutableStateOf<User?>(null) }
    val isUserSubscriber = remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser!!
    LaunchedEffect(Unit) {
        article.value = articleController.getArticleByID(id).first()
        doctor.value = User.getUserByID(article.value.doctorId).first()
        CategoryController().getCategorySubscribers(article.value.categoryId) { subscribersMap -> // [userID to userToken]
            isUserSubscriber.value = subscribersMap.keys.contains(currentUser.uid)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("comment_screen/$id")
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = "",
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            doctor.value?.let { doctor ->
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    AsyncImage(
                        model = article.value.picture,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .fillMaxHeight(0.3f)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    DoctorInfo(
                        user = ChatUser(
                            doctor.image,
                            doctor.name,
                            doctor.phoneNumber,
                            doctor.userType,
                            "12:00",
                            "1"
                        ),
                        isSubscribed = isUserSubscriber,
                        onClickUnSubscribe = {
                            CategoryController().removeSubscriberFromFireStore(
                                categoryId = article.value.categoryId,
                                userId = currentUser.uid,
                                ){success->
                                if (success){
                                    isUserSubscriber.value = false
                                }
                            }
                        }
                    ) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                            CategoryController().addCategorySubscribersInFireStore(
                                categoryId = article.value.categoryId,
                                newToken = it.result,
                            ) { success ->
                                if (success) {
                                    isUserSubscriber.value = true
                                }
                            }
                        }
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = article.value.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = article.value.description
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }

        }
    }


}

@Composable
fun DoctorInfo(
    user: ChatUser,
    isSubscribed: MutableState<Boolean>,
    onClickUnSubscribe: () -> Unit,
    onClickSubscribe: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user.image),
            contentDescription = "User Image",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = user.phone,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = if (isSubscribed.value) onClickUnSubscribe else onClickSubscribe,
        ) {
            Text(if (isSubscribed.value) "إلغاء المتابعة" else "متابعة")
        }
    }
}