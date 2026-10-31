# 共享界面后续开发指南

更新日期：2026-10-31（Asia/Shanghai）。

本文以当前工程目录为基础，说明 Android 和 desktop 共用界面时的开发方式。它是后续实施指南，不代表文中规划的组件、路由、存储功能已经实现。架构背景参见 [单模块共享 UI 方案](shared_ui_single_module_zh.md)。

## 1. 当前已经具备的结构

```text
shared/src/
├── commonMain/kotlin/com/isogelun/sijinote/
│   ├── app/IndexScreen.kt      目前显示三行文本
│   └── ui/UI.kt               UiText、UiColumn 的 expect 声明
├── kuiklyMain/kotlin/com/isogelun/sijinote/
│   ├── app/Kuikly.kt          @Page("index")，调用 IndexScreen
│   ├── ui/UI.kt               Kuikly actual 实现
│   └── base/                 Kuikly 容器与桥接基础代码
└── composeMain/kotlin/com/isogelun/sijinote/
    ├── app/Compose.kt         桌面窗口、Material3 主题，调用 IndexScreen
    └── ui/UI.kt               官方 Compose actual 实现

desktopApp/src/main/kotlin/Main.kt
    main() → runSiJiNoteDesktop()
```

Android 默认页标识为 `index`。Android 和 desktop 已接入同一个 `IndexScreen()`；Kuikly 入口也供其他 Kuikly 平台复用，但其他宿主是否默认打开 `index`，需要分别配置。

目前公共组件只有 `UiText` 和 `UiColumn`。按钮、输入框、列表、公共导航及数据保存均属于下一步工作。

## 2. 界面在哪里写

业务页面统一写在 `shared/src/commonMain/kotlin/com/isogelun/sijinote/app/`。随着页面增加，按功能拆目录：

```text
app/
├── IndexScreen.kt             根界面：组装应用状态、选择当前页面
├── navigation/
│   └── AppRoute.kt            页面标识及参数
├── note/
│   ├── NoteListScreen.kt      笔记列表页
│   ├── NoteEditorScreen.kt    笔记编辑页
│   └── components/            笔记功能内部复用的界面区块
└── settings/
    └── SettingsScreen.kt      设置页

ui/                           跨页面使用的中立基础组件接口
model/                        公共数据模型，按需新增
data/                         数据访问接口与实现，按需新增
```

例如列表页包名为 `com.isogelun.sijinote.app.note`。目录和包名保持对应，所有 Kotlin 源文件都放在对应源码集的 `kotlin/` 之下。

公共页面可以使用 `androidx.compose.runtime` 的状态与组合 API，以及项目的中立组件。不要直接导入 `androidx.compose.ui/foundation/material*` 或 `com.tencent.kuikly.*`；具体 UI 类型应留在两端适配层。

功能组件如果只是由 `UiText`、`UiColumn` 等中立组件组合而成，也直接写在公共层，无需增加 expect/actual。

## 3. IndexScreen 的职责

目前 `IndexScreen()` 只是一个最小页面。后续将它作为应用根界面：

- 持有或接收应用级界面状态。
- 根据当前路由显示列表、编辑、设置等页面。
- 把页面需要的数据传入，把页面事件连接到导航或业务操作。

具体页面内部的布局仍放在各自文件，避免根界面逐渐包含所有业务细节。后续如果名称造成歧义，可以再统一改为 `SiJiNoteApp`；当前无需为命名先做重构。

```text
Android 宿主 → Kuikly.kt ─┐
                         ├→ IndexScreen → 列表 / 编辑 / 设置
desktopApp → Compose.kt ──┘
```

两端入口继续调用根界面。新增普通业务页面时，不需要为每个页面增加一个 Kuikly `@Page` 或一个桌面 `Window`。需要独立原生页面、独立窗口或外部直接打开页面时，再增加相应宿主适配。

## 4. 多页面如何显示与切换

先采用公共层状态驱动页面切换。第一阶段可用枚举；打开已有笔记时，路由需要携带笔记 ID，因此可以直接使用以下最小路由模型。

```kotlin
package com.isogelun.sijinote.app.navigation

sealed interface AppRoute {
    data object Notes : AppRoute
    data class Editor(val noteId: String?) : AppRoute
    data object Settings : AppRoute
}
```

`Editor(null)` 表示新建，`Editor(id)` 表示编辑已有笔记。根界面示意如下。此示例需要先实现对应页面及回调，并补齐 import，不能直接替换当前文件后就编译。

```kotlin
@Composable
fun IndexScreen() {
    var route by remember { mutableStateOf<AppRoute>(AppRoute.Notes) }

    when (val current = route) {
        AppRoute.Notes -> NoteListScreen(
            onCreateNote = { route = AppRoute.Editor(null) },
            onOpenNote = { id -> route = AppRoute.Editor(id) },
            onOpenSettings = { route = AppRoute.Settings }
        )
        is AppRoute.Editor -> NoteEditorScreen(
            noteId = current.noteId,
            onBack = { route = AppRoute.Notes }
        )
        AppRoute.Settings -> SettingsScreen(
            onBack = { route = AppRoute.Notes }
        )
    }
}
```

具体页面通过 `onOpenNote`、`onBack` 等回调表达事件，不直接操作 Android Activity、Kuikly RouterModule 或桌面窗口。

这套示例的返回行为固定回到列表，不是完整的返回栈。需要多层连续返回时，再引入公共返回栈。Android 系统返回键必须通过宿主或 Kuikly 容器能力接入公共返回逻辑，不能只实现页面上的返回按钮。

