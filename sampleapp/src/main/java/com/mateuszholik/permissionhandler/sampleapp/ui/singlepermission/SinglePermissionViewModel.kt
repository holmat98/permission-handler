package com.mateuszholik.permissionhandler.sampleapp.ui.singlepermission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateuszholik.permissionhandler.SinglePermissionHandler
import com.mateuszholik.permissionhandler.models.SinglePermissionState
import com.mateuszholik.permissionhandler.sampleapp.ui.di.CameraPermissionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SinglePermissionViewModel @Inject constructor(
    @CameraPermissionHandler private val cameraPermissionHandler: SinglePermissionHandler,
) : ViewModel() {

    val state: StateFlow<SinglePermissionState>
        get() = cameraPermissionHandler.state

    fun handlePermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            cameraPermissionHandler.handlePermissionResult(isGranted)
        }
    }

    fun handleBackFromSettings() {
        viewModelScope.launch {
            cameraPermissionHandler.handleBackFromSettings()
        }
    }

}
