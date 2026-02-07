package com.mateuszholik.permissionhandler.utils

import android.Manifest
import android.os.Build
import com.mateuszholik.permissionhandler.models.Permission

object PermissionsConstants {

    /**
     * Write external storage permission object adjusted for every android version
     */
    val WRITE_EXTERNAL_STORAGE = Permission.Single(
        name = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        maxSdk = Build.VERSION_CODES.P,
    )

    /**
     * Read photos permission object adjusted for every android version
     */
    val READ_PHOTOS_PERMISSION by lazy {
        when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 -> {
                Permission.Single(
                    name = Manifest.permission.READ_EXTERNAL_STORAGE,
                    maxSdk = Build.VERSION_CODES.S_V2,
                )
            }

            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> {
                Permission.Single(
                    name = Manifest.permission.READ_MEDIA_IMAGES,
                    minSdk = Build.VERSION_CODES.TIRAMISU,
                )
            }

            else -> {
                Permission.Coupled(
                    names = listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    ),
                    mainPermissions = listOf(Manifest.permission.READ_MEDIA_IMAGES),
                    minSdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
                )
            }
        }
    }
}
