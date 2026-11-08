# SiJiNote 单模块跨端 UI 共享方案（含桌面端）

更新日期：2026-11-01（Asia/Shanghai）。

状态：**单模块源码集分层和最小共享文本界面已落地，业务页面、公共导航和自适应布局待开发**。日常开发以 [共享界面开发指南](shared_ui_development_guide_zh.md) 为准。

## 当前实现快照（2026-11-08）

- 公共根界面为 `shared/src/commonMain/kotlin/com/isogelun/sijinote/app/Index.kt` 中的 `IndexScreen()`：内部持有 `AppState`，通过 `UiAdaptiveLayout(breakpointDp = 900)` 按容器宽度在 `DesktopShell`（三栏）与 `MobileShell`（单栏 + 底部导航）之间切换。
- 中立组件层在 `ui/`：`UiTheme.kt` 定义颜色、字重、文本层级与图标矢量数据；`Ui.kt` 声明 `UiText`、`UiColumn`、`UiRow`、`UiBox`、`UiSpacer`、`UiDivider`、`UiTextField`、`UiIcon`、`UiAdaptiveLayout`，以及列/行作用域的 `Flex`。
- Kuikly 入口为 `kuiklyMain/.../app/Kuikly.kt`，`Kuikly` 直接继承 `ComposeContainer`，注册 `@Page("index")` 并调用公共页面；桌面入口装配为 `composeMain/.../app/Compose.kt` 的 `runSiJiNoteDesktop()`。
- 页面按功能分目录：`app/note`（列表面板、桌面编辑区、移动编辑页、卡片与正文块）、`app/search`、`app/favorites`、`app/settings`、`app/shell`（两端外壳）、`app/components`（角标、图标按钮、头像）。
- 演示数据在 `data/SampleNotes.kt`，仅存在于内存；`NoteRepository` 尚无实现，重启后回到初始数据。
- 主构建使用 `jvm("compose")`，桌面源码集为 `composeMain`；主脚本的 Kuikly 依赖位于 `kuiklyMain`。关闭默认层级模板，手工连接 Kuikly 各 target。
- 主构建设置 `kuikly.useLocalKsp=true`，手工按 Android、JS、三个 iOS target 添加处理器。
- Android、H5、iOS、鸿蒙宿主默认页已改为 index；小程序未在本轮完成启动验证。
- Kuikly 侧没有公开的 `verticalScroll` 修饰符，可滚动列在 Kuikly actual 中用 `LazyColumn` 承载单个内容项，桌面 actual 使用 `verticalScroll`。
- 本轮改动后未执行编译或运行验证；桌面与移动端的实际渲染结果仍需分别运行确认。


## 以下为首轮设计基线（历史方案）

以下章节保留初始设计及其论证，包含尚未实施的 DesktopRouter、UnknownPageScreen、旧目录和搬迁清单，不能直接当作当前工程结构或完整验收结果。与当前快照冲突时，以本节快照、开发指南及实际源码为准；新业务页面计划通过公共根界面管理导航，不要求每新增页面都添加一对平台路由。

## 0. 与既有文档的关系

`docs/shared_ui_architecture_zh.md` 提出 `shared-core` / `shared-ui` 双模块结构。本文改为**单模块内 source set 分层**，不新建任何模块，其余思路（`expect/actual`、源码集隔离、适配层克制、页面插槽）沿用。

差异对照：

| 项 | 旧文档 | 本文 |
| --- | --- | --- |
| 模块划分 | 新增 `shared-core`、`shared-ui` | 只保留 `shared`，新增 source set |
| 业务代码位置 | `shared-core/commonMain` | `shared/src/commonMain` |
| 界面代码位置 | `shared-ui/commonMain` | `shared/src/commonMain` |
| 组件实现位置 | `shared-ui/kuiklyMain`、`shared-ui/composeMain` | `shared/src/kuiklyMain`、`shared/src/composeMain` |
| 路由位置 | 各宿主工程 | **`shared/src/kuiklyMain` 与 `shared/src/composeMain`** |
| 桌面接入 | 依赖 `shared-ui` 的 desktop 变体 | 依赖 `shared` 的 desktop JVM 变体 |

旧文档中关于 `expect/actual` 机制、依赖隔离原则、作用域能力风险、适配层规模控制的论述仍然有效。以下提供拟实施配置与示例，仍需通过实际构建验证。

## 1. 结论

`shared` 单个 KMP 模块内，三个 source set 各司其职：

| source set | 职责 | 覆盖端 |
| --- | --- | --- |
| `commonMain` | 界面 UI（用中立组件编写）、统一调用入口、业务逻辑 | 全部 |
| `kuiklyMain` | Kuikly 组件 `actual` + `@Page` 路由 | Android / iOS / Web / 小程序 / 鸿蒙 |
| `composeMain` | 官方 Compose 组件 `actual` + 桌面路由表 + `Window` 入口 | Windows / macOS / Linux |

