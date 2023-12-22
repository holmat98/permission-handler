package com.mateuszholik.permissionhandler.utils

import android.content.Context
import android.content.SharedPreferences
import com.mateuszholik.permissionhandler.models.PermissionState

internal interface PermissionsPreferenceAssistant {

    fun savePermissionState(permissionName: String, state: PermissionState)

    fun getPermissionState(permissionName: String): PermissionState

    companion object {
        internal fun newInstance(context: Context): PermissionsPreferenceAssistant =
            PermissionPreferenceAssistantImpl(context)
    }
}

internal class PermissionPreferenceAssistantImpl(
    private val context: Context,
) : PermissionsPreferenceAssistant {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    override fun savePermissionState(permissionName: String, state: PermissionState) {
        sharedPreferences
            .edit()
            .putString(permissionName, state.name)
            .apply()
    }

    override fun getPermissionState(permissionName: String): PermissionState =
        sharedPreferences.getString(permissionName, null)?.let {
            PermissionState.valueOf(it)
        } ?: PermissionState.NOT_ASKED

    private companion object {
        const val SHARED_PREFS_FILE_NAME = "permissions_preferences"
    }
}
