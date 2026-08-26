plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

// JitPack (and `./gradlew publish -Pgroup=... -Pversion=...`) passes `group`/`version` as project
// properties before this script runs. Gradle's own default for an unset subproject `group` is the
// *root project's name*, not blank, so check the raw property directly rather than `group.toString()`.
// JitPack also passes the GitHub org's literal casing (e.g. "com.github.RBWare") - Maven group ids
// are conventionally all lowercase, so normalize whatever we end up with.
group = providers.gradleProperty("group").orNull?.lowercase() ?: "com.rbware"
if (version == Project.DEFAULT_VERSION) {
    version = "0.1.0"
}

android {
    namespace = "com.rbware.rbverticalticker"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.preview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "verticalticker"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
