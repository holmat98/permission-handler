package com.mateuszholik.permissionhandler.extensions

import com.mateuszholik.permissionhandler.models.Permission

internal val Permission.permissions: List<String>
    get() = when (this) {
        is Permission.Coupled -> this.names
        is Permission.Single -> listOf(name)
    }
