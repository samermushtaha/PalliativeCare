package com.example.palliativecare.ui.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.palliativecare.R
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.controller.category.CategoryController
import com.example.palliativecare.controller.notifications.NotificationController
import com.example.palliativecare.model.Article
import com.example.palliativecare.model.Category
import com.example.palliativecare.model.NotificationData
import com.example.palliativecare.model.PushNotification
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArticleScreen(
    articleController: ArticleController,
    categoryController: CategoryController,
    navController: NavController,
) {
    val image = remember { mutableStateOf<Uri?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val isFailure = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
//    val categoryId = remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    val createdAt = dateFormat.format(currentDate).toString()
    var selectedTopic = remember { mutableStateOf<Category>(Category("1", "اختر التصنيف")) }
    val topics = remember { mutableStateListOf<Category>() }
    val scope = rememberCoroutineScope()
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                image.value = uri
            }
        }
    )

    LaunchedEffect(Unit) {
        topics.addAll(categoryController.getAllCategory())
    }
    fun sendNotificationToSubscribers(category: Category) {
        CategoryController().getCategorySubscribers(category.id) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    it.values.forEach { token ->
                        NotificationController.sendNotification(
                            PushNotification(
                                notification = NotificationData(
                                    title = "تم نشر مقالة جديدة",
                                    body = " مقالة جديدة في موضوع ${category.name}"
                                ),
                                to = token
                            )
                        )
                    }
                }
                isLoading.value = false
                navController.popBackStack()
            }
        }
    }


    fun onClick() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        image.value?.let { imageUri ->
            isLoading.value = true
            articleController.uploadImage(
                imageUri = imageUri,
                onSuccess = { uri ->
                    articleController.addArticle(
                        Article(
                            title = title.value,
                            description = description.value,
                            categoryId = selectedTopic.value.id,
                            createdAt = createdAt,
                            doctorId = currentUser?.uid.toString(),
                            picture = uri
                        ),
                        onSuccess = {
                            sendNotificationToSubscribers(selectedTopic.value)
                        },
                        onFailure = {
                            isLoading.value = false
                            isFailure.value = true
                            Toast.makeText(context, "حدث خطا ما", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onFailure = { Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show() }
            )

        } ?: Toast.makeText(context, "يرجى اختيار صورة", Toast.LENGTH_SHORT).show()

    }

    Column {
        TopAppBar(
            title = { Text(text = "اضافة مقالة") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }
            },
            actions = {
                Button(onClick = {
                    onClick()
                }) {
                    if (isLoading.value) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(text = "اضافة")
                    }

                }
            }
        )
        AsyncImage(
            model = image.value ?: R.drawable.add_a_photo,
            contentDescription = "Profile picture",
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .padding(horizontal = 16.dp),
            contentScale = if (image.value != null) ContentScale.Crop else ContentScale.None
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text(text = "العنوان") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .padding(horizontal = 16.dp),
            placeholder = { Text(text = "وصف المقالة") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropdownMenuExample(
            selectedItem = selectedTopic.value,
            items = topics,
            onSelectedItem = {
                selectedTopic.value.name = it
            },
            onSelectedItem2 = {
                selectedTopic.value = it
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuExample(
    selectedItem: Category,
    items: List<Category>,
    onSelectedItem: (String) -> Unit,
    onSelectedItem2: (Category) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            readOnly = true,
            value = if (selectedItem.name.isNotEmpty()) selectedItem.name else "اختر التصنيف",
            onValueChange = { onSelectedItem(it) },
            label = { Text("التصنيف") },
            trailingIcon = {
                IconButton(onClick = { expanded.value = !expanded.value }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "")
                }
            }
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.name) },
                    onClick = {
                        onSelectedItem2(item)
                        expanded.value = false
                    })
            }
        }
    }
}
