package com.mateuszholik.permissionhandler.extensions

import android.app.Activity
import com.mateuszholik.permissionhandler.models.Permission

internal fun Activity.shouldShowRationale(permission: Permission): Boolean =
    shouldShowRequestPermissionRationale(permission.name)
