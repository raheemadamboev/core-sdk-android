plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    id("maven-publish")
}

android {
    namespace = "xyz.teamgravity.coresdkandroid"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }

    lint {
        targetSdk = libs.versions.sdk.target.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvm.target.get()
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.io.encoding.ExperimentalEncodingApi"
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.raheemadamboev"
                artifactId = "core-sdk-android"
                version = "1.0.10"
            }
        }
    }
}