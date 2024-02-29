object Plugins {
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val ANDROID_KOTLIN = "org.jetbrains.kotlin.android"
}

object DefaultConfig {
    const val COMPILE_SDK = 34
    const val MIN_SDK = 26
    const val TARGET_SDK = 34
    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
    const val CONSUMER_RULES_FILE = "consumer-rules.pro"
}

object Proguard {
    const val FILE = "proguard-android-optimize.txt"
    const val RULES = "proguard-rules.pro"
}

object AndroidX {

    object CoreKtx {
        private const val version = "1.12.0"

        const val DEPENDENCY = "androidx.core:core-ktx:$version"

        object Testing {
            private const val version = "2.2.0"

            const val DEPENDENCY = "androidx.arch.core:core-testing:$version"
        }
    }

    object Lifecycle {
        private const val version = "2.6.2"

        const val DEPENDENCY = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }

    object ActivityCompose {
        private const val version = "1.8.0"

        const val DEPENDENCY = "androidx.activity:activity-compose:$version"
    }

    object Compose {
        private const val composeVersion = "1.5.2"

        const val KOTLIN_COMPILER_EXTENSION_VERSION = "1.5.10"
        const val UI = "androidx.compose.ui:ui:$composeVersion"
        const val MATERIAL = "androidx.compose.material3:material3:1.2.0-alpha02"
        const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$composeVersion"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val NAVIGATION = "androidx.navigation:navigation-compose:2.7.3"
        const val TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest:$composeVersion"
    }
}

object Testing {

    object JUnit {
        private const val version = "5.9.1"

        const val DEPENDENCY = "org.junit.jupiter:junit-jupiter:$version"
        const val API_DEPENDENCY = "org.junit.jupiter:junit-jupiter-api:$version"
        const val ENGINE = "org.junit.jupiter:junit-jupiter-engine:$version"
        const val PARAMS = "org.junit.jupiter:junit-jupiter-params:$version"
    }

    object AssertJ {
        private const val version = "3.21.0"

        const val DEPENDENCY = "org.assertj:assertj-core:$version"
    }

    object Turbine {
        private const val version = "1.0.0"

        const val DEPENDENCY = "app.cash.turbine:turbine:$version"
    }

    object Mockk {
        private const val version = "1.13.5"

        const val DEPENDENCY = "io.mockk:mockk:$version"

        object Android {
            const val DEPENDENCY = "io.mockk:mockk-android:$version"
        }
    }
}

object AndroidGitVersion {
    const val VERSION = "0.4.14"
    const val PLUGIN = "com.gladed.androidgitversion"
    const val FORMAT = "%tag%%-commit%%-dirty%"
    const val PARTS = 4
    const val MULTIPLIER = 100
}
