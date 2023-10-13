import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
val kotlinJvmTargetVersion by extra(JvmTarget.JVM_17)

subprojects {
    plugins.withType(AndroidBasePlugin::class.java) {
        extensions.configure(CommonExtension::class.java) {
            print("extension name is: $name\n")

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
                if (this is ApplicationDefaultConfig) {
                    targetSdk = androidTargetSdkVersion
                    versionCode = verCode
                    versionName = verName
                }

                externalNativeBuild {
                    cmake {
                        arguments.addAll(
                            arrayOf(
                                "-DCORE_ROOT=${File(rootDir.absolutePath, "core")}",
//                                "-DANDROID_STL=none", // 不引用Android标准库
                            )
                        )
                        val flags = arrayOf(
                            "-Wno-gnu-string-literal-operator-template",
                            "-Wno-c++2b-extensions",
//                            "-std=c++11"
                        )
                        cFlags.addAll(flags)
                        cppFlags.addAll(flags)
                        abiFilters("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    }
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

    //参考:https://kotlinlang.org/docs/gradle-compiler-options.html
    tasks
        .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>()
        .configureEach {
            compilerOptions.jvmTarget.set(kotlinJvmTargetVersion)
        }
}
