plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.prism.android.features.${MODULE_NAME}.api"
}

dependencies {
    implementation(projects.libraries.architecture)
}
