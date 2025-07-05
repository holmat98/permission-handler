# PermissionHandler

<p align="center">
<img src="sampleapp/src/main/ic_launcher-playstore.png" alt="App icon" width="256px" height="256px">
</p>

<b>Android library to handle permissions flow in applications that are created with jetpack compose.</b>

### Table of contents
* [Sample app](#sample-app)
* [How to download it](#how-to-download-it)
* [How to use it](#how-to-use-it)
* [Permission states](#permission-states)
* [Known issues](#known-issues)

### Sample App

You can download the sample app [here](https://install.appcenter.ms/users/holmat1998/apps/permission-handler/distribution_groups/releases/releases/3)

### How to download it

In `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

In module `build.gradle.kts`:

```kotlin
dependencies {
  implementation("com.github.holmat98:permission-handler:1.1.2")
}
```

Current version:
[![](https://jitpack.io/v/holmat98/permission-handler.svg)](https://jitpack.io/#holmat98/permission-handler)

### How to use it

Use `rememberPermissionHandler` method in your composable to get the current state of the permission and lambda to launch system permission dialog.
As an argument pass `Permission` object. If you have one permission use `Permission.Single`.
If there is permission that needs to be asked coupled like `android.Manifest.permission.ACCESS_FINE_LOCATION` and `android.Manifest.permission.ACCESS_COARSE_LOCATION` use `Permission.Coupled`.

```kotlin
@Composable
fun Screen() {
    val permissionHandler by rememberPermissionHandler(
        permission = Permission.Single(name = Manifest.permission.CAMERA)
    )
    
    when (permissionHandler.currentPermissionState) {
        PermissionState.AskForPermission -> {
            // Ask for permission with permissionHandler.launchPermissionDialog().
        }
        PermissionState.ShowRationale -> {
            // Show rationale to the user. Show permission dialog with permissionHandler.launchPermissionDialog().
        }
        PermissionState.Denied -> {
            // Permission is denied forever. Open settings screen with permissionHandler.launchPermissionDialog().
        }
        PermissionState.Granted -> {
            // Permission is granted.
        }
    }
}
```

```kotlin
@Composable
fun Screen() {
    val permissionHandler by rememberPermissionHandler(
        permission = Permission.Coupled(
            names = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    )
    
    when (permissionHandler.currentPermissionState) {
        PermissionState.AskForPermission -> {
            // Ask for permission with permissionHandler.launchPermissionDialog().
        }
        PermissionState.ShowRationale -> {
            // Show rationale to the user. Show permission dialog with permissionHandler.launchPermissionDialog().
        }
        PermissionState.Denied -> {
            // Permission is denied forever. Open settings screen with permissionHandler.launchPermissionDialog().
        }
        PermissionState.Granted -> {
            // Permission is granted.
        }
    }
}
```

In case of permissions like `android.Manifest.permission.POST_NOTIFICATIONS` that was added as the runtime permission in Android 13 you can set `minSdk` in `Permission`.
When user opens app on the device that has older version of the Android than provided `minSdk` then `PermissionState.Granted` is returned.

```kotlin
@Composable
fun Screen() {
    val permissionHandler by rememberPermissionHandler(
        permission = Permission.Single(
            name = Manifest.permission.POST_NOTIFICATIONS,
            minSdk = Build.VERSION_CODES.TIRAMISU
        )
    )
    
    when (permissionHandler.currentPermissionState) {
        PermissionState.AskForPermission -> {
            // Ask for permission with permissionHandler.launchPermissionDialog().
        }
        PermissionState.ShowRationale -> {
            // Show rationale to the user. Show permission dialog with permissionHandler.launchPermissionDialog().
        }
        PermissionState.Denied -> {
            // Permission is denied forever. Open settings screen with permissionHandler.launchPermissionDialog().
        }
        PermissionState.Granted -> {
            // Permission is granted.
        }
    }
}
```

### Permission states

Permission can be in one of four states:

* `AskForPermission` -> it is returned when user was never asked for the permission or when user selected ask every time on system dialog.
* `ShowRationale` -> it is returned when user denied permission once.
* `Denied` -> it is returned when permission was denied forever and system dialog cannot be shown.
* `Granted` -> it is returned when user grants the permission.

### Known issues

I am not able to find out if user selected `Ask every time` in system settings after permission was denied forever. 
This is because `Activity.shouldShowRequestPermissionRationale(permissionName)` returns `false` and `ContextCompat.checkSelfPermission(context, permissionName)` returns `PackageManager.PERMISSION_DENIED`.
