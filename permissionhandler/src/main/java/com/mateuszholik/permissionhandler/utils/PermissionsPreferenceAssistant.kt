package com.mateuszholik.permissionhandler.utils

import android.content.Context
import android.content.SharedPreferences
import com.mateuszholik.permissionhandler.models.State

internal interface PermissionsPreferenceAssistant {

    fun saveState(permissionName: String, state: State)

    fun getState(permissionName: String): State

    companion object {
        fun newInstance(context: Context): PermissionsPreferenceAssistant =
            PermissionPreferenceAssistantImpl(context)
    }
}

internal class PermissionPreferenceAssistantImpl(
    private val context: Context,
) : PermissionsPreferenceAssistant {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun saveState(permissionName: String, state: State) {
        sharedPreferences
            .edit()
            .putString(permissionName, state.name)
            .apply()
    }

    override fun getState(permissionName: String): State =
        sharedPreferences.getString(permissionName, null)?.let {
            State.valueOf(it)
        } ?: State.NOT_ASKED

    private companion object {
        const val SHARED_PREFS_FILE_NAME = "permission_handler_preferences"
    }
}
