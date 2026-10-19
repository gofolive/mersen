package com.example.ui.model

data class AuthenticatedUser(
    val username: String,
    val displayName: String,
    val actor: UserActor,
    val studentMtuId: String? = null,
    val stationOrDept: String = ""
)
