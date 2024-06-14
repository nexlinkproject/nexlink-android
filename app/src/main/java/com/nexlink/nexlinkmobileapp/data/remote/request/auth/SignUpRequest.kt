package com.nexlink.nexlinkmobileapp.data.remote.request.auth

data class SignUpRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String,
)
