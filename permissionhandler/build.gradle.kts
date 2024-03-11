plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.ANDROID_KOTLIN)
    id(Plugins.JACOCO)
}

android.configure(
    namespace = "com.mateuszholik.permissionhandler",
    isUsingCompose = true
)

dependencies {
    coreKtx()
    implementation(AndroidX.Compose.UI)
    implementation(AndroidX.ActivityCompose.DEPENDENCY)
    unitTesting()
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}
