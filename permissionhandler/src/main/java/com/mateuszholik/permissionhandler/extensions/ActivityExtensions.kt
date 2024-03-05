package com.mateuszholik.permissionhandler.extensions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

internal fun Activity.isPermissionGranted(permissionName: String): Boolean =
    ContextCompat.checkSelfPermission(this, permissionName) == PackageManager.PERMISSION_GRANTED
