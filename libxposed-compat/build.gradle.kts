plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "io.github.libxposed"
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
