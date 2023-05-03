package com.example.palliativecare.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.palliativecare.model.User

@Composable
fun ChatScreen() {
    val userList = listOf(
        User(
            name = "سامر مشتهى",
            phone = "0592121665",
            image = "https://randomuser.me/api/portraits/men/1.jpg"
        ),
        User(
            name = "محمود حبيب",
            phone = "0592121665",
            image = "https://randomuser.me/api/portraits/women/2.jpg"
        ),
        User(
            name = "بهاء الرملاوي",
            phone = "0592121665",
            image = "https://randomuser.me/api/portraits/men/3.jpg"
        ),
        User(
            name = "حسام شعبان",
            phone = "0592121665",
            image = "https://randomuser.me/api/portraits/women/4.jpg"
        )
    )

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(userList) { user ->
            UserListItem(user = user)
        }
    }
}

@Composable
fun UserListItem(user: User) {
    Row(
        modifier = Modifier.clickable {  },
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
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = user.lastMessageDate,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = user.phone,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
            )
        }
    }
}
