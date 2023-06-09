package com.example.palliativecare.ui.screen

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.controller.category.CategoryController
import com.example.palliativecare.controller.chat.ChatController
import com.example.palliativecare.controller.profile.ProfileController
import com.example.palliativecare.model.Screen
import com.example.palliativecare.model.User
import com.example.palliativecare.ui.screen.chat.ChatScreen
import com.example.palliativecare.ui.screen.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navHostController: NavController, preferences: SharedPreferences) {
    val navController = rememberNavController()
    val selectedItem = remember { mutableStateOf(Screen.Home.route) }
    val current = remember { mutableStateOf(0) }
    val navItems = listOf(
        Screen.Home,
        Screen.Chat,
        Screen.Profile
    )
    val userId =  FirebaseAuth.getInstance().currentUser?.uid?:" "
    val userType = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit){
        userType.value = User.getUserByID(userId).first().userType
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = navItems[current.value].title, fontSize = 22.sp) },
            )
        },
        floatingActionButton = {
            if (selectedItem.value == Screen.Home.route && userType.value == "طبيب") {
                FloatingActionButton(onClick = { navHostController.navigate("add_article_screen") }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = ""
                            )
                        },
                        label = { Text(text = screen.title, fontSize = 12.sp) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                            current.value = index
                            selectedItem.value = screen.route
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
        ) {
            HomeNavHost(
                navHostController = navController,
                mainNavHostController = navHostController,
                preferences
            )
        }
    }
}

@Composable
fun HomeNavHost(
    navHostController: NavHostController,
    mainNavHostController: NavController,
    preferences: SharedPreferences
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) { HomeScreen(mainNavHostController, ArticleController(), CategoryController()) }
        composable(Screen.Chat.route) { ChatScreen(mainNavHostController, ChatController()) }
        composable(Screen.Profile.route) {
            ProfileScreen(
                mainNavHostController,
                ProfileController(),
                preferences
            )
        }
    }
}