宿主保留各自必要的渲染初始化、桥接及平台配置，页面接入时把 `pageName` 指向共享页面。`desktopApp` 的 Kotlin 启动源码只需要一个 `main` 函数和一个 `pageName`，形态与 Android 传入 `pageName` 一致；业务页面结构放在 `commonMain`，组件实现和平台容器放在对应源码集。

共享的边界是**页面结构与业务逻辑**，不是**组件实现**。`commonMain` 中不得出现任何具体 UI 组件调用，只能出现中立组件名。

## 2. 硬约束：Kuikly 没有桌面渲染器

Kuikly 官方支持的平台与渲染器（来源：KuiklyUI README、Maven Central 构件索引）：

| 平台 | 渲染器构件 | 状态 |
| --- | --- | --- |
| Android | `core-render-android` | 正式 |
| iOS | `core-render-ios`（Apple 侧） | 正式 |
| macOS | 复用 iOS Apple 实现 | Alpha |
| 鸿蒙 | `core-render-ohos` | 正式 |
| Web / 小程序 | `core-render-web` | beta |
| **Windows / Linux 桌面（JVM）** | **不存在** | **不支持** |

`com.tencent.kuikly-open` 实际发布的变体：

| 模块 | 变体 | 是否有 JVM |
| --- | --- | --- |
| `core` | android / iosarm64 / iosx64 / iossimulatorarm64 / js / ohosarm64 / **jvm** | 是（`core-jvm`） |
| `core-annotations` | 同上 + **jvm** | 是 |
| `core-ksp` | jvm / iosarm64 | 是（构建期工具） |
| **`compose`** | android / iosarm64 / iosx64 / iossimulatorarm64 / js / ohosarm64 | **否，无 `compose-jvm`** |
| `core-render-*` | 仅 android、web | 否 |

由此推出两条必须遵守的规则：

1. 主构建脚本中的 `com.tencent.kuikly-open:compose` 必须从 `commonMain` 移出，放入 `kuiklyMain`。否则 `shared` 一旦包含 JVM target，桌面依赖解析会因缺少兼容的 Kuikly Compose JVM 变体而失败。鸿蒙独立脚本不包含 JVM target，其公共层依赖例外见第 6.4 节。
2. `@Page` 页面类必须留在 Kuikly 侧 source set。`core-jvm` 虽然存在，使 `Pager`、`Module` 等类型在 JVM 上可解析，但没有渲染器，能编译不代表能运行。

## 3. 关键依据：为什么 `commonMain` 可以只用 Compose Runtime

### 3.1 Compose 编译器插件只认固定包名

Compose 编译器插件识别的注解是 `androidx.compose.runtime.Composable` 这一完全限定名。Kuikly 做包名改写时，改写对象是 UI 层（`androidx.compose.foundation`、`androidx.compose.material3`、`androidx.compose.ui` → `com.tencent.kuikly.compose.*`），**runtime 层必须保留原包名**，否则整个 Compose DSL 无法编译。

现有代码即为佐证：

```kotlin
// shared/src/commonMain/kotlin/com/isogelun/sijinote/RouterPage.kt
import androidx.compose.runtime.Composable          // 保留 androidx
import com.tencent.kuikly.compose.material3.Text   // 改写为 com.tencent.kuikly
```

### 3.2 当前固定版本的 Runtime 依赖声明

已核对本机 Gradle 缓存中的 `com.tencent.kuikly-open:compose:2.7.0-2.1.21` POM，其依赖声明包括：

| 依赖 | 声明版本 | POM scope |
| --- | --- | --- |
| `org.jetbrains.compose.runtime:runtime` | 1.7.3 | runtime |
| `org.jetbrains.compose.runtime:runtime-saveable` | 1.7.3 | runtime |
| `org.jetbrains.compose.collection-internal:collection` | 1.7.3 | runtime |
| `com.tencent.kuikly-open:core` | 2.7.0-2.1.21 | runtime |

主构建中 `commonMain` 显式使用 `compose.runtime`，与上述 Runtime 声明的坐标和版本一致。此处确认的是固定版本的发布依赖声明，不是最终解析结果；各 target 的 Gradle Module Metadata、传递依赖选择及运行兼容性仍需核对，不能仅凭版本一致保证无冲突。

鸿蒙版本线独立核对，不用此 POM 推断其 Runtime 的发布形式或兼容性。

### 3.3 版本一致性要求

当前主构建以 Compose 1.7.3 为统一基线，升级时一起核对以下配置。Compose 插件与 Runtime 的版本号相同并非普遍的兼容性规则；Kotlin Compose 编译器插件在本工程跟随 Kotlin 2.1.21：

