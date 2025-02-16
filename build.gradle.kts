buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath(Google.PlayServices.OssLicenses.CLASSPATH)
    }
}
plugins {
    id(AndroidX.Compose.COMPILER_PLUGIN) version AndroidX.Compose.KOTLIN_COMPILER_PLUGIN_VERSION apply false
    id(AndroidGitVersion.PLUGIN) version AndroidGitVersion.VERSION apply false
}
