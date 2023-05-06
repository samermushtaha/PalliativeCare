package com.example.palliativecare.controller.profile

import com.google.firebase.auth.FirebaseAuth


class ProfileController {

    fun logout(onComplete: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().signOut()
        onComplete(true)
    }
}