- 根 `build.gradle.kts` 的 `org.jetbrains.compose` 插件版本（当前 1.7.3）
- Kuikly 依赖传递的 `org.jetbrains.compose.runtime`（当前 1.7.3）
- 任何显式声明的 `compose.*` 依赖

## 4. 模块与源码集结构

```text
SiJiNote/
├── shared/
│   ├── build.gradle.kts            主构建脚本（Android / iOS / JS / desktop）
│   ├── build.ohos.gradle.kts       鸿蒙构建脚本（Android / iOS / ohosArm64，无 JVM）
│   └── src/
│       ├── commonMain/             界面 + 统一调用 + 业务，写一次
│       │   └── kotlin/com/isogelun/sijinote/
│       │       ├── ui/             中立组件 expect 声明、中立枚举
│       │       ├── screen/         共享页面（IndexScreen 等）
│       │       └── model/          笔记模型、加解密、序列化、同步协议
│       ├── kuiklyMain/             Kuikly 实现 + 移动/Web/鸿蒙路由（新增）
│       │   └── kotlin/com/isogelun/sijinote/
│       │       ├── ui/             actual，调用 com.tencent.kuikly.compose.*
│       │       ├── route/          @Page 页面类（IndexPager）
│       │       └── base/           BasePager、BridgeModule、Utils
│       ├── composeMain/            Compose 实现 + 桌面路由（新增）
│       │   └── kotlin/com/isogelun/sijinote/
│       │       ├── ui/             actual，调用 androidx.compose.*
│       │       └── app/            DesktopRouter：窗口 + pageName → 界面
│       ├── androidMain/            已有
│       ├── iosMain/                已有
│       ├── jsMain/                 已有（Web + 小程序）
│       └── ohosArm64Main/          已有（仅 ohos 脚本）
├── androidApp/                     宿主，只改 pageName
├── iosApp/                         宿主，只改 pageName
├── ohosApp/                        宿主，只改 pageName
├── h5App/                          宿主，只改 pageName
├── miniApp/                        宿主，只改 pageName
└── desktopApp/
    └── src/main/kotlin/Main.kt     main(args) → runSiJiNoteDesktop(pageName)
```

源码集依赖关系：

```text
commonMain
├── kuiklyMain
│   ├── androidMain
│   ├── jsMain
│   ├── iosMain → iosX64 / iosArm64 / iosSimulatorArm64
│   └── ohosArm64Main（仅 ohos 脚本）
└── composeMain → jvm target（命名 compose）
```

`kuiklyMain` 是自定义中间 source set，不是平台，需要显式配置 `dependsOn`。两套构建脚本各自定义它。

## 5. 调用链路

调用方向自上而下：

```text
第 1 层  宿主：只提供一个 pageName
         androidApp / iosApp / ohosApp / h5App / miniApp
         desktopApp
                            │
第 2 层  路由：pageName → 界面，放进该端容器
         ┌──────────────────┴──────────────────┐
         kuiklyMain/route/IndexPager.kt        composeMain/app/DesktopRouter.kt
         @Page("index")                        runSiJiNoteDesktop(pageName)
         setContent { IndexScreen() }          Window { when(pageName) { ... } }
                            │
第 3 层  界面：写一次，两端共用
         commonMain/screen/IndexScreen.kt
         只调用 UiText / UiColumn 等中立组件
                            │
第 4 层  中立组件声明
         commonMain/ui/Ui.kt
         expect fun UiText / UiColumn
                            │
         ┌──────────────────┴──────────────────┐
第 5 层  组件实现
         kuiklyMain/ui/Ui.kt                   composeMain/ui/Ui.kt
         actual → com.tencent.kuikly.compose.*  actual → androidx.compose.*
```

组件实现绑定发生在 Kotlin 编译期，各 target 单独编译时从对应 source set 取 `actual`，不使用运行时组件注册表。路由仍会在运行时根据 `pageName` 查表或分支选择页面。

两端路由在形式上对称：宿主只提供一个 `pageName`，路由层负责把它解析成界面并放进该端容器。差别只在解析机制——移动侧由 KSP 扫描 `@Page` 生成映射，桌面侧是手写查表。

iOS 不需要单独的路由代码：`iosApp` 通过 `pageName = "index"` 进入同一个 `kuikly`。

## 6. Gradle 配置

### 6.1 `shared/build.gradle.kts`（主脚本）

