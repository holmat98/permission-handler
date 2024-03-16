plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.ANDROID_KOTLIN)
    id(Plugins.JACOCO)
    id(Plugins.MAVEN_PUBLISH)
    id(AndroidGitVersion.PLUGIN)
}

androidGitVersion {
    format = AndroidGitVersion.FORMAT
    parts = AndroidGitVersion.PARTS
    multiplier = AndroidGitVersion.MULTIPLIER
}

android.configure(
    namespace = "com.mateuszholik.permissionhandler",
    isUsingCompose = true
)

task(name ="sourceJar", type = Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                groupId = "com.github.holmat98"
                artifactId = "permission-handler"
                version = androidGitVersion.name()
                artifact("$projectDir/build/outputs/aar/permissionhandler-release.aar")
                artifact(tasks.named<Jar>("sourceJar"))

                pom {
                    withXml {
                        val dependenciesNode = asNode().appendNode("dependencies")

                        configurations.implementation.orNull
                            ?.dependencies
                            ?.filter { it.version != "undefined" && it.group != "org.jacoco" }
                            ?.forEach {
                                val node = dependenciesNode.appendNode("dependency")

                                node.run {
                                    appendNode("groupId", it.group)
                                    appendNode("artifactId", it.name)
                                    appendNode("version", it.version)
                                }
                            }
                    }

                    licenses {
                        license {
                            name = "Apache License 2.0"
                            url = "https://github.com/holmat98/permission-handler/blob/main/LICENSE"
                            distribution = "repo"
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    coreKtx()
    implementation(AndroidX.Compose.UI)
    implementation(AndroidX.ActivityCompose.DEPENDENCY)
    unitTesting()
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}
