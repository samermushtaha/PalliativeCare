package com.example.palliativecare.ui.screen.auth

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.palliativecare.R
import com.example.palliativecare.controller.auth.AuthController
import com.example.palliativecare.model.LoginUser
import com.example.palliativecare.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginController: AuthController,
    preferences: SharedPreferences
) {
    val context = LocalContext.current
    val loginProgress = remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Define a state variable to track whether the login was successful
    var isLoggedIn by remember { mutableStateOf(false) }

    // Define a function to handle the login button click
    fun handleLoginButtonClick(preferences: SharedPreferences) {
        // Create a LoginUser object with the email and password
        val loginUser = LoginUser(email = email, password = password)
        loginProgress.value = true

        // Call the login method on the loginController
        loginController.loginUser(loginUser) { success, errorMessage ->
            loginProgress.value = false
            // Update the "isLoggedIn" preference
            preferences.edit {
                putBoolean("isLoggedIn", true)
            }
            if (success) {
                isLoggedIn = true
                navController.navigate("main_screen")
                navController.navigate("main_screen")
            } else {
                Toast.makeText(context, "Login failed $errorMessage", Toast.LENGTH_LONG).show()
                loginProgress.value = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .padding(16.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد الالكتروني") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "")
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة السر") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "")
            },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                handleLoginButtonClick(preferences)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (loginProgress.value) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                Text("تسجيل دخول")
            }
        }


        Spacer(Modifier.weight(1f))

        TextButton(
            onClick = { navController.navigate("register_screen") },
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {
            Text("ليس لديك حساب؟ انشاء حساب")
        }
    }
}

