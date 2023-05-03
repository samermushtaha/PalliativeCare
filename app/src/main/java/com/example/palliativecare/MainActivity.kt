package com.example.palliativecare

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
import com.example.palliativecare.ui.screen.AddArticleScreen
import com.example.palliativecare.ui.screen.ArticleDetailsScreen
import com.example.palliativecare.ui.screen.ChatDetailsScreen
import com.example.palliativecare.ui.screen.CommentScreen
import com.example.palliativecare.ui.screen.HistoryScreen
import com.example.palliativecare.ui.screen.LoginScreen
import com.example.palliativecare.ui.screen.MainScreen
import com.example.palliativecare.ui.screen.RegisterScreen
import com.example.palliativecare.ui.theme.PalliativeCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PalliativeCareTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl ) {
                    val navController = rememberNavController()
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MyNavHost(navHostController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MyNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = "chat_details_screen"
    ) {
        composable(route = "login_screen") {
            LoginScreen(navHostController)
        }
        composable(route = "register_screen") {
            RegisterScreen(navHostController)
        }
        composable(route = "main_screen") {
            MainScreen()
        }
        composable(route = "chat_details_screen") {
            ChatDetailsScreen()
        }
        composable(route = "article_details_screen") {
            ArticleDetailsScreen()
        }
        composable(route = "comment_screen") {
            CommentScreen()
        }
        composable(route = "history_screen") {
            HistoryScreen()
        }
        composable(route = "add_article_screen") {
            AddArticleScreen()
        }
    }
}