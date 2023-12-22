package com.mateuszholik.permissionhandler.models

import android.os.Build

data class Permission(
    val name: String,
    val isOptional: Boolean,
    val minSdk: Int = Build.VERSION_CODES.ICE_CREAM_SANDWICH,
)
