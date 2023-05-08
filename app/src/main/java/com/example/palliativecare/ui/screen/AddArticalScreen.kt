package com.example.palliativecare.ui.screen

import android.net.Uri
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.palliativecare.R
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.model.Article
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArticleScreen(
    articleController: ArticleController,
    navController: NavController
) {
    val image = remember { mutableStateOf<Uri?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val isFailure = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val categoryId = remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    val createdAt = dateFormat.format(currentDate).toString()
    val options = listOf("فقر الدم", "السرطان", "امراض القلب")
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                image.value = uri
            }
        }
    )

    Column {
        TopAppBar(
            title = { Text(text = "اضافة مقالة") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            },
            actions = {
                Button(onClick = {
                    isLoading.value = true
                    articleController.addArticle(
                        Article(
                            image = image.value,
                            title = title.value,
                            description = description.value,
                            categoryId = categoryId.value,
                            createdAt = createdAt,
                            doctorId = "1"
                        ),
                        onSuccess = {
                            isLoading.value = false
                            navController.popBackStack()
                        },
                        onFailure = {
                            isLoading.value = false
                            isFailure.value = true
                            Toast.makeText(context, "حدث خطا ما", Toast.LENGTH_SHORT).show()
                        }
                    )
                }) {
                    if (isLoading.value) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = "اضافة")
                    }

                }
            }
        )
        AsyncImage(
            model = image.value ?: R.drawable.topic,
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
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spinner(
            selectedOptionText = categoryId.value,
            options = options,
            onSelectedItem = {
                categoryId.value = it
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    selectedOptionText: String,
    options: List<String>,
    onSelectedItem: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text("المواضيع") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded.value
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        onSelectedItem(selectionOption)
                        expanded.value = false
                    },
                    text = {
                        Text(text = selectionOption)
                    }
                )
            }
        }
    }
}