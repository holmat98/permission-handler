plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.ANDROID_KOTLIN)
}

android.configure(
    namespace = "com.mateuszholik.permissionhandler",
    isUsingCompose = true
)

dependencies {
    coreKtx()
    compose()
    unitTesting()
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}
