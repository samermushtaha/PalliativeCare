package com.example.palliativecare.ui.screen.auth

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.palliativecare.R
import com.example.palliativecare.controller.auth.AuthController
import com.example.palliativecare.model.User
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    registerController: AuthController,
) {
    val context = LocalContext.current
    val registrationInProgress = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val birthdate = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<Uri?>(null) }
    val selectedUserType = remember { mutableStateOf(0) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                image.value = uri
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AsyncImage(
            model = image.value ?: R.drawable.ic_profile_placeholder,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentScale = ContentScale.Crop
        )

        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("الاسم كامل") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("الايميل") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            label = { Text("رقم الجوال") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = birthdate.value,
            onValueChange = { birthdate.value = it },
            label = { Text("تاريخ الميلاد") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address.value,
            onValueChange = { address.value = it },
            label = { Text("العنوان") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("كلمة السر") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            label = { Text("تاكيد كلمة السر") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = " نوع المستخدم (مريض / طبيب)")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,) {
                RadioButton(selected = selectedUserType.value == 0, onClick = {
                    selectedUserType.value = 0
                })
                Text(text = "طبيب", modifier = Modifier.padding(horizontal = 4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically,) {
                RadioButton(selected = selectedUserType.value == 1, onClick = {
                    selectedUserType.value = 1
                })
                Text(text = "مريض", modifier = Modifier.padding(horizontal = 4.dp))
            }
        }

        Button(
            onClick = {
                image.value?.let { imageUri ->
                    registrationInProgress.value = true
                    registerController.uploadImage(
                        imageUri = imageUri,
                        onSuccess = { downloadUrl ->
                            Toast.makeText(context, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show()
                            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                val user = User(
                                    email = email.value,
                                    name = name.value,
                                    password = password.value,
                                    phoneNumber = phoneNumber.value,
                                    address = address.value,
                                    birthdate = birthdate.value,
                                    image = downloadUrl,
                                    userType = if (selectedUserType.value == 0) "طبيب" else "مريض",
                                    token = it.result
                                )
                                registerController.registerUser(user, context)
                                registrationInProgress.value = false
                            }

                        },
                        onFailure = { _ ->
                            Toast.makeText(context, "فشل التسجيل", Toast.LENGTH_SHORT).show()
                            registrationInProgress.value = false
                        }
                    )
                }?: Toast.makeText(context, "يرجى اختيار صورة", Toast.LENGTH_SHORT).show()


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            if (registrationInProgress.value) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                Text("تسجيل")
            }
        }


        Spacer(Modifier.weight(1f))

        TextButton(
            onClick = { navController.navigate("login_screen") },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("لدي حساب بلفعل؟ تسجيل دخول")
        }
    }
}

