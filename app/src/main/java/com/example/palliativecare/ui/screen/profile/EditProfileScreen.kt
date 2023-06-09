package com.example.palliativecare.ui.screen.profile

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.palliativecare.R
import com.example.palliativecare.controller.profile.ProfileController
import com.example.palliativecare.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.jvm.Throws

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    profileController: ProfileController,
) {

    val context = LocalContext.current
    val currentUser = remember { mutableStateOf<User?>(null) }
    val editProfileProgress = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val birthdate = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val userType = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<Uri?>(null) }
    val selectedUserType = remember { mutableStateOf("") }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                image.value = uri
            }
        }
    )
    val firestore = Firebase.firestore
    val auth = Firebase.auth

    // Load user data from Firestore and update the `currentUser` state
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userRef = firestore.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                currentUser.value = user
                name.value = user?.name ?: ""
                phoneNumber.value = user?.phoneNumber ?: ""
                address.value = user?.address ?: ""
                birthdate.value = user?.birthdate ?: ""
                email.value = user?.email ?: ""
                userType.value = user?.userType ?: ""
                password.value = user?.password ?: ""
                selectedUserType.value = userType.value
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TopAppBar(
            title = { Text(text = "تعديل البيانات الشخصية") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }
            }
        )
        AsyncImage(
            model = if (image.value != null) image.value!! else currentUser.value?.image
                ?: R.drawable.ic_profile_placeholder,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(130.dp)
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
            onValueChange = {
                name.value = it
                currentUser.value?.name = it
            },
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
        Button(
            onClick = {
                editProfileProgress.value = true
                image.value?.let{
                    profileController.uploadImage(
                        imageUri = it,
                        onSuccess = { downloadUrl ->
                            Log.e("image", downloadUrl)
                            Toast.makeText(context, "Uploaded successful!", Toast.LENGTH_SHORT)
                                .show()
                            currentUser.value?.let { user ->
                                user.name = name.value
                                user.email = email.value
                                user.phoneNumber = phoneNumber.value
                                user.address = address.value
                                user.birthdate = birthdate.value
                                user.image = downloadUrl
                                user.userType = selectedUserType.value
                                editProfileProgress.value = true
                                profileController.updateProfileInFirestore(user) { _ ->
                                    editProfileProgress.value = false
                                    navController.popBackStack()
                                }
                            }

                        },
                        onFailure = { exception ->
                            Toast.makeText(
                                context,
                                "Upload failed $exception",
                                Toast.LENGTH_SHORT
                            ).show()
                            editProfileProgress.value = false
                        }
                    )
                }

                currentUser.value?.let { user ->
                    user.name = name.value
                    user.email = email.value
                    user.phoneNumber = phoneNumber.value
                    user.address = address.value
                    user.birthdate = birthdate.value
                    user.image = currentUser.value?.image!!
                    user.userType = selectedUserType.value
                    editProfileProgress.value = true
                    profileController.updateProfileInFirestore(user) { _ ->
                        editProfileProgress.value = false
                        navController.popBackStack()
                    }
                }





            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !editProfileProgress.value
        ) {
            if (editProfileProgress.value) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text("حفظ التعديلات")
            }
        }

    }
}