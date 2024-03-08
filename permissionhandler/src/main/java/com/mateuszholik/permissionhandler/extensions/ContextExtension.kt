package com.mateuszholik.permissionhandler.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

internal val Context.activity: Activity
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        error("Context cannot be casted to the activity")
    }
