package com.mateuszholik.permissionhandler.extensions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.mateuszholik.permissionhandler.models.Permission

internal fun Activity.shouldShowRationale(permission: Permission): Boolean =
    permission.permissions.any {
        shouldShowRequestPermissionRationale(it)
    }

internal fun Activity.isPermissionGranted(permissionName: String): Boolean =
    ContextCompat.checkSelfPermission(this, permissionName) == PackageManager.PERMISSION_GRANTED
