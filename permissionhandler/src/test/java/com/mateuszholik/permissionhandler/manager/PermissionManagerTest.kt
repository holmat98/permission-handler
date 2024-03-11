package com.mateuszholik.permissionhandler.manager

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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PermissionManagerTest {

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
    fun `When saved permission state is GRANTED and ContextCompat returns PERMISSION_DENIED then the initial state is equal to AskForPermission`() {
        mockkContextCompat(isGranted = false)
        initializePermissionManager(savedState = State.GRANTED)

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.AskForPermission)
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

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
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

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
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

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
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

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        mockkActivityShouldShowRationale(shouldShow = false)

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))

        assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
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

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        val nextPermissionState =
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to true))

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
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

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
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

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkActivityShouldShowRationale(shouldShow = true)
        mockkContextCompat(isGranted = false)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
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
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))
        ).isEqualTo(PermissionState.ShowRationale)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.SHOW_RATIONALE) }

        mockkActivityShouldShowRationale(shouldShow = false)

        assertThat(
            permissionManager.handlePermissionResult(result = mapOf(PERMISSION_NAME_1 to false))
        ).isEqualTo(PermissionState.Denied)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.DENIED) }

        mockkContextCompat(isGranted = true)

        assertThat(
            permissionManager.handleBackFromSettings()
        ).isEqualTo(PermissionState.Granted)

        verify { permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.GRANTED) }
    }

    // Coupled

    @Test
    fun `When saved permission state is GRANTED for Coupled permissions and ContextCompat returns PERMISSION_GRANTED then the initial state is equal to Granted`() {
        mockkContextCompat(permissionName = PERMISSION_NAME_1, isGranted = true)
        mockkContextCompat(permissionName = PERMISSION_NAME_2, isGranted = true)
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.GRANTED,
            secondPermissionSavedState = State.GRANTED,
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When saved permission state is GRANTED and ContextCompat for at least one permission returns PERMISSION_DENIED then the initial state is equal to AskForPermission`() {
        mockkContextCompat(
            permissionName = PERMISSION_NAME_1,
            isGranted = true
        )
        mockkContextCompat(
            permissionName = PERMISSION_NAME_2,
            isGranted = false
        )
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.GRANTED,
            secondPermissionSavedState = State.GRANTED,
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.AskForPermission)
    }

    @Test
    fun `When Android version is lower than min sdk for the coupled permission then initial state is equal to Granted`() {
        initializeCoupledPermissionManager(
            permission = COUPLED_PERMISSION.copy(minSdk = 33),
            androidSdkVersion = 32
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Granted)
    }

    @Test
    fun `When at least one saved state is NOT_ASKED then the initial state is equal to AskForPermission`() {
        mockkContextCompat(
            permissionName = PERMISSION_NAME_2,
            isGranted = true
        )
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.NOT_ASKED,
            secondPermissionSavedState = State.GRANTED,
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.AskForPermission)
    }

    @Test
    fun `When saved permissions states are SHOW_RATIONALE then the initial state is equal to ShowRationale`() {
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.SHOW_RATIONALE,
            secondPermissionSavedState = State.SHOW_RATIONALE,
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.ShowRationale)
    }

    @Test
    fun `When saved permissions states are SHOW_RATIONALE and activity should show rationale returns false then the initial state is equal to Denied`() {
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = false)
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.SHOW_RATIONALE,
            secondPermissionSavedState = State.SHOW_RATIONALE,
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `When saved permissions states are DENIED then the initial state is equal to Denied`() {
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.DENIED,
            secondPermissionSavedState = State.DENIED,
        )

        val initialState = permissionManager.initialState

        assertThat(initialState).isEqualTo(PermissionState.Denied)
    }

    @Test
    fun `When one permission was denied by the user then ShowRationale is the current state`() {
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)
        initializeCoupledPermissionManager()

        val nextPermissionState = permissionManager.handlePermissionResult(
            result = mapOf(
                PERMISSION_NAME_1 to false,
                PERMISSION_NAME_2 to true,
            )
        )

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.SHOW_RATIONALE,
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.GRANTED,
            )
        }
    }

    @Test
    fun `When both permissions were denied by the user then ShowRationale is the current state`() {
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)
        initializeCoupledPermissionManager()

        val nextPermissionState = permissionManager.handlePermissionResult(
            result = mapOf(
                PERMISSION_NAME_1 to false,
                PERMISSION_NAME_2 to false,
            )
        )

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.SHOW_RATIONALE,
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.SHOW_RATIONALE,
            )
        }
    }

    @Test
    fun `When both permissions were granted by the user then Granted is the current state`() {
        initializeCoupledPermissionManager()

        val nextPermissionState = permissionManager.handlePermissionResult(
            result = mapOf(
                PERMISSION_NAME_1 to true,
                PERMISSION_NAME_2 to true,
            )
        )

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.GRANTED
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is ShowRationale and coupled permissions were denied by the user then Denied is the current state`() {
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.SHOW_RATIONALE,
            secondPermissionSavedState = State.SHOW_RATIONALE,
        )

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = false)

        val nextPermissionState = permissionManager.handlePermissionResult(
            result = mapOf(
                PERMISSION_NAME_1 to false,
                PERMISSION_NAME_2 to false,
            )
        )

        assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.DENIED
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.DENIED
            )
        }
    }

    @Test
    fun `When current state is ShowRationale and coupled permissions were granted by the user then Granted is the current state`() {
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.SHOW_RATIONALE,
            secondPermissionSavedState = State.SHOW_RATIONALE,
        )

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.ShowRationale)

        val nextPermissionState = permissionManager.handlePermissionResult(
            result = mapOf(
                PERMISSION_NAME_1 to true,
                PERMISSION_NAME_2 to true,
            )
        )

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.GRANTED
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is Denied and coupled permission were granted in the settings then Granted is the current state`() {
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.DENIED,
            secondPermissionSavedState = State.DENIED,
        )
        mockkContextCompat(permissionName = PERMISSION_NAME_1, isGranted = true)
        mockkContextCompat(permissionName = PERMISSION_NAME_2, isGranted = true)

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.Granted)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.GRANTED
            )
        }
    }

    @Test
    fun `When current state is Denied and activity should show rationale returns true for at least one permission then ShowRationale is the current state`() {
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.DENIED,
            secondPermissionSavedState = State.DENIED,
        )

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkContextCompat(permissionName = PERMISSION_NAME_1, isGranted = false)
        mockkContextCompat(permissionName = PERMISSION_NAME_2, isGranted = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When current state is Denied and activity should show rationale returns true for all permissions then ShowRationale is the current state`() {
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.DENIED,
            secondPermissionSavedState = State.DENIED,
        )

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkContextCompat(permissionName = PERMISSION_NAME_1, isGranted = false)
        mockkContextCompat(permissionName = PERMISSION_NAME_2, isGranted = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.ShowRationale)
        verify(exactly = 1) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.SHOW_RATIONALE
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.SHOW_RATIONALE
            )
        }
    }

    @Test
    fun `When current state is Denied and coupled permissions were not granted in the settings then Denied is the current state`() {
        initializeCoupledPermissionManager(
            firstPermissionSavedState = State.DENIED,
            secondPermissionSavedState = State.DENIED,
        )

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.Denied)

        mockkContextCompat(permissionName = PERMISSION_NAME_1, isGranted = false)
        mockkContextCompat(permissionName = PERMISSION_NAME_2, isGranted = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = false)

        val nextPermissionState = permissionManager.handleBackFromSettings()

        assertThat(nextPermissionState).isEqualTo(PermissionState.Denied)
        verify(exactly = 0) {
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_1,
                State.DENIED
            )
            permissionsPreferenceAssistant.saveState(
                PERMISSION_NAME_2,
                State.DENIED
            )
        }
    }

    @Test
    fun `PermissionManager handles correctly the whole flow of the coupled permissions granting process`() {
        initializeCoupledPermissionManager()

        assertThat(permissionManager.initialState).isEqualTo(PermissionState.AskForPermission)

        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)

        assertThat(
            permissionManager.handlePermissionResult(
                result = mapOf(
                    PERMISSION_NAME_1 to false,
                    PERMISSION_NAME_2 to false,
                )
            )
        ).isEqualTo(PermissionState.ShowRationale)

        verify {
            permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.SHOW_RATIONALE)
            permissionsPreferenceAssistant.saveState(PERMISSION_NAME_2, State.SHOW_RATIONALE)
        }

        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = true)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = true)

        assertThat(
            permissionManager.handlePermissionResult(
                result = mapOf(
                    PERMISSION_NAME_1 to false,
                    PERMISSION_NAME_2 to false,
                )
            )
        ).isEqualTo(PermissionState.ShowRationale)

        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_1, shouldShow = false)
        mockkActivityShouldShowRationale(permissionName = PERMISSION_NAME_2, shouldShow = false)

        assertThat(
            permissionManager.handlePermissionResult(
                result = mapOf(
                    PERMISSION_NAME_1 to false,
                    PERMISSION_NAME_2 to false,
                )
            )
        ).isEqualTo(PermissionState.Denied)

        verify {
            permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.DENIED)
            permissionsPreferenceAssistant.saveState(PERMISSION_NAME_2, State.DENIED)
        }

        mockkContextCompat(permissionName = PERMISSION_NAME_1, isGranted = true)
        mockkContextCompat(permissionName = PERMISSION_NAME_2, isGranted = true)

        assertThat(
            permissionManager.handleBackFromSettings()
        ).isEqualTo(PermissionState.Granted)

        verify {
            permissionsPreferenceAssistant.saveState(PERMISSION_NAME_1, State.GRANTED)
            permissionsPreferenceAssistant.saveState(PERMISSION_NAME_2, State.GRANTED)
        }
    }

    private fun initializePermissionManager(
        permission: Permission = PERMISSION,
        savedState: State = State.NOT_ASKED,
        androidSdkVersion: Int = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
    ) {
        every { SdkProvider.provide() } returns androidSdkVersion
        every { permissionsPreferenceAssistant.getState(PERMISSION_NAME_1) } returns savedState
        permissionManager = PermissionManagerImpl(
            activity = activity,
            permissionsPreferenceAssistant = permissionsPreferenceAssistant,
            permission = permission
        )
    }

    private fun initializeCoupledPermissionManager(
        permission: Permission = COUPLED_PERMISSION,
        firstPermissionSavedState: State = State.NOT_ASKED,
        secondPermissionSavedState: State = State.NOT_ASKED,
        androidSdkVersion: Int = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
    ) {
        every { SdkProvider.provide() } returns androidSdkVersion
        every { permissionsPreferenceAssistant.getState(PERMISSION_NAME_1) } returns firstPermissionSavedState
        every { permissionsPreferenceAssistant.getState(PERMISSION_NAME_2) } returns secondPermissionSavedState
        permissionManager = PermissionManagerImpl(
            activity = activity,
            permissionsPreferenceAssistant = permissionsPreferenceAssistant,
            permission = permission,
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

    private companion object {
        const val PERMISSION_NAME_1 = "permission_name_1"
        const val PERMISSION_NAME_2 = "permission_name_2"
        val PERMISSION = Permission.Single(
            name = PERMISSION_NAME_1,
            minSdk = 1
        )
        val COUPLED_PERMISSION = Permission.Coupled(
            names = listOf(
                PERMISSION_NAME_1,
                PERMISSION_NAME_2,
            ),
            minSdk = 1
        )
    }
}
