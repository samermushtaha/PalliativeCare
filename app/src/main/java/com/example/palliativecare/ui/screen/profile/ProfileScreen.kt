package com.example.palliativecare.ui.screen.profile

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.palliativecare.R
import com.example.palliativecare.controller.profile.ProfileController
import com.example.palliativecare.model.User

@Composable
fun ProfileScreen(
    navController: NavController,
    profileController: ProfileController,
    preferences: SharedPreferences
) {
    val context = LocalContext.current
    val profileProgress = remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }


    LaunchedEffect(Unit) {
        profileProgress.value = true
        profileController.getCurrentUserFromFirebase(
            onSuccess = { user ->
                profileProgress.value = false
                currentUser = user
                Log.e("msg", "${currentUser?.image}")
            },
            onFailure = { exception ->
                profileProgress.value = false
                // Handle the error
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (profileProgress.value) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            AsyncImage(
                model = "${currentUser?.image ?: R.drawable.ic_profile_placeholder}",
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                ,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text =
                "${currentUser?.name}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "${currentUser?.userType}",
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.surfaceTint)
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("edit_profile_screen")
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "تعديل بيانات الحساب")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = "",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                profileController.logout { success ->
                    if (success) {
                        preferences.edit {
                            putBoolean("isLoggedIn", false)
                        }
                        navController.navigate("login_screen") // navigate to login screen after logout
                    } else {
                        Toast.makeText(context, "Logout failed", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("تسجيل الخروج")
        }
    }
}
