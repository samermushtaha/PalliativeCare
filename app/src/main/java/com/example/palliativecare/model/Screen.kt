package com.example.palliativecare.model

import com.example.palliativecare.R

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("home_screen", "الرئيسية", R.drawable.ic_home)
    object Chat : Screen("chat_screen", "الدردشة", R.drawable.ic_chat)
    object Profile : Screen("profile_screen", "الصفحة الشخصية", R.drawable.ic_person)
}