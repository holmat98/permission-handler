package com.mateuszholik.permissionhandler.listeners

import com.mateuszholik.permissionhandler.models.State

internal fun interface OnStateChangedListener<in T: State> {

    fun onStateChanged(newState: T)
}
