package com.mateuszholik.permissionhandler.sampleapp.navigation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.mateuszholik.permissionhandler.sampleapp.ui.writeexternal.WriteExternalStoragePermissionScreen

object Navigation {

    const val ROOT = "ROOT"
    private const val MAIN_SCREEN = "$ROOT/MAIN_SCREEN"
    private const val CAMERA_PERMISSION_SCREEN = "$ROOT/CAMERA_PERMISSION_SCREEN"
    private const val LOCATION_PERMISSION_SCREEN = "$ROOT/LOCATION_PERMISSION_SCREEN"
    private const val NOTIFICATION_PERMISSION_SCREEN = "$ROOT/NOTIFICATION_PERMISSION_SCREEN"
    private const val INFO_SCREEN = "$ROOT/INFO_SCREEN"
    private const val LICENSE_SCREEN = "$ROOT/LICENSE_SCREEN"
    private const val WRITE_EXTERNAL_STORAGE_SCREEN = "$ROOT/WRITE_EXTERNAL_STORAGE_SCREEN"

    fun NavGraphBuilder.navigationGraph(navController: NavController) {
        navigation(startDestination = MAIN_SCREEN, route = ROOT) {
            mainScreen(navController)
            cameraPermissionScreen(navController)
            locationPermissionScreen(navController)
            notificationPermissionScreen(navController)
            infoScreen(navController)
            licenseScreen(navController)
            writeExternalStoragePermissionScreen(navController)
        }
    }

    private fun NavGraphBuilder.mainScreen(navController: NavController) {
        composable(MAIN_SCREEN) {
            MainScreen(
                onCameraPermissionPressed = { navController.navigateToCameraPermissionScreen() },
                onLocationPermissionPressed = { navController.navigateToLocationPermissionScreen() },
                onNotificationPermissionPressed = { navController.navigateToNotificationPermissionScreen() },
                onWriteExternalStoragePermissionPressed = { navController.navigateToWriteExternalStorageScreen() },
                onInfoPressed = { navController.navigateToInfoScreen() },
            )
        }
    }

    private fun NavGraphBuilder.cameraPermissionScreen(navController: NavController) {
        composable(
            route = CAMERA_PERMISSION_SCREEN,
            enterTransition = { horizontalEnterAnimation },
            exitTransition = { horizontalExitAnimation },
        ) {
            CameraPermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.locationPermissionScreen(navController: NavController) {
        composable(
            route = LOCATION_PERMISSION_SCREEN,
            enterTransition = { horizontalEnterAnimation },
            exitTransition = { horizontalExitAnimation },
        ) {
            LocationPermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.notificationPermissionScreen(navController: NavController) {
        composable(
            route = NOTIFICATION_PERMISSION_SCREEN,
            enterTransition = { horizontalEnterAnimation },
            exitTransition = { horizontalExitAnimation },
        ) {
            NotificationPermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.writeExternalStoragePermissionScreen(navController: NavController) {
        composable(
            route = WRITE_EXTERNAL_STORAGE_SCREEN,
            enterTransition = { horizontalEnterAnimation },
            exitTransition = { horizontalExitAnimation },
        ) {
            WriteExternalStoragePermissionScreen(
                onBackPressed = { navController.navigateUp() }
            )
        }
    }

    private fun NavGraphBuilder.infoScreen(navController: NavController) {
        composable(
            route = INFO_SCREEN,
            enterTransition = { horizontalEnterAnimation },
            exitTransition = { horizontalExitAnimation },
        ) {
            InfoScreen(
                onBackPressed = { navController.navigateUp() },
                onPermissionHandlerLicensePressed = { navController.navigateToLicenseScreen() }
            )
        }
    }

    private fun NavGraphBuilder.licenseScreen(navController: NavController) {
        composable(
            route = LICENSE_SCREEN,
            enterTransition = { horizontalEnterAnimation },
            exitTransition = { horizontalExitAnimation },
        ) {
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

    private fun NavController.navigateToWriteExternalStorageScreen() =
        navigate(WRITE_EXTERNAL_STORAGE_SCREEN)

    private const val ANIMATION_DURATION = 400

    private val horizontalEnterAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = LinearOutSlowInEasing
        ),
        initialOffsetX = { it }
    )

    private val horizontalExitAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = LinearOutSlowInEasing,
        ),
        targetOffsetX = { it }
    )
}
