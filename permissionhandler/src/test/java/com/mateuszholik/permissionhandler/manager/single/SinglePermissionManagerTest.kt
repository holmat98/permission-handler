package com.mateuszholik.permissionhandler.manager.single

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class SinglePermissionManagerTest {

    private val activity = mockk<Activity>()
    private val permissionsPreferenceAssistant =
        mockk<PermissionsPreferenceAssistant>(relaxed = true)

    private lateinit var permissionManager: SinglePermissionManager

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
    fun `When maxSdk is smaller then minSdk exception is thrown`() {
        mockkContextCompat(isGranted = false)
        assertThrows<IllegalStateException> {
            initializePermissionManager(
                permission = PERMISSION.copy(
                    minSdk = 33,
                    maxSdk = 31,
                ),
            )
        }
    }

    @Test
    fun `When maxSdk is equal to minSdk exception is not thrown`() {
        mockkContextCompat(isGranted = false)
        assertDoesNotThrow {
            initializePermissionManager(
                permission = PERMISSION.copy(
                    minSdk = 31,
                    maxSdk = 31,
                ),
            )
        }
    }

    @Test
    fun `When maxSdk is greater than minSdk exception is not thrown`() {
        mockkContextCompat(isGranted = false)
        assertDoesNotThrow {
            initializePermissionManager(
                permission = PERMISSION.copy(
                    minSdk = 31,
                    maxSdk = 34,
                ),
            )
        }
    }

    @Test
    fun `When current android version is lower than minSdk then GRANTED is returned`() {
        mockkContextCompat(isGranted = true)
        assertDoesNotThrow {
            initializePermissionManager(
                permission = PERMISSION.copy(minSdk = 31),
                androidSdkVersion = 30,
            )
        }
        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When current android version is greater than maxSdk then GRANTED is returned`() {
        mockkContextCompat(isGranted = false)
        assertDoesNotThrow {
            initializePermissionManager(
                permission = PERMISSION.copy(maxSdk = 29),
                androidSdkVersion = 30,
            )
        }
        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When saved permission state is GRANTED and ContextCompat returns PERMISSION_GRANTED then the initial state is equal to Granted`() {
        mockkContextCompat(isGranted = true)
        initializePermissionManager(savedState = State.GRANTED)

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When saved permission state is GRANTED and ContextCompat returns PERMISSION_DENIED then the initial state is equal to AskForPermission`() {
        mockkContextCompat(isGranted = false)
        initializePermissionManager(savedState = State.GRANTED)

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.AskForPermission)
    }

    @Test
    fun `When Android version is greater than max sdk for the permission then initial state is equal to Granted`() {
        initializePermissionManager(
            permission = PERMISSION.copy(maxSdk = 28),
            androidSdkVersion = 33,
        )

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When Android version is lower than min sdk for the permission then initial state is equal to Granted`() {
        initializePermissionManager(
            permission = PERMISSION.copy(minSdk = 33),
            androidSdkVersion = 32
        )

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When saved permission state is NOT_ASKED then the initial state is equal to AskForPermission`() {
        initializePermissionManager(savedState = State.NOT_ASKED)

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.AskForPermission)
    }

    @Test
    fun `When saved permission state is SHOW_RATIONALE then the initial state is equal to ShowRationale`() {
        mockkActivityShouldShowRationale(shouldShow = true)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.ShowRationale)
    }

    @Test
    fun `When saved permission state is SHOW_RATIONALE and activity should show rationale returns false then the initial state is equal to Denied`() {
        mockkActivityShouldShowRationale(shouldShow = false)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `When saved permission state is DENIED then the initial state is equal to Denied`() {
        initializePermissionManager(savedState = State.DENIED)

        val initialState = permissionManager.initialState

        Assertions.assertThat(initialState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `When permission was denied by the user then ShowRationale is the current state`() {
        initializePermissionManager()
        mockkActivityShouldShowRationale(shouldShow = true)

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When permission was denied by the user and NOT_ASKED is current saved state then ShowRationale is the current state`() {
        initializePermissionManager()
        mockkActivityShouldShowRationale(shouldShow = false)

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When permission was granted by the user then Granted is the current state`() {
        initializePermissionManager()

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to true))

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is ShowRationale and permission was denied by the user then Denied is the current state`() {
        mockkActivityShouldShowRationale(shouldShow = true)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        Assertions.assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        mockkActivityShouldShowRationale(shouldShow = false)

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.DENIED
            )
        }
    }

    @Test
    fun `When current state is ShowRationale and permission was granted by the user then Granted is the current state`() {
        mockkActivityShouldShowRationale(shouldShow = true)
        initializePermissionManager(savedState = State.SHOW_RATIONALE)

        Assertions.assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to true))

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is Denied and permission was granted in the settings then Granted is the current state`() {
        initializePermissionManager(savedState = State.DENIED)
        mockkContextCompat(isGranted = true)

        Assertions.assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is Denied and activity should show rationale returns true then ShowRationale is the current state`() {
        initializePermissionManager(savedState = State.DENIED)

        Assertions.assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkActivityShouldShowRationale(shouldShow = true)
        mockkContextCompat(isGranted = false)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When current state is Denied and permission was not granted in the settings then Denied is the current state`() {
        initializePermissionManager(savedState = State.DENIED)
        mockkContextCompat(isGranted = true)

        Assertions.assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkActivityShouldShowRationale(shouldShow = false)
        mockkContextCompat(isGranted = false)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        Assertions.assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `PermissionManager handles correctly the whole flow of the permission granting process`() {
        initializePermissionManager()

        Assertions.assertThat(permissionManager.initialState).isEqualTo(PermissionState.AskForPermission)

        mockkActivityShouldShowRationale(shouldShow = true)

        Assertions.assertThat(
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))
        ).isEqualTo(PermissionState.ShowRationale)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.SHOW_RATIONALE) }

        mockkActivityShouldShowRationale(shouldShow = false)

        Assertions.assertThat(
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))
        ).isEqualTo(PermissionState.Denied)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.DENIED) }

        mockkContextCompat(isGranted = true)

        Assertions.assertThat(
            permissionManager.handleBackFromSettings()
        ).isEqualTo(PermissionState.Granted)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.GRANTED) }
    }

    private fun initializePermissionManager(
        permission: Permission.Single = PERMISSION,
        savedState: State = State.NOT_ASKED,
        androidSdkVersion: Int = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
    ) {
        every { SdkProvider.provide() } returns androidSdkVersion
        every { permissionsPreferenceAssistant.getState(PERMISSION_NAME_1) } returns savedState
        permissionManager = SinglePermissionManager(
            activity = activity,
            permissionsPreferenceAssistant = permissionsPreferenceAssistant,
            permission = permission
        )
    }

    private fun mockkContextCompat(
        isGranted: Boolean,
        permissionName: String = PERMISSION_NAME_1,
    ) {
        val result = if (isGranted) {
            PackageManager.PERMISSION_GRANTED
        } else {
            PackageManager.PERMISSION_DENIED
        }
        every { ContextCompat.checkSelfPermission(activity, permissionName) } returns result
    }

    private fun mockkActivityShouldShowRationale(
        shouldShow: Boolean,
        permissionName: String = PERMISSION_NAME_1,
    ) {
        every {
            activity.shouldShowRequestPermissionRationale(permissionName)
        } returns shouldShow
    }

    private companion object Companion {
        const val PERMISSION_NAME_1 = "permission_name_1"
        val PERMISSION = Permission.Single(
            name = PERMISSION_NAME_1,
            minSdk = 1,
            maxSdk = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
        )

    }
}
