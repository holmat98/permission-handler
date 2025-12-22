package com.mateuszholik.permissionhandler.utils

import android.app.Activity
import com.mateuszholik.permissionhandler.extensions.isPermissionGranted
import com.mateuszholik.permissionhandler.models.State

internal object StateUtils {

    fun getInitialStateFor(
        permissionName: String,
        currentState: State,
        activity: Activity,
    ): State =
        when (currentState) {
            State.NOT_ASKED -> State.NOT_ASKED
            State.SHOW_RATIONALE -> {
                if (activity.shouldShowRequestPermissionRationale(permissionName)) {
                    State.SHOW_RATIONALE
                } else {
                    State.DENIED
                }
            }

            State.DENIED -> State.DENIED
            State.GRANTED -> {
                if (activity.isPermissionGranted(permissionName)) {
                    State.GRANTED
                } else {
                    State.NOT_ASKED
                }
            }
        }

    fun State.getNextState(
        permissionName: String,
        isGranted: Boolean,
        activity: Activity,
    ): State =
        when {
            isGranted -> State.GRANTED
            this == State.NOT_ASKED ||
                    activity.shouldShowRequestPermissionRationale(permissionName) -> State.SHOW_RATIONALE

            else -> State.DENIED
        }
}
