package com.mateuszholik.permissionhandler

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.mateuszholik.permissionhandler.manager.PermissionManager
import com.mateuszholik.permissionhandler.manager.PermissionManagerImpl
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.State
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SinglePermissionHandlerTest {

    private val activity = mockk<Activity>()
    private val permissionsPreferenceAssistant =
        mockk<PermissionsPreferenceAssistant>(relaxed = true)

    private lateinit var permissionManager: PermissionManager

    @BeforeEach
    fun setUp() {
        mockkStatic(ContextCompat::class)
        mockkObject(SdkProvider)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(ContextCompat::class)
        unmockkObject(SdkProvider)
    }

    @Test
    fun `When saved permission state is GRANTED and ContextCompat returns PERMISSION_GRANTED then the initial state is equal to Granted`() {
        mockkContextCompat(isGranted = true)
        initializePermissionManager(savedState = State.GRANTED)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When saved permission state is GRANTED and ContextCompat returns PERMISSION_DENIED then the initial state is equal to ShowRationale`() {
        mockkContextCompat(isGranted = false)
        initializePermissionManager(savedState = State.GRANTED)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.ShowRationale)
    }

    @Test
    fun `When Android version is lower than min sdk for the permission then initial state is equal to Granted`() {
        initializePermissionManager(
            permission = PERMISSION.copy(minSdk = 33),
            androidSdkVersion = 32
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When saved permission state is NOT_ASKED then the initial state is equal to AskForPermission`() {
        initializePermissionManager(savedState = State.NOT_ASKED)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.AskForPermission)
    }

    @Test
    fun `When saved permission state is SHOW_RATIONALE then the initial state is equal to ShowRationale`() {
        mockkActivityShouldShowRationale(shouldShow = true)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.ShowRationale)
    }

    @Test
    fun `When saved permission state is SHOW_RATIONALE and activity should show rationale returns false then the initial state is equal to Denied`() {
        mockkActivityShouldShowRationale(shouldShow = false)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `When saved permission state is DENIED then the initial state is equal to Denied`() {
        initializePermissionManager(savedState = State.DENIED)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `When permission was denied by the user then ShowRationale is the current state`() {
        initializePermissionManager()
        mockkActivityShouldShowRationale(shouldShow = true)

        val nextPermissionState = permissionManager.handlePermissionResult(isGranted = false)

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When permission was denied by the user and activity should show rationale returns false then Denied is the current state`() {
        initializePermissionManager()
        mockkActivityShouldShowRationale(shouldShow = false)

        val nextPermissionState = permissionManager.handlePermissionResult(isGranted = false)

        assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.DENIED
            )
        }
    }

    @Test
    fun `When permission was granted by the user then Granted is the current state`() {
        initializePermissionManager()

        val nextPermissionState = permissionManager.handlePermissionResult(isGranted = true)

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is ShowRationale and permission was denied by the user then Denied is the current state`() {
        mockkActivityShouldShowRationale(shouldShow = true)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        mockkActivityShouldShowRationale(shouldShow = false)

        val nextPermissionState = permissionManager.handlePermissionResult(isGranted = false)

        assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.DENIED
            )
        }
    }

    @Test
    fun `When current state is ShowRationale and permission was granted by the user then Granted is the current state`() {
        mockkActivityShouldShowRationale(shouldShow = true)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        val nextPermissionState = permissionManager.handlePermissionResult(isGranted = true)

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is Denied and permission was granted in the settings then Granted is the current state`() {
        initializePermissionManager(savedState = State.DENIED)
        mockkContextCompat(isGranted = true)

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is Denied and activity should show rationale returns true then ShowRationale is the current state`() {
        initializePermissionManager(savedState = State.DENIED)

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkActivityShouldShowRationale(shouldShow = true)
        mockkContextCompat(isGranted = false)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION.name,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When current state is Denied and permission was not granted in the settings then Denied is the current state`() {
        initializePermissionManager(savedState = State.DENIED)
        mockkContextCompat(isGranted = true)

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkActivityShouldShowRationale(shouldShow = false)
        mockkContextCompat(isGranted = false)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `PermissionManager handles correctly the whole flow of the permission granting process`() {
        initializePermissionManager()

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.AskForPermission)

        mockkActivityShouldShowRationale(shouldShow = true)

        assertThat(
            permissionManager.handlePermissionResult(isGranted = false)
        ).isEqualTo(PermissionState.ShowRationale)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION.name, State.SHOW_RATIONALE) }

        mockkActivityShouldShowRationale(shouldShow = false)

        assertThat(
            permissionManager.handlePermissionResult(isGranted = false)
        ).isEqualTo(PermissionState.Denied)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION.name, State.DENIED) }

        mockkContextCompat(isGranted = true)

        assertThat(
            permissionManager.handleBackFromSettings()
        ).isEqualTo(PermissionState.Granted)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION.name, State.GRANTED) }
    }

    private fun initializePermissionManager(
        permission: Permission = PERMISSION,
        savedState: State = State.NOT_ASKED,
        androidSdkVersion: Int = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
    ) {
        every { SdkProvider.provide() } returns androidSdkVersion
        every { permissionsPreferenceAssistant.getState(PERMISSION.name) } returns savedState
        permissionManager = PermissionManagerImpl(
            activity = activity,
            permissionsPreferenceAssistant = permissionsPreferenceAssistant,
            permission = permission
        )
    }

    private fun mockkContextCompat(isGranted: Boolean) {
        val result = if (isGranted) {
            PackageManager.PERMISSION_GRANTED
        } else {
            PackageManager.PERMISSION_DENIED
        }
        every { ContextCompat.checkSelfPermission(activity, PERMISSION.name) } returns result
    }

    private fun mockkActivityShouldShowRationale(shouldShow: Boolean) {
        every {
            activity.shouldShowRequestPermissionRationale(PERMISSION.name)
        } returns shouldShow
    }

    private companion object {
        val PERMISSION = Permission(
            name = "permission_name",
            minSdk = 1
        )
    }
}