```kotlin
kotlin {
    androidTarget { /* 保持现有配置 */ }

    js(IR) { /* 保持现有配置 */ }

    iosX64(); iosArm64(); iosSimulatorArm64()

    jvm("compose")                 // 新增：桌面三系统，source set 名为 composeMain

    sourceSets {
        // 1) 公共层：仅 Compose Runtime，与 Kuikly 传递依赖同源
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }

        // 2) Kuikly 组：自定义中间 source set
        val kuiklyMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("com.tencent.kuikly-open:core:${Version.getKuiklyVersion()}")
                implementation("com.tencent.kuikly-open:core-annotations:${Version.getKuiklyVersion()}")
                implementation("com.tencent.kuikly-open:compose:${Version.getKuiklyVersion()}")
            }
        }

        val androidMain by getting {
            dependsOn(kuiklyMain)
            dependencies {
                api("com.tencent.kuikly-open:core-render-android:${Version.getKuiklyVersion()}")
            }
        }

        val jsMain by getting {
            dependsOn(kuiklyMain)
        }

        val iosMain by creating {
            dependsOn(kuiklyMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        // 3) 桌面组：官方 Compose + 桌面窗口 API
        val composeMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.desktop.common)   // 桌面 API；原生运行库由宿主选择
            }
        }
    }
}
```

`compose.runtime`、`compose.foundation`、`compose.desktop.common` 等访问器由已应用的 `org.jetbrains.compose` 插件提供。

本方案沿用当前工程手工组织 `iosMain` 的方式，显式配置全部源码集关系。在 `gradle.properties` 中设置：

```properties
kotlin.mpp.applyDefaultHierarchyTemplate=false
```

保留现有 `commonTest`、`iosTest` 及各 iOS test 源码集的手工连接；上面的增量示例省略了这些现有配置。鸿蒙脚本也采用同一手工层级策略，不与自动创建的默认层级混用。

`jvm("compose")` 中的 `compose` 是 target 名，对应 `composeMain`、`composeTest` 和 `compileKotlinCompose` 等名称。它不是新增模块；`desktopApp` 才是桌面宿主模块。采用 `compose` 作为 target 名，使官方 Compose 实现位于 `composeMain`；宿主模块仍为 `desktopApp`。

桌面 target 的 Java/Kotlin 字节码目标需一致，并与宿主的 JDK 17 工具链兼容；不要把 Android 的 JVM 1.8 配置无差别应用到桌面 target。KSP 处理器只配置到 Kuikly targets，检查现有通用 `compileOnly(core-ksp)` 声明是否会使处理器进入桌面依赖，按实际解析结果收窄。

`compose.desktop.common` 只提供桌面窗口 API，不含 skiko 平台二进制，因此 `shared` 的产物不会绑定构建机的操作系统。skiko 由 `desktopApp` 的 `compose.desktop.currentOs` 在运行时提供。

### 6.2 `desktopApp/build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":shared"))          // 提供 composeMain 的入口与 UI
    implementation(compose.desktop.currentOs)   // 提供 skiko 运行时
}

compose.desktop {
    application {
        mainClass = "com.isogelun.sijinote.desktop.MainKt"   // 保持不变
    }
}
```

### 6.3 `desktopApp/src/main/kotlin/Main.kt`

```kotlin
package com.isogelun.sijinote.desktop

import com.isogelun.sijinote.app.runSiJiNoteDesktop

fun main(args: Array<String>) = runSiJiNoteDesktop(args.firstOrNull() ?: "index")
```

`desktopApp` 只剩这一个函数：一个 `main`，一个 `pageName`。窗口创建、窗口标题、窗口状态、主题、页面查表全部位于 `shared/src/composeMain/.../app/DesktopRouter.kt`。

`mainClass` 仍为 `com.isogelun.sijinote.desktop.MainKt`，无需改动。传入 `pageName` 的方式与 Android 的 `KuiklyRenderActivity` 取 `pageName` 一致。

保留现有 `kotlin("jvm")` 宿主。普通 Kotlin/JVM 项目可以消费 KMP 模块的 JVM 产物，预期由 Gradle 按平台属性选择 `shared` 的 desktop JVM 变体，而不是按 target 名匹配。若解析失败，先检查实际候选变体、JVM 版本及依赖属性；不预先改成 KMP，也不把 `withJava()` 当作变体匹配的必要条件。

### 6.4 `shared/build.ohos.gradle.kts`

**鸿蒙继续沿用 Kuikly 原有方案**：保留独立的 settings / build 脚本、Kuikly ohos 版本线（`2.7.0-2.0.21-ohos`，Kotlin `2.0.21-KBA-010`）、原生渲染器、KSP 和 `@Page` 路由。本次不为鸿蒙接入官方 Compose 桌面渲染，也不新增 JVM target。

源码迁移后，在鸿蒙脚本中创建 `kuiklyMain`，使 `androidMain`、`iosMain`、`ohosArm64Main` 都 `dependsOn(kuiklyMain)`；各 iOS target 再依赖 `iosMain`。此脚本不配置 `jsMain` 和 `composeMain`。

为保留原构建的依赖来源，首轮鸿蒙脚本的 `commonMain` **继续保留现有三项 Kuikly ohos 依赖**（`core`、`core-annotations`、`compose`），由原有链路提供公共代码所需的 `androidx.compose.runtime` API。不在鸿蒙脚本中新增 JetBrains `compose.runtime` 或 `compose.desktop.common`；官方 JetBrains 构件不能直接假定具有 `ohosArm64` 变体。

这是鸿蒙独立构建的依赖隔离例外：公共源码仍不得导入 Kuikly UI 类型，但其依赖闭包暂时包含 Kuikly UI。由于该脚本没有桌面 target，此例外不会把 Kuikly 依赖带入主脚本的桌面产物。后续只有在确认鸿蒙 Runtime 能独立解析后，才考虑把 UI 依赖收窄到 `kuiklyMain`。

`commonMain` 使用 `@Composable`，必须从自身依赖中获得 Runtime；父源码集不能反向使用子源码集 `kuiklyMain` 的依赖。实施时仍需验证源码搬迁后的鸿蒙编译、页面注册及运行，保留旧依赖来源不等于新结构已经验收。

## 7. expect / actual 示例

### 公共声明（`commonMain/.../ui/Ui.kt`）

```kotlin
package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable

