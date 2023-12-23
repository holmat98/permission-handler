package com.mateuszholik.permissionhandler.models

data class Permission(
    val name: String,
    val isOptional: Boolean,
    val minSdk: Int,
)
