package com.mateuszholik.permissionhandler.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mateuszholik.permissionhandler.models.Permission

@SuppressLint("ObsoleteSdkInt")
internal fun Permission.isGranted(context: Context): Boolean =
    Build.VERSION.SDK_INT < minSdk ||
            ContextCompat.checkSelfPermission(context, name) == PackageManager.PERMISSION_GRANTED
