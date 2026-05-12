import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.library)
    id("maven-publish")
}

android {
    namespace = "xyz.teamgravity.coresdkandroid"

    compileSdk {
        version = release(libs.versions.sdk.compile.get().toInt()) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
        consumerProguardFiles.add(file("proguard-rules.pro"))
    }

    lint {
        targetSdk = libs.versions.sdk.target.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    // core
    implementation(libs.core)

    // activity
    implementation(libs.activity)

    // preferences
    implementation(libs.preferences)

    // update
    implementation(libs.update)

    // review
    implementation(libs.review)

    // coroutines
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.services)

    // timber
    implementation(libs.timber)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.raheemadamboev"
            artifactId = "core-sdk-android"
            version = "1.0.46"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.io.encoding.ExperimentalEncodingApi"
        )
    }
}