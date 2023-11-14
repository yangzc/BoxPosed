@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
//    alias(libs.plugins.application)
    alias(libs.plugins.library)
    `maven-publish`
}

android {
    namespace = "cn.xutils.boxposed.api"
//    compileSdk = 33

    defaultConfig {
//        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")

        multiDexEnabled = false
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(projects.core)
    compileOnly(libs.androidx.annotation)
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.espresso.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "cn.xutils.boxposed"
            artifactId = "api"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}