enum class UiTextRole { Title, Body, Caption }

@Composable
expect fun UiText(text: String, role: UiTextRole = UiTextRole.Body)

@Composable
expect fun UiColumn(
    paddingDp: Int = 0,
    spacingDp: Int = 0,
    fillMaxWidth: Boolean = false,
    content: @Composable () -> Unit
)
```

### 共享界面（`commonMain/.../screen/IndexScreen.kt`）

```kotlin
package com.isogelun.sijinote.screen

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.ui.*

@Composable
fun IndexScreen(noteCount: Int) {
    UiColumn(paddingDp = 16, spacingDp = 12, fillMaxWidth = true) {
        UiText("我的笔记", UiTextRole.Title)
        UiText("共 $noteCount 条", UiTextRole.Caption)
    }
}

@Composable
fun UnknownPageScreen(pageName: String) {
    UiColumn(paddingDp = 16) {
        UiText("页面不存在", UiTextRole.Title)
        UiText("未知页面：$pageName")
    }
}
```

### Kuikly 实现（`kuiklyMain/.../ui/Ui.kt`）

```kotlin
package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth as kuiklyFillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp

@Composable
actual fun UiText(text: String, role: UiTextRole) {
    val fontSize = when (role) {
        UiTextRole.Title -> 24.sp
        UiTextRole.Body -> 16.sp
        UiTextRole.Caption -> 12.sp
    }
    Text(text = text, fontSize = fontSize)
}

@Composable
actual fun UiColumn(
    paddingDp: Int,
    spacingDp: Int,
    fillMaxWidth: Boolean,
    content: @Composable () -> Unit
) {
    val widthModifier = if (fillMaxWidth) Modifier.kuiklyFillMaxWidth() else Modifier
    Column(
        modifier = widthModifier.padding(paddingDp.dp),
        verticalArrangement = Arrangement.spacedBy(spacingDp.dp)
    ) { content() }
}
```

### Compose 实现（`composeMain/.../ui/Ui.kt`）

```kotlin
package com.isogelun.sijinote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth as desktopFillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
actual fun UiText(text: String, role: UiTextRole) {
    val fontSize = when (role) {
        UiTextRole.Title -> 24.sp
        UiTextRole.Body -> 16.sp
        UiTextRole.Caption -> 12.sp
    }
    Text(text = text, fontSize = fontSize)
}

@Composable
actual fun UiColumn(
    paddingDp: Int,
    spacingDp: Int,
    fillMaxWidth: Boolean,
    content: @Composable () -> Unit
) {
    val widthModifier = if (fillMaxWidth) Modifier.desktopFillMaxWidth() else Modifier
    Column(
        modifier = widthModifier.padding(paddingDp.dp),
        verticalArrangement = Arrangement.spacedBy(spacingDp.dp)
    ) { content() }
}
```

这组基础组件的两份 `actual` 结构接近，具体 UI 类型各自留在实现侧。`paddingDp`、`spacingDp` 为非负 dp 整数，默认均为 0；`fillMaxWidth = true` 表示填满父布局允许的宽度，false 使用框架默认宽度行为。实现不重复声明默认参数，且不得忽略公共参数。这里的 API 组合仍需在固定版本上编译、运行验证，不据此推断输入、列表或动画组件只需替换 import。

### 路由 · 移动 / Web / 小程序 / 鸿蒙（`kuiklyMain/.../route/IndexPager.kt`）

```kotlin
package com.isogelun.sijinote.route

