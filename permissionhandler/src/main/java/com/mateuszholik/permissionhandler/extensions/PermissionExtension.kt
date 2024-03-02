package com.mateuszholik.permissionhandler.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.mateuszholik.permissionhandler.models.Permission

@SuppressLint("ObsoleteSdkInt")
internal fun Permission.isGranted(context: Context): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
