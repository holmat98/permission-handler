package com.mateuszholik.permissionhandler.sampleapp.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mateuszholik.permissionhandler.sampleapp.ui.camera.CameraPermissionScreen
import com.mateuszholik.permissionhandler.sampleapp.ui.info.InfoScreen
import com.mateuszholik.permissionhandler.sampleapp.ui.license.LicenseScreen
import com.mateuszholik.permissionhandler.sampleapp.ui.location.LocationPermissionScreen
import com.mateuszholik.permissionhandler.sampleapp.ui.main.MainScreen
import com.mateuszholik.permissionhandler.sampleapp.ui.notification.NotificationPermissionScreen

object Navigation {

    const val ROOT = "ROOT"
    private const val MAIN_SCREEN = "$ROOT/MAIN_SCREEN"
    private const val CAMERA_PERMISSION_SCREEN = "$ROOT/CAMERA_PERMISSION_SCREEN"
    private const val LOCATION_PERMISSION_SCREEN = "$ROOT/LOCATION_PERMISSION_SCREEN"
    private const val NOTIFICATION_PERMISSION_SCREEN = "$ROOT/NOTIFICATION_PERMISSION_SCREEN"
    private const val INFO_SCREEN = "$ROOT/INFO_SCREEN"
    private const val LICENSE_SCREEN = "$ROOT/LICENSE_SCREEN"

    fun NavGraphBuilder.navigationGraph(navController: NavController) {
        navigation(startDestination = MAIN_SCREEN, route = ROOT) {
            mainScreen(navController)
            cameraPermissionScreen(navController)
            locationPermissionScreen(navController)
            notificationPermissionScreen(navController)
            infoScreen(navController)
            licenseScreen(navController)
        }
    }

    private fun NavGraphBuilder.mainScreen(navController: NavController) {
        composable(MAIN_SCREEN) {
            MainScreen(
                onCameraPermissionPressed = { navController.navigateToCameraPermissionScreen() },
                onLocationPermissionPressed = { navController.navigateToLocationPermissionScreen() },
                onNotificationPermissionPressed = { navController.navigateToNotificationPermissionScreen() },
                onInfoPressed = { navController.navigateToInfoScreen() },
            )
        }
    }

    private fun NavGraphBuilder.cameraPermissionScreen(navController: NavController) {
        composable(CAMERA_PERMISSION_SCREEN) {
            CameraPermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.locationPermissionScreen(navController: NavController) {
        composable(LOCATION_PERMISSION_SCREEN) {
            LocationPermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.notificationPermissionScreen(navController: NavController) {
        composable(NOTIFICATION_PERMISSION_SCREEN) {
            NotificationPermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.infoScreen(navController: NavController) {
        composable(INFO_SCREEN) {
            InfoScreen(
                onBackPressed = { navController.navigateUp() },
                onPermissionHandlerLicensePressed = { navController.navigateToLicenseScreen() }
            )
        }
    }

    private fun NavGraphBuilder.licenseScreen(navController: NavController) {
        composable(LICENSE_SCREEN) {
            LicenseScreen(onBackPressed = { navController.navigateUp() })
        }
    }

    private fun NavController.navigateToCameraPermissionScreen() =
        navigate(CAMERA_PERMISSION_SCREEN)

    private fun NavController.navigateToLocationPermissionScreen() =
        navigate(LOCATION_PERMISSION_SCREEN)

    private fun NavController.navigateToNotificationPermissionScreen() =
        navigate(NOTIFICATION_PERMISSION_SCREEN)

    private fun NavController.navigateToInfoScreen() =
        navigate(INFO_SCREEN)

    private fun NavController.navigateToLicenseScreen() =
        navigate(LICENSE_SCREEN)
}