import com.isogelun.sijinote.base.BasePager
import com.isogelun.sijinote.screen.IndexScreen
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.compose.setContent

@Page("index", supportInLocal = true)
internal class IndexPager : BasePager() {
    override fun willInit() {
        super.willInit()
        setContent { IndexScreen(noteCount = 0) }
    }
}
```

### 路由 · 桌面（`composeMain/.../app/DesktopRouter.kt`）

```kotlin
package com.isogelun.sijinote.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.isogelun.sijinote.screen.IndexScreen
import com.isogelun.sijinote.screen.UnknownPageScreen

fun runSiJiNoteDesktop(pageName: String = "index") = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = pageTitle(pageName),
        state = rememberWindowState(width = 1000.dp, height = 700.dp)
    ) {
        MaterialTheme { DesktopPageHost(pageName) }
    }
}

@Composable
fun DesktopPageHost(pageName: String) {
    when (pageName) {
        "index" -> IndexScreen(noteCount = 0)
        else -> UnknownPageScreen(pageName)
    }
}

private fun pageTitle(pageName: String) = when (pageName) {
    "index" -> "四季笔记"
    else -> "四季笔记 · $pageName"
}
```

页面查表只有 `DesktopPageHost` 一处，新增页面只需加一个分支。

`IndexScreen`、`UnknownPageScreen` 均为上文 `commonMain` 中的界面函数。现有 Kuikly `router` 模板不自动成为桌面页面；若以后需要共享路由选择界面，先在公共层实现，再添加桌面映射。`composeMain` 只负责「页标识 → 界面」的映射与窗口装配，不承载业务界面。

### 桌面为什么不能复用 @Page

现有 `@Page` 处理链路面向 Kuikly `Pager` 页面及其渲染容器，不能直接作为官方 Compose 桌面的页面宿主。本方案选择手写桌面路由表以减少首轮改造；这不是说桌面路由只能手写，后续也可以设计中立的路由映射或代码生成，但不在本轮范围内。

两端路由的对应关系：

| 端 | 页标识 | 页面解析方式 | 渲染容器 |
| --- | --- | --- | --- |
| Android / iOS / Web / 小程序 / 鸿蒙 | `pageName = "index"` | KSP 扫描 `@Page` 生成映射 | `setContent { IndexScreen() }` |
| 桌面 | `pageName = "index"` | `DesktopPageHost` 的 `when` 手写查表 | `application { Window { ... } }` |

机制不同，形式对齐。新增页面时移动侧写一个 `@Page` 类，桌面侧加一个 `when` 分支，两边最终调用的都是 `commonMain` 里同一个界面函数。

`UnknownPageScreen` 是兜底页，保证桌面端收到未知 `pageName` 时不崩溃。Kuikly 侧的对应行为由 `RouterModule` 决定，两端不必强求一致。

## 8. 适配层成本分级

Kuikly 官方 Compose 能力文档的 API 对齐度说明不能换算为本项目的组件适配成本或共享比例。按实际接口和行为核对结果划分三档：

| 档位 | 内容 | 处理方式 |
| --- | --- | --- |
| A · 简单转调候选 | 文本、无作用域需求的行列布局、间距、尺寸 | 用中立参数分别调用框架 API，验证默认值与行为 |
| B · 需要专门接口 | `weight` / `align`、`LazyColumn`、`AnimatedVisibility`、输入框、按钮、Card、Scaffold | 核对作用域、列表 DSL、状态、事件与焦点；不预设只改 import |
| C · 按需求决定共享边界 | `stickyHeader`、`ModalBottomSheet`、`Dialog`、`Canvas`、复杂手势、`BoxWithConstraints` | 可封装业务组件，也可保留平台实现并用插槽注入；不预先判定无法中立化 |

官方标注为「完全支持」：状态管理、布局系统、列表与滚动、动画。标注为「大部分支持」：手势、Material3、常用 `Modifier`。文档未显式列出、状态未知的包括 `BoxScope.align`、`OutlinedTextField`、`AlertDialog`、`stickyHeader`，以及 `background` / `border` / `clip` / `shadow` 等 `Modifier`。

### 作用域能力

`ColumnScope.weight`、`BoxScope.align`、`matchParentSize` 涉及各框架的布局作用域。`expect` 函数可以声明带接收者的 `content` lambda，例如 `content: @Composable UiColumnScope.() -> Unit`；前提是公共层先定义自己的 `UiColumnScope`，并为两套框架适配其能力，不能直接把任一框架的作用域类型当作中立类型。

第一阶段暂不实现中立作用域，这是适配规模的选择，不是 Kotlin 机制不支持：

- `content` 参数统一为 `@Composable () -> Unit`
- 需要 `weight` 的场景优先验证具名布局组件；若确有必要，再设计最小中立作用域，不复制整个 `RowScope`
- 需要复杂作用域布局的页面区块，允许保留平台实现

## 9. 现有代码搬迁清单

`commonMain` 中不得出现任何 `com.tencent.kuikly.*` 导入。现有 7 个文件全部依赖 Kuikly，需移动到 `kuiklyMain`：

| 现路径（`shared/src/commonMain/kotlin/com/isogelun/sijinote/`） | 目标路径（`shared/src/kuiklyMain/kotlin/com/isogelun/sijinote/`） | 依赖 |
| --- | --- | --- |
| `RouterPage.kt` | `route/RouterPage.kt` | `@Page`、`setContent`、Kuikly Compose |
| `BasicWidget.kt` | `ui/BasicWidget.kt` | Kuikly Compose 组件与扩展 |
| `ImageAdapterBenchmarks.kt` | `route/ImageAdapterBenchmarks.kt` | `@Page` |
| `base/BasePager.kt` | `base/BasePager.kt` | `com.tencent.kuikly.core.pager.Pager` |
| `base/BridgeModule.kt` | `base/BridgeModule.kt` | `com.tencent.kuikly.core.module.Module` |
| `base/IPagerIdKtx.kt` | `base/IPagerIdKtx.kt` | Kuikly core |
| `base/Utils.kt` | `base/Utils.kt` | Kuikly core |

保留现有文件的包名，只移动目录，不因目录名变化修改引用。新增示例按自己的包名组织。预期各 Kuikly target 的 KSP 可以处理其 `dependsOn` 的 `kuiklyMain` 源码，但必须检查实际生成的页面注册代码，并运行现有 `router` 及新增 `index` 页面；不以 Kotlin 编译成功代替 KSP 注册验证。

`composeMain` 全新建，无搬迁内容。

搬迁完成后 `commonMain` 为空属正常状态，业务代码本就从零开始。

## 10. 鸿蒙双构建脚本的影响

本工程实际存在两套独立 Gradle 配置：

| 项 | `settings.gradle.kts` + `build.gradle.kts` | `settings.ohos.gradle.kts` + `build.ohos.gradle.kts` |
| --- | --- | --- |
| Kotlin | 2.1.21 | 2.0.21-KBA-010 |
| Kuikly | `2.7.0-2.1.21` | `2.7.0-2.0.21-ohos` |
| targets | android / js / ios×3 / **desktop(jvm)** | android / ios×3 / ohosArm64 |
| source sets | common / kuikly / desktop / android / js / ios | common / kuikly / android / ios / ohosArm64 |
| `commonMain` 的 Runtime 来源 | `compose.runtime` 1.7.3 | 保留原有 Kuikly ohos 依赖链路，独立核对 |

同一个 `commonMain` 目录会由两套 Kotlin/Compose 工具链分别编译。共享代码必须限制在两套构建均支持的语言与 Runtime API 范围；主构建通过不代表鸿蒙通过。鸿蒙 Runtime 的实际版本、元数据和新公共页面能否编译、运行均待验证，不能仅凭 API 基线相近判定兼容。

本轮保留原有鸿蒙工具链与渲染路线，仅调整源码集连接；两套构建的兼容性需要持续回归，不能因采用单模块而省略鸿蒙验证。

## 11. 未验证风险

以下各项均未在本工程实测，实施时必须逐项确认，失败后按给出的退路调整。

| 编号 | 风险或待验证项 | 验证与处理方式 |
| --- | --- | --- |
| R1 | Kuikly Gradle 插件遇到新增 JVM target 时的实际行为未知 | 先运行配置与编译任务，依据具体报错检查任务注册和 target 过滤；如确需拆模块，先记录阻碍再重新评估，不自动切换 WebView 路线 |
| R2 | `@Page` 搬入 `kuiklyMain` 后的 KSP 页面注册，以及处理器依赖隔离待验证 | 检查各 Kuikly target 的生成代码包含 `router`、`index`；桌面不运行 Kuikly KSP，处理器不进入其运行依赖 |
| R3 | `desktopApp` 消费 `shared` 的 desktop JVM 变体及 JVM 版本配置待验证 | 保留 `kotlin("jvm")`，编译宿主并检查候选变体、JVM 属性与字节码目标；遇到具体错误再调整，不预设需要 KMP 或 `withJava()` |
| R4 | 手工源码集层级是否完整待验证 | 显式关闭默认层级模板，核对 main/test 关系、iOS 叶子 target 及鸿蒙脚本连接；避免混用自动创建的源码集 |
| R5 | 当前主版本已确认声明 Runtime 1.7.3，但最终解析与运行兼容性未验收 | 对 Android、JS、桌面分别检查依赖报告和必要的 `dependencyInsight`，并验证公共 Composable 的渲染和状态更新 |
| R6 | 窗口 API 与 skiko 运行时分属共享模块和宿主后的运行、打包装配待验证 | 保持窗口在 `shared/composeMain`，先检查宿主运行依赖并启动、打包验证；只有具体阻碍无法解决时才重新评估窗口归属 |
| R7 | 输入、列表、动画等实际需要的 API 与行为可能存在差异 | 按第 8 节逐项核对，记录接口、运行差异与适配成本，不由官方对齐比例推算 |
| R8 | 剩余空间分配、作用域能力尚未验证 | 先验证具名布局组件；必要时设计最小中立作用域或保留局部平台布局 |
| R9 | 鸿蒙保留原依赖后，新公共代码与 Kuikly 源码集结构是否兼容待验证 | 用原鸿蒙工具链独立检查依赖、编译、KSP 注册和页面运行；不以主构建通过替代鸿蒙验收 |

其他已知但与本方案无关的问题：`shared/build.gradle.kts` 第 110 行 `rootProject.properties["mavenUr?"]` 拼写错误，发布仓库 URL 恒为空；`settings.gradle.kts` 中 `include("untitled")` 指向不存在的目录，`include("desktopApp")` 重复两次。

## 12. 实施阶段与验收标准

| 阶段 | 工作 | 通过条件 |
| --- | --- | --- |
| 1. 构建链路 | 新增 `kuiklyMain` / `composeMain`，迁移 7 个文件，`shared` 加 `jvm("compose")`，宿主依赖 `shared` | `:shared:compileKotlinCompose`、`:shared:compileDebugKotlinAndroid`、`:shared:compileKotlinJs` 和 `:desktopApp:compileKotlin` 全部通过；R1–R4 的配置与注册检查通过，R5 完成依赖解析核对 |
| 2. 最小共享 UI | `UiText`、`UiColumn`、`IndexScreen`，两端路由打通，再增加计数按钮 | 同一 `IndexScreen` 在 Android 与 Windows 渲染正确；默认参数、间距、宽度、嵌套内容和点击后的状态更新正常，补齐 R5 的首轮运行验证 |
| 3. 布局能力 | 文本、按钮、输入框、基础布局、页面插槽 | 中文输入、焦点、点击在两端正常 |
| 4. 剩余空间 | 标题固定 + 列表占满的兄弟节点布局 | 显式验证 `weight` 类需求的实现方式，R8 有结论 |
| 5. 业务闭环 | 笔记模型、加密保存、列表读取 | 两端使用同一 `commonMain` 业务逻辑完成闭环 |
| 6. 平台扩展与打包 | iOS、鸿蒙回归，桌面打包与其他桌面系统扩展 | R9 有独立结果，现有页面和新共享页正常；Windows 运行及打包验证 R6；macOS、Linux 分别记录实际结果，不由 Windows 结果推断 |

首轮额外检查：

- `commonMain` 仅使用中立 UI 类型、公共业务类型和 Runtime API，不导入 `com.tencent.kuikly.*` 或官方 Compose 的 UI、foundation、material 组件
- `kuiklyMain` 不导入官方 Compose UI 组件，`composeMain` 不导入 `com.tencent.kuikly.*`
- 主脚本的桌面依赖闭包不含 Kuikly Compose、渲染器或 Kuikly KSP 处理器；鸿蒙公共层保留原 Kuikly 依赖的例外按第 6.4 节单独检查
- 迁移后各 Kuikly target 的 KSP 生成映射包含预期页面，实际页面可打开
- Android、H5 现有 `router` 页面仍可正常打开
- 桌面端不依赖任何 `core-render-*` 构件
- `desktopApp` 的源码只有一个 `main` 函数，不含任何界面代码
- 桌面端收到未知 `pageName` 时显示兜底页而非崩溃
- `DesktopPageHost` 中的每个分支都指向 `commonMain` 的界面函数，不在 `composeMain` 编写业务界面

## 13. 参考资料

- [KuiklyUI 官方仓库](https://github.com/Tencent-TDS/KuiklyUI)
- [Kuikly Compose 能力说明（status.md）](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/Compose/status.md)
- [Maven Central：com.tencent.kuikly-open 构件索引](https://repo1.maven.org/maven2/com/tencent/kuikly-open/)
- [Kotlin：Expected and actual declarations](https://kotlinlang.org/docs/multiplatform/multiplatform-expect-actual.html)
- [Kotlin：Hierarchy template 与自定义 source set](https://kotlinlang.org/docs/multiplatform/multiplatform-hierarchy.html)
- [Compose 默认参数与 Composable 参数组合的历史 Issue](https://github.com/JetBrains/compose-multiplatform/issues/2806)

外部文档与仓库元数据会随版本变化。实施时应以本工程固定版本的依赖解析结果和实际编译输出为准，不以文档描述替代验证。
