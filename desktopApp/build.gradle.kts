plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}

group = "com.isogelun.sijinote"
version = "1.0.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":shared"))
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "com.isogelun.sijinote.desktop.MainKt"
    }
}
