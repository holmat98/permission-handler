buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath(Google.PlayServices.OssLicenses.CLASSPATH)
    }
}
plugins {
    id(AndroidGitVersion.PLUGIN) version AndroidGitVersion.VERSION apply false
}
