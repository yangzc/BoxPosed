enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://www.jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://www.jitpack.io")
        }
        mavenLocal {
            content {
                includeGroup("io.github.libxposed")
            }
        }
    }
}

rootProject.name = "BoxPosed"
include(":app")
include(":core")
//include("external:lsplant:lsplant")
include(":test_plugin")
include(":api")
