plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.jasonkhew96.fff"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jasonkhew96.fff"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["debug"]
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
    packaging {
        resources {
            excludes += "**"
            merges += "META-INF/xposed/*"
        }
    }
}

dependencies {
    // libxposed
    compileOnly(project(":libxposed-compat"))
    compileOnly(libs.libxposed.api)
    implementation(libs.libxposed.service)
    implementation(libs.kotlinx.coroutines.core)
}

val deleteAppMetadata = task("deleteAppMetadata") {
    doLast {
        file("build/intermediates/app_metadata/release/writeReleaseAppMetadata/app-metadata.properties").writeText(
            ""
        )
    }
}

afterEvaluate {
    tasks.named("mergeReleaseArtProfile").get().enabled = false
    tasks.named("compileReleaseArtProfile").get().enabled = false
    tasks.named("extractReleaseVersionControlInfo").get().enabled = false
    tasks.named("writeReleaseAppMetadata").get().finalizedBy(deleteAppMetadata)
}
