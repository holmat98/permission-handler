package com.mateuszholik.permissionhandler.sampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mateuszholik.permissionhandler.sampleapp.navigation.Navigation
import com.mateuszholik.permissionhandler.sampleapp.navigation.Navigation.navigationGraph
import com.mateuszholik.permissionhandler.sampleapp.theme.PermissionHandlerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionHandlerTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Navigation.ROOT,
                ) {
                    navigationGraph(navController)
                }
            }
        }
    }
}
