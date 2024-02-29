package com.mateuszholik.permissionhandler.sampleapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mateuszholik.permissionhandler.sampleapp.ui.singlepermission.SinglePermissionScreen
import com.mateuszholik.permissionhandler.sampleapp.ui.theme.PermissionHandlerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionHandlerTheme {
                SinglePermissionScreen()
            }
        }
    }
}
