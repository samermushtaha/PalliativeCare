package com.example.palliativecare.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.palliativecare.R

val tajawalFamily = FontFamily(
    Font(R.font.tajawal_regular, weight = FontWeight.Normal)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = tajawalFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(fontFamily = tajawalFamily),
    displayMedium = TextStyle(fontFamily = tajawalFamily),
    displaySmall = TextStyle(fontFamily = tajawalFamily),
    headlineLarge = TextStyle(fontFamily = tajawalFamily),
    headlineMedium = TextStyle(fontFamily = tajawalFamily),
    headlineSmall = TextStyle(fontFamily = tajawalFamily),
    titleLarge = TextStyle(fontFamily = tajawalFamily),
    titleMedium = TextStyle(fontFamily = tajawalFamily),
    titleSmall = TextStyle(fontFamily = tajawalFamily),
    bodyMedium = TextStyle(fontFamily = tajawalFamily),
    bodySmall = TextStyle(fontFamily = tajawalFamily),
    labelLarge = TextStyle(fontFamily = tajawalFamily),
    labelMedium = TextStyle(fontFamily = tajawalFamily),
    labelSmall = TextStyle(fontFamily = tajawalFamily)
)

