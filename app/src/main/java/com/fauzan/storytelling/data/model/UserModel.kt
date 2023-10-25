package com.fauzan.storytelling.data.model

data class UserModel(
    val name: String,
    val email: String,
    val token: String? = null,
)
