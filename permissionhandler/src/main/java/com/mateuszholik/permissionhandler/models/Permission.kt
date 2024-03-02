package com.mateuszholik.permissionhandler.models

sealed interface Permission {

    val permissions: List<String>
    val minSdk: Int

    data class Single(
        val name: String,
        override val minSdk: Int,
    ) : Permission {

        override val permissions: List<String> = listOf(name)
    }

    data class Coupled(
        override val permissions: List<String>,
        override val minSdk: Int,
    ) : Permission
}
