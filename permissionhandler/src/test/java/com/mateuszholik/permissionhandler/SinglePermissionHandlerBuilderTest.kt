package com.mateuszholik.permissionhandler

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SinglePermissionHandlerBuilderTest {

    private val context = mockk<Context> {
        every { getSharedPreferences(any(), any()) } returns MOCKED_SHARED_PREFS
    }

    @Test
    fun `SinglePermissionHandler builder method correctly builds the SinglePermissionHandler object`() {
        val handler = SinglePermissionHandler.builder(context) {
            permission = "permission"
            isOptional = false
            minSdk = 20
        }

        assertThat(handler).isInstanceOf(SinglePermissionHandler::class.java)
    }

    @Test
    fun `SinglePermissionHandler builder method correctly builds the SinglePermissionHandler object when the isOptional and minSdk is not provided`() {
        val handler = SinglePermissionHandler.builder(context) {
            permission = "permission"
        }

        assertThat(handler).isInstanceOf(SinglePermissionHandler::class.java)
    }

    @Test
    fun `SinglePermissionHandler builder method throws IllegalStateException when permission is skipped`() {

        assertThrows<IllegalStateException> {
            SinglePermissionHandler.builder(context) {
                isOptional = true
                minSdk = 1
            }
        }
    }

    private companion object {
        val MOCKED_SHARED_PREFS = mockk<SharedPreferences> {
            every { getString(any(), null) } returns null
        }
    }
}
