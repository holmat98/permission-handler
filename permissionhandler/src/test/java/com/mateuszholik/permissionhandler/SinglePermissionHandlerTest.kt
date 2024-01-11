package com.mateuszholik.permissionhandler

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import app.cash.turbine.test
import com.mateuszholik.permissionhandler.models.Permission
import com.mateuszholik.permissionhandler.models.PermissionState
import com.mateuszholik.permissionhandler.models.SinglePermissionState
import com.mateuszholik.permissionhandler.providers.SdkProvider
import com.mateuszholik.permissionhandler.utils.PermissionsPreferenceAssistant
import org.assertj.core.api.Assertions.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class SinglePermissionHandlerTest {

    private val context = mockk<Context>()
    private val permissionsPreferenceAssistant =
        mockk<PermissionsPreferenceAssistant>(relaxed = true)

    private lateinit var permissionHandler: SinglePermissionHandler

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

    @TestFactory
    fun testInitialState() =
        listOf(
            PermissionState.NOT_ASKED to SinglePermissionState.AskForPermission(PERMISSION.name),
            PermissionState.SKIPPED to SinglePermissionState.ShowRationale(PERMISSION.name),
            PermissionState.SHOW_RATIONALE to SinglePermissionState.ShowRationale(PERMISSION.name),
            PermissionState.DENIED to SinglePermissionState.Denied,
        ).map { (permissionState, expectedResult) ->
            dynamicTest("When saved permission state is equal to $permissionState then the initial state is equal to $expectedResult") {
                runTest {
                    initializePermissionHandler(savedPermissionState = permissionState)

                    permissionHandler.observe().test {
                        assertThat(awaitItem()).isEqualTo(expectedResult)
                    }
                }
            }
        }

    @Test
    fun `When saved permission state is GRANTED then the initial state is equal to Granted`() =
        runTest {
            mockkContextCompat(isGranted = true)
            initializePermissionHandler(savedPermissionState = PermissionState.GRANTED)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Granted)
            }

            verify(exactly = 0) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.SHOW_RATIONALE
                )
            }
        }

    @Test
    fun `When saved permission state is GRANTED and permission was removed in settings then the initial state is equal to ShowRationale`() =
        runTest {
            mockkContextCompat(isGranted = false)
            initializePermissionHandler(savedPermissionState = PermissionState.GRANTED)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(SinglePermissionState.ShowRationale(PERMISSION.name))
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.SHOW_RATIONALE
                )
            }
        }

    @Test
    fun `When Android version is lower than min sdk for the permission then initial state is equal to Granted`() =
        runTest {
            initializePermissionHandler(
                permission = PERMISSION.copy(minSdk = 33),
                androidSdkVersion = 32
            )

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Granted)
            }
        }

    @Test
    fun `When permission is optional and it was denied by the user then Skipped is the current state`() =
        runTest {
            initializePermissionHandler(permission = PERMISSION.copy(isOptional = true))

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.AskForPermission(
                        PERMISSION.name
                    )
                )

                permissionHandler.handlePermissionResult(isGranted = false)

                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Skipped)
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.SKIPPED
                )
            }
        }

    @Test
    fun `When permission was denied by the user then ShowRationale is the current state`() =
        runTest {
            initializePermissionHandler()

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.AskForPermission(
                        PERMISSION.name
                    )
                )

                permissionHandler.handlePermissionResult(isGranted = false)

                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.ShowRationale(
                        PERMISSION.name
                    )
                )
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.SHOW_RATIONALE
                )
            }
        }

    @Test
    fun `When permission was granted by the user then Granted is the current state`() =
        runTest {
            initializePermissionHandler()

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.AskForPermission(
                        PERMISSION.name
                    )
                )

                permissionHandler.handlePermissionResult(isGranted = true)

                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Granted)
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.GRANTED
                )
            }
        }

    @Test
    fun `When current state is ShowRationale and permission was denied then Denied is the current state`() =
        runTest {
            initializePermissionHandler(savedPermissionState = PermissionState.SHOW_RATIONALE)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.ShowRationale(
                        PERMISSION.name
                    )
                )

                permissionHandler.handlePermissionResult(isGranted = false)

                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Denied)
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.DENIED
                )
            }
        }

    @Test
    fun `When current state is ShowRationale and permission was granted then Granted is the current state`() =
        runTest {
            initializePermissionHandler(savedPermissionState = PermissionState.SHOW_RATIONALE)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.ShowRationale(
                        PERMISSION.name
                    )
                )

                permissionHandler.handlePermissionResult(isGranted = true)

                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Granted)
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.GRANTED
                )
            }
        }

    @Test
    fun `When current state is Denied and permission was granted in the settings then Granted is the current state`() =
        runTest {
            mockkContextCompat(isGranted = true)
            initializePermissionHandler(savedPermissionState = PermissionState.DENIED)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.Denied
                )

                permissionHandler.handleBackFromSettings()

                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Granted)
            }

            verify(exactly = 1) {
                permissionsPreferenceAssistant.savePermissionState(
                    PERMISSION.name,
                    PermissionState.GRANTED
                )
            }
        }

    @Test
    fun `When current state is Denied and permission was not granted in the settings then Denied is the current state`() =
        runTest {
            mockkContextCompat(isGranted = false)

            initializePermissionHandler(savedPermissionState = PermissionState.DENIED)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.Denied
                )

                permissionHandler.handleBackFromSettings()

                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Denied)
            }

            verify(exactly = 0) {
                permissionsPreferenceAssistant.savePermissionState(PERMISSION.name, any())
            }
        }

    @Test
    fun `SinglePermissionHandler handles correctly the whole flow of the permission granting process`() =
        runTest {
            mockkContextCompat(isGranted = true)
            initializePermissionHandler()

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.AskForPermission(PERMISSION.name)
                )

                permissionHandler.handlePermissionResult(false)

                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.ShowRationale(PERMISSION.name)
                )

                permissionHandler.handlePermissionResult(false)

                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.Denied
                )

                permissionHandler.handleBackFromSettings()

                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.Granted
                )
            }
        }

    @Test
    fun `When handleBackFromSettings is called when current state is different than Denied then IllegalStateException is thrown`() =
        runTest {
            initializePermissionHandler()

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(
                    SinglePermissionState.AskForPermission(
                        PERMISSION.name
                    )
                )

                assertThrows<IllegalStateException> {
                    permissionHandler.handleBackFromSettings()
                }
            }
        }

    @Test
    fun `When handlePermissionResult is called when current state is different than AskForPermission or ShowRationale then IllegalStateException is thrown`() =
        runTest {
            initializePermissionHandler(savedPermissionState = PermissionState.DENIED)

            permissionHandler.observe().test {
                assertThat(awaitItem()).isEqualTo(SinglePermissionState.Denied)

                assertThrows<IllegalStateException> {
                    permissionHandler.handlePermissionResult(isGranted = false)
                }
            }
        }

    private fun initializePermissionHandler(
        permission: Permission = PERMISSION,
        savedPermissionState: PermissionState = PermissionState.NOT_ASKED,
        androidSdkVersion: Int = Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
    ) {
        every { SdkProvider.provide() } returns androidSdkVersion
        every { permissionsPreferenceAssistant.getPermissionState(PERMISSION.name) } returns savedPermissionState
        permissionHandler = SinglePermissionHandlerImpl(
            context = context,
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
        every { ContextCompat.checkSelfPermission(context, PERMISSION.name) } returns result
    }

    private companion object {
        val PERMISSION = Permission(
            name = "permission_name",
            isOptional = false,
            minSdk = 1
        )
    }
}
