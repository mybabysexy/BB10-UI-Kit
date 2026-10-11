plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

group = "duc1607"
version = "0.1.0"

android {
    // Public package: duc1607.bb10.designsystem.*
    namespace = "duc1607.bb10.designsystem"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    publishing {
        singleVariant("release") { withSourcesJar() }
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

// ./gradlew :bb10-kotlin-ui-kit:publishToMavenLocal  ->  duc1607:bb10-kotlin-ui-kit:<version> in ~/.m2
afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                artifactId = "bb10-kotlin-ui-kit"
                pom {
                    name.set("BB10 Kotlin UI Kit")
                    description.set("A BlackBerry 10 style Jetpack Compose UI kit for square screens, d-pad and keyboard first.")
                    licenses {
                        license {
                            name.set("Creative Commons Attribution-NonCommercial 4.0 International")
                            url.set("https://creativecommons.org/licenses/by-nc/4.0/legalcode")
                            distribution.set("repo")
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)

    // JVM tests of focus / d-pad behaviour (Robolectric + Compose UI test)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test-manifest")
}
