package com.nexlink.nexlinkmobileapp.data.local.pref

data class UserModel(
    val fullName: String,
    val userId: String,
    val accessToken: String,
    val isLogin: Boolean = false
)
