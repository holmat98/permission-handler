package com.mateuszholik.permissionhandler.sampleapp.ui.singlepermission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateuszholik.permissionhandler.SinglePermissionHandler
import com.mateuszholik.permissionhandler.models.SinglePermissionState
import com.mateuszholik.permissionhandler.sampleapp.ui.di.CameraPermissionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SinglePermissionViewModel @Inject constructor(
    @CameraPermissionHandler private val cameraPermissionHandler: SinglePermissionHandler,
) : ViewModel() {

    val state: StateFlow<SinglePermissionState>
        get() = cameraPermissionHandler.observe().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            SinglePermissionState.Skipped
        )

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
