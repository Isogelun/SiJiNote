import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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

        nativeDistributions {
            // createDistributable 产出带 .exe 启动器的应用目录，不需要 WiX。
            // packageMsi 需要安装 WiX Toolset v3 后才会成功。
            targetFormats(TargetFormat.Msi)
            packageName = "SiJiNote"
            packageVersion = "1.0.0"
            description = "思迹笔记 · 跨端笔记应用"
            vendor = "isogelun"

            windows {
                menuGroup = "思迹笔记"
                shortcut = true
                dirChooser = true
                perUserInstall = true
                // 升级时保持同一标识，便于覆盖安装
                upgradeUuid = "8F3A6C2E-4B71-4D95-9A2C-1E7B5F0D3A64"
            }
        }
    }
}
