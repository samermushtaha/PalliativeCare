package com.example.palliativecare

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.controller.auth.AuthController
import com.example.palliativecare.controller.category.CategoryController
import com.example.palliativecare.controller.chat.ChatController
import com.example.palliativecare.controller.chat.ChatDetailsController
import com.example.palliativecare.controller.comment.CommentController
import com.example.palliativecare.controller.profile.ProfileController
import com.example.palliativecare.model.User
import com.example.palliativecare.ui.screen.AddArticleScreen
import com.example.palliativecare.ui.screen.ArticleDetailsScreen
import com.example.palliativecare.ui.screen.CommentScreen
import com.example.palliativecare.ui.screen.EditArticleScreen
import com.example.palliativecare.ui.screen.MainScreen
import com.example.palliativecare.ui.screen.auth.LoginScreen
import com.example.palliativecare.ui.screen.auth.RegisterScreen
import com.example.palliativecare.ui.screen.chat.ChatDetailsScreen
import com.example.palliativecare.ui.screen.chat.ChatScreen
import com.example.palliativecare.ui.screen.profile.EditProfileScreen
import com.example.palliativecare.ui.theme.PalliativeCareTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("my_app", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {token->
            FirebaseAuth.getInstance().currentUser?.uid?.let { uid->
                lifecycleScope.launch {
                    val db = Firebase.firestore
                    val userRef = db.collection("users").document(uid)
                    val updates = hashMapOf("token" to token.result)
                    userRef.update(updates as Map<String, String>)
                }
            }
        }
        setContent {
            PalliativeCareTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MyNavHost(navHostController = navController, preferences)
                    }
                }
            }
        }
    }
}


@Composable
fun MyNavHost(navHostController: NavHostController, preferences: SharedPreferences) {
    val isLoggedIn = preferences.getBoolean("isLoggedIn", false)
    NavHost(
        navController = navHostController,
        startDestination = if (isLoggedIn) "main_screen" else "login_screen"
    ) {
        composable(route = "login_screen") {
            LoginScreen(navHostController, loginController = AuthController(), preferences)
        }
        composable(route = "register_screen") {
            RegisterScreen(navHostController, registerController = AuthController())
        }
        composable(route = "main_screen") {
            MainScreen(navHostController, preferences)
        }
        composable(route = "chat_screen") {
            ChatScreen(navHostController, ChatController())
        }
///////////////////////////////////////////////////////////////////////////////
        val cUser = FirebaseAuth.getInstance().currentUser

        composable(
            route = "chat_details_screen/{userId}?name={name}&phone={phone}&userType={userType}&image={image}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType },
                navArgument("image") { type = NavType.StringType },
                navArgument("userType") { type = NavType.StringType }
            )
        ) { entry ->
            val userId = entry.arguments?.getString("userId")
            val name = entry.arguments?.getString("name")
            val phone = entry.arguments?.getString("phone")
            val userType = entry.arguments?.getString("userType")
            val image = entry.arguments!!.getString("image")

            val currentUser = ChatDetailsController.User(
                id = cUser!!.uid,
                name = cUser.displayName.toString(),
                phone = cUser.phoneNumber.toString(),
                image = "cUserImage"
            )

            val selectedUser = ChatDetailsController.User(
                id = userId ?: "",
                name = name ?: "",
                phone = phone ?: "",
                image = image ?: ""
            )

            val chatController = ChatDetailsController(currentUser, selectedUser)

            ChatDetailsScreen(
                navController = navHostController,
                chatController = chatController,
                id = userId,
                name = name,
                phone = phone,
            )
        }

        composable(route = "article_details_screen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ArticleDetailsScreen(navHostController, ArticleController(), id.toString())
        }
        composable(route = "comment_screen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            CommentScreen(navHostController, CommentController(), id.toString())
        }
        composable(route = "edit_profile_screen") {
            EditProfileScreen(navHostController, ProfileController())
        }
        composable(route = "add_article_screen") {
            AddArticleScreen(
                articleController = ArticleController(),
                navController = navHostController,
                categoryController = CategoryController()
            )
        }
        composable(route = "edit_article_screen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            EditArticleScreen(
                articleController = ArticleController(),
                navController = navHostController,
                categoryController = CategoryController(),
                articleId = id.toString()
            )
        }
    }
}