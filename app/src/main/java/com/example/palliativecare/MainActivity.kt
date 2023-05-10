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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.controller.auth.AuthController
import com.example.palliativecare.controller.profile.ProfileController
import com.example.palliativecare.model.Screen
import com.example.palliativecare.model.User
import com.example.palliativecare.ui.screen.AddArticleScreen
import com.example.palliativecare.ui.screen.ArticleDetailsScreen
import com.example.palliativecare.ui.screen.ChatDetailsScreen
import com.example.palliativecare.ui.screen.CommentScreen
import com.example.palliativecare.ui.screen.EditProfileScreen
import com.example.palliativecare.ui.screen.HistoryScreen
import com.example.palliativecare.ui.screen.LoginScreen
import com.example.palliativecare.ui.screen.MainScreen
import com.example.palliativecare.ui.screen.RegisterScreen
import com.example.palliativecare.ui.theme.PalliativeCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val preferences = getSharedPreferences("my_app", Context.MODE_PRIVATE)
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
        composable(route = "chat_details_screen") {
            ChatDetailsScreen(navHostController)
        }
        composable(route = "article_details_screen") {
            ArticleDetailsScreen(navHostController)
        }
        composable(route = "comment_screen") {
            CommentScreen(navHostController)
        }
        composable(route = "edit_profile_screen") {
            EditProfileScreen(navHostController, ProfileController())
        }
        composable(route = "history_screen") {
            HistoryScreen(navHostController)
        }
        composable(route = "add_article_screen") {
            AddArticleScreen(
                articleController = ArticleController(),
                navController = navHostController
            )
        }
    }
}