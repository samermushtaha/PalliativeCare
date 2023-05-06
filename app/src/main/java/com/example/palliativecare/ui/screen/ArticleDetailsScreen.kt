package com.example.palliativecare.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.R
import com.example.palliativecare.model.ChatUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsScreen() {
    val image = remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    //OnClick Method
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
            Column {
                AsyncImage(
                    model = image.value ?: R.drawable.topic,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                DoctorInfo(
                    ChatUser(
                        "https://randomuser.me/api/portraits/men/1.jpg",
                        "د. سامر مشتهى",
                        "059212665",
                    )
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "داء السكري مرض مزمن يحدث عندما يعجز البنكرياس عن إنتاج الإنسولين بكمية كافية، أو عندما يعجز الجسم عن الاستخدام الفعال للإنسولين الذي ينتجه. والإنسولين هو هرمون يضبط مستوى الغلوكوز في الدم. ويُعد فرط السكر في الدم، الذي يعرف أيضا بارتفاع مستوى الغلوكوز في الدم، من النتائج الشائعة الدالة على خلل في ضبط مستوى السكر في الدم، ويؤدي مع مرور الوقت إلى الإضرار الخطير بالعديد من أجهزة الجسم، ولاسيما الأعصاب والأوعية الدموية."
                )
            }
        }
    }


}

@Composable
fun DoctorInfo(user: ChatUser) {
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
            onClick = { },
        ) {
            Text("تواصل معي")
        }
    }
}