package com.mateuszholik.permissionhandler.sampleapp.ui.di

import android.Manifest
import android.content.Context
import com.mateuszholik.permissionhandler.SinglePermissionHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class CameraPermissionHandler

@Module
@InstallIn(ViewModelComponent::class)
internal object HandlerModule {

    @CameraPermissionHandler
    @Provides
    fun providesCameraPermissionHandler(
        @ApplicationContext context: Context
    ): SinglePermissionHandler =
        SinglePermissionHandler.builder(context) {
            permission = Manifest.permission.CAMERA
            isOptional = false
        }
}
