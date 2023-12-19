package com.example.storyapp_ade.data.pref

data class UserModel(
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)