plugins {
    alias(libs.plugins.library)
//    alias(libs.plugins.kotlin)

}

android {
    namespace = "cn.xutils.boxposed.core"

    defaultConfig {

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")

        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
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
    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {

//    implementation("androidx.core:core-ktx:1.9.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.8.0")
    api(libs.libxposed.api)
    implementation(libs.commons.lang3)
    compileOnly(libs.androidx.annotation)
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

afterEvaluate {
//    logger.quiet(File(android.sdkDirectory, "build-tools/30.0.3/dx").absolutePath)
}

task("makeDex") {
    dependsOn("assemble").doLast {
        logger.quiet("makeDex is running done")
        var buildDir = project(":core").layout.buildDirectory.get()
            .asFile.absolutePath
        logger.quiet("${File(buildDir, "intermediates/aar_main_jar/debug/classes.jar").exists()}")
        exec {
            setWorkingDir(File(buildDir, "intermediates/aar_main_jar/debug/").absolutePath)
            commandLine(
                File(android.sdkDirectory, "build-tools/30.0.3/dx").absolutePath,
                "--dex",
                "--output=classes.dex",
                "classes.jar"
            )
        }
    }
}