package com.mateuszholik.permissionhandler.providers

import android.os.Build

internal object SdkProvider {

    fun provide(): Int = Build.VERSION.SDK_INT
}
