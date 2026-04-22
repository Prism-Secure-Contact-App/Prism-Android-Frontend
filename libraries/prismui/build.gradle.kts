/*
 * Compatibility alias module for legacy references.
 */

plugins {
    id("io.prism.android-library")
}

android {
    namespace = "io.element.android.libraries.prism.ui"
}

dependencies {
    api(projects.libraries.matrixui)
}
