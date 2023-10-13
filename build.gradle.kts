import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin) apply false
}

val verCode by extra(100)
val verName by extra("1.0.0")

val androidTargetSdkVersion by extra(34)
val androidMinSdkVersion by extra(27)
val androidBuildToolsVersion by extra("34.0.0")
val androidCompileSdkVersion by extra(34)
val androidCompileNdkVersion by extra("25.2.9519653")
val androidSourceCompatibility by extra(JavaVersion.VERSION_17)
val androidTargetCompatibility by extra(JavaVersion.VERSION_17)
subprojects {
    plugins.withType(AndroidBasePlugin::class.java) {
        extensions.configure(CommonExtension::class.java) {
            print("ss $name")

            compileSdk = androidCompileSdkVersion
            ndkVersion = androidCompileNdkVersion
            buildToolsVersion = androidBuildToolsVersion

            externalNativeBuild {
                cmake {
                    version = "3.22.1+"
                }
            }

            defaultConfig {
                minSdk = androidMinSdkVersion
                if(this is ApplicationDefaultConfig) {
                    targetSdk = androidTargetSdkVersion
                    versionCode = verCode
                    versionName = verName
                }
            }
            
            lint {
                abortOnError = true
                checkReleaseBuilds = false
            }

            compileOptions {
                sourceCompatibility = androidSourceCompatibility
                targetCompatibility = androidTargetCompatibility
            }
        }
    }
}