package com.example.palliativecare.controller.notifications

import android.util.Log
import com.example.palliativecare.model.PushNotification
import java.lang.Exception

object NotificationController {
    suspend fun sendNotification(notification: PushNotification) {
        try {
            RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.e("Mah ", e.toString())
        }
    }
}