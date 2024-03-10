plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.ANDROID_KOTLIN)
    id(AndroidGitVersion.PLUGIN)
    id(Google.PlayServices.OssLicenses.PLUGIN)
}

androidGitVersion {
    format = AndroidGitVersion.FORMAT
    parts = AndroidGitVersion.PARTS
    multiplier = AndroidGitVersion.MULTIPLIER
}

android.configure(
    namespace = "com.mateuszholik.permissionhandler.sampleapp",
    versionCode = androidGitVersion.code().coerceAtLeast(1),
    versionName = androidGitVersion.name() ?: "1.0.0"
)

dependencies {

    // modules
    module(name = ":permissionhandler")

    // dependencies
    coreKtx()
    lifecycle()
    activity()
    compose()
    ossLicences()
}