`remember` 只保留当前组合生命周期中的状态，不保证进程重启或 Activity 重建后的恢复。编辑草稿需要放在页面切换后仍存在的状态持有者中，后续再接持久化，避免切走页面丢失输入。

## 5. 新增基础组件的流程

当公共页面需要当前没有的底层能力时，按以下顺序开发：

1. 在 `commonMain/.../ui` 定义最小中立接口。
2. 在 `kuiklyMain/.../ui` 提供 Kuikly actual。
3. 在 `composeMain/.../ui` 提供官方 Compose actual。
4. 在一个公共页面中调用，分别验证两端行为。

例如下一步需要按钮，可以设计以下接口；它目前尚未实现：

```kotlin
@Composable
expect fun UiButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
)
```

默认参数只放在 expect 声明中。两份 actual 都应落实 `enabled` 和点击行为。输入框应由调用方传入 `value` 和 `onValueChange`，让公共状态成为唯一数据来源。

建议按实际页面需求补充：

| 需求 | 下一步组件 | 核对重点 |
| --- | --- | --- |
| 新建、返回、保存 | UiButton | 点击一次触发一次，禁用状态有效 |
| 标题和正文编辑 | UiTextField | 中文输入、焦点、多行、值同步 |
| 横向工具栏 | UiRow | 间距、对齐、窗口变窄后的行为 |
| 长笔记列表 | 中立列表组件 | 滚动、稳定 key、数据变化 |
| 标题和说明层级 | 扩展 UiText | 字号、颜色、主题语义 |

公共 API 不暴露任一框架的 Modifier、Color、Dp、RowScope 等类型。先使用中立参数；需要作用域布局、输入或列表 DSL 时，再设计专门的接口，不直接复制整套 Compose API。

组件变多后，可以将各端的 `UI.kt` 分为 `Text.kt`、`Layout.kt`、`Button.kt` 等文件，三边按相同分类组织。无需为了每个功能页面重复适配组件。

## 6. 状态与数据如何分工

- 页面：根据传入的状态绘制界面，通过回调发出用户事件。
- 公共状态持有者：管理当前笔记、输入内容、加载与错误状态，以及保存动作。
- 数据层：定义读取、保存、删除笔记的接口。
- 平台实现：在需要文件系统等平台能力时提供具体实现。

第一阶段可以只使用内存数据打通交互，但必须清楚标注重启后会丢失。不要在平台组件 actual 中保存笔记业务数据，也不要将磁盘读写直接写进 Composable 函数体。

列表中的每条笔记应有稳定 ID。编辑现有笔记使用 ID 读取数据，避免用列表位置代表笔记身份。正式保存、加密和同步另做实现，不以演示交互代替数据安全能力。

## 7. 推荐实施顺序

| 阶段 | 工作 | 验收结果 |
| --- | --- | --- |
| 1. 页面骨架 | 新增列表、编辑、设置三个公共页面，根界面接公共路由 | 两端都能通过相同操作切换页面 |
| 2. 基础交互 | 补按钮、输入框，接新建、打开、返回 | 中文输入、焦点、禁用与点击行为正常 |
| 3. 内存闭环 | 使用稳定 ID，实现内存中的增删改查 | 新建后能打开、修改、返回并看到更新 |
| 4. 草稿与返回 | 明确取消、保存、未保存返回的行为 | 切页不会意外丢输入，系统返回行为明确 |
| 5. 本地保存 | 增加存储接口及平台实现 | 重启后笔记仍存在，错误可反馈 |
| 6. 布局完善 | 滚动、主题、状态栏避让、桌面尺寸适配 | 手机内容不被状态栏和键盘遮挡，桌面缩放可用 |
| 7. 扩展平台 | iOS、鸿蒙及其他宿主独立验证 | 记录各平台实际编译与运行结果 |

第一步先完成“列表 → 新建编辑 → 返回列表”，再加入设置页。多页面不等于必须同时显示多个页面；桌面以后需要左右分栏时，可让公共根界面根据中立的窗口尺寸信息选择布局。

## 8. 开发与验证方式

启动方式保持不变：

```powershell
# 桌面
.\gradlew.bat :desktopApp:run
```

Android 在 IDE 中运行 `androidApp`，默认进入 `index`。

需要验证编译时，由开发者按改动范围选择任务：

```powershell
.\gradlew.bat :shared:compileKotlinCompose :desktopApp:compileKotlin :androidApp:compileDebugKotlin :shared:compileKotlinJs --console=plain
```

编译通过只能说明源码及依赖链路通过检查。共享界面的字体、间距、按钮响应、中文输入、滚动、键盘遮挡和返回行为仍需分别运行 Android 和 desktop 验证。

Windows 上的主构建不能替代 iOS 或鸿蒙验收。鸿蒙继续使用独立构建脚本与工具链；当前环境此前未能离线解析其 Kotlin 插件，不能据此宣称已经验证通过。

按照当前协作约定，自动修改代码后不默认发起编译，由开发者自行运行，除非另行要求。

## 9. 每次新增页面的操作清单

1. 在 `commonMain/app/功能目录` 新建页面。
2. 优先复用公共组件和公共功能区块。
3. 缺少底层组件能力时，先补中立声明和两端实现。
4. 为页面定义必要的状态参数和事件回调。
5. 在公共路由和根界面中接入页面。
6. 处理离开页面时的草稿、保存及返回行为。
7. 在两端运行确认，再继续增加功能。

日常界面开发主要发生在 `commonMain`。两端 `app` 容器通常只在主题、生命周期、窗口或宿主交互发生变化时调整。
