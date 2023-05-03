package com.example.palliativecare.ui.screen

import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.palliativecare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArticleScreen() {
    val image = remember { mutableStateOf<Uri?>(null) }

    Column {
        TopAppBar(
            title = { Text(text = "اضافة مقالة") },
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            },
            actions = {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "اضافة")
                }
            }
        )
        AsyncImage(
            model = image.value ?: R.drawable.topic,
            contentDescription = "Profile picture",
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentScale = ContentScale.Crop
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

    }
}