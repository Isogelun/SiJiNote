# 共享界面后续开发指南

更新日期：2026-11-01（Asia/Shanghai）。

本文以当前工程目录为基础，说明 Android 和 desktop 共用界面时的开发方式。它是后续实施指南，不代表文中规划的组件、路由、存储功能已经实现。架构背景参见 [单模块共享 UI 方案](shared_ui_single_module_zh.md)。

## 1. 当前已经具备的结构

```text
shared/src/
├── commonMain/kotlin/com/isogelun/sijinote/
│   ├── app/Index.kt            定义 IndexScreen()，目前显示三行文本
│   └── ui/UI.kt               UiText、UiColumn 的 expect 声明
├── kuiklyMain/kotlin/com/isogelun/sijinote/
│   ├── app/Kuikly.kt          直接继承 ComposeContainer，注册 index
│   └── ui/UI.kt               Kuikly actual 实现
└── composeMain/kotlin/com/isogelun/sijinote/
    ├── app/Compose.kt         桌面窗口、Material3 主题，调用 IndexScreen
    └── ui/UI.kt               官方 Compose actual 实现

desktopApp/src/main/kotlin/Main.kt
    main() → runSiJiNoteDesktop()
```

Android、H5、iOS、鸿蒙的默认页标识均已配置为 `index`。desktop 直接通过窗口调用同一个 `IndexScreen()`。H5 的 `page_name` 参数和 Android、鸿蒙传入的页面参数仍可指定其他页面，但显式指定的页面必须实际注册；旧 `router` 已移除。小程序宿主未在本轮完成启动与运行验证。

共享层的模板 `RouterPage.kt`、`ImageAdapterBenchmarks.kt`、`BasicWidget.kt`、`Utils.kt`、`IPagerIdKtx.kt`、`BridgeModule.kt`、`BasePager.kt` 及测试图片已清理。`kuiklyMain` 只保留应用入口和 UI 适配；各宿主自身的桥接实现仍保留。Kuikly 入口直接使用框架默认容器行为，不再包含原 BasePager 的主题参数与调试行为覆盖。

验证记录：清理前曾通过桌面、Android、JS 编译及 index 页面注册检查；用户已确认 desktop、H5 显示三行共享文本，内置浏览器也观察到 H5 内容。最近删除模板及 BasePager 后未重新编译或启动验证，不能把先前结果当作清理后版本的完整验收。iOS、鸿蒙没有完成实际运行验证。

目前公共组件只有 `UiText` 和 `UiColumn`。按钮、输入框、列表、公共导航及数据保存均属于下一步工作。

## 2. 界面在哪里写

业务页面统一写在 `shared/src/commonMain/kotlin/com/isogelun/sijinote/app/`。随着页面增加，按功能拆目录：

```text
app/
├── Index.kt                   定义 IndexScreen()，后续扩展为根界面
├── route/
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
package com.isogelun.sijinote.app.route

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

## 7. 桌面、手机及 H5 的差异如何处理

### 7.1 先区分空间差异和平台能力差异

共享的是业务状态、页面区块和操作含义，界面允许根据可用空间和交互能力变化。不要为适配桌面另复制一整套业务页面。

| 差异 | 放在哪里处理 | 例子 |
| --- | --- | --- |
| 可用空间 | commonMain 的自适应布局 | 窄窗口单栏，宽窗口左右分栏 |
| 框架组件 API | 两端 ui actual | Kuikly Column 与官方 Compose Column |
| 平台专属操作 | 公共能力接口 + 平台实现 | 文件选择、快捷键、系统返回、剪贴板 |
| 平台专属界面区块 | 公共插槽或小型 expect/actual 组件 | 桌面工具栏、触屏操作栏 |
| 系统环境 | 平台入口或中立环境接口 | 安全区、键盘遮挡、生命周期 |

`kuiklyMain` 是渲染框架分组，不等于手机端：它同时服务 Android、iOS、H5 和鸿蒙。桌面浏览器里的 H5 也可能需要宽屏布局；触屏、鼠标和快捷键能力也不能仅根据源码集名称判断。

### 7.2 在公共层根据实际可用宽度选择布局

建议后续增加中立的布局类型和观测接口。以下是待实现的接口设计，不是当前已有 API：

```kotlin
enum class AppLayout { Compact, Expanded }

@Composable
expect fun rememberAppLayout(): AppLayout
```

两端实现读取当前内容容器的可用宽度，将其转换为中立类型。优先测量容器约束；受框架 API 限制时再由宿主传入并持续更新。不要只在启动时读取一次屏幕分辨率，也不要把物理像素直接当作 dp。

宽度阈值根据列表和编辑器的最小可用宽度选择，先在项目中集中定义一个待验证值，再用实际内容调整。桌面窗口缩放、手机旋转、浏览器尺寸变化及分屏都应触发布局更新。第一轮只需要 Compact/Expanded，无需提前设计所有设备分类。

```text
Compact                         Expanded
┌─────────────────┐             ┌───────────┬────────────────┐
│ 笔记列表 或 编辑器 │             │ 笔记列表   │ 编辑器或未选提示 │
└─────────────────┘             └───────────┴────────────────┘
```

列表和编辑器是公共区块；窄屏按当前路由显示一个，宽屏同时显示两个。实现双栏还需要中立的横向布局和剩余空间分配能力，当前 UiColumn/UiText 不足以完成它。

### 7.3 业务状态放在自适应分支之外

以下仅示意数据流，NotesState、rememberNotesState 和页面函数均需后续实现：

```kotlin
@Composable
fun NotesScreen() {
    val notesState = rememberNotesState()
    val layout = rememberAppLayout()

    when (layout) {
        AppLayout.Compact -> CompactNotesScreen(notesState)
        AppLayout.Expanded -> ExpandedNotesScreen(notesState)
    }
}
```

两种布局操作同一个状态持有者。选中笔记 ID、编辑草稿、保存状态不能分别 remember 在两个布局分支里，否则跨阈值重组时可能重置。需要保留的列表滚动位置也应明确存放位置。

提前规定宽窄布局转换规则：从宽屏变窄时，已打开笔记则显示编辑器，未选中笔记则显示列表；恢复宽屏时保留选中笔记和草稿。路由切换与宽度变化都不能隐式触发保存或丢弃。

### 7.4 平台专属能力用接口，专属 UI 用插槽

同一个“保存”动作可以由触屏按钮、桌面菜单或快捷键触发，最终都调用公共业务层的同一个保存函数。文件选择的具体 API 留在平台侧，公共层接收中立的结果或错误。

某一小块界面需要不同实现时，可以定义语义明确的 `expect/actual` 组件；如果只是容器需要注入操作区，优先用公共 `@Composable () -> Unit` 插槽。不要为了每个页面增加一大组 `isDesktop` 分支，也不要把平台 Modifier 放进公共插槽参数。

Kuikly actual 内还可能需要按宿主能力区分 Android、iOS 和 H5；确有需要时再向叶子源码集下沉实现，不假设这些平台共享所有系统 API。

### 7.5 自适应验收

- 用窄、宽两种桌面窗口和 H5 视口检查布局，手机横竖屏检查内容是否可用。
- 拖动窗口反复跨过阈值，选中笔记、输入草稿和保存状态保持一致。
- 小窗口可滚动，没有被固定宽度挤出屏幕的主要操作。
- 手机系统返回、页面返回按钮和桌面导航操作落到同一套公共返回逻辑。
- 触屏用户无需依赖悬停或右键才能完成必要操作，键盘用户能访问主要操作。
- 手机状态栏、安全区和输入法出现后，编辑内容及操作按钮仍可见。

当前尚未实现上述布局接口、分栏、专属能力和返回适配。先完成共享列表和编辑交互，再增加自适应组合；状态归属在第一轮就按上述规则安排。

## 8. 推荐实施顺序

| 阶段 | 工作 | 验收结果 |
| --- | --- | --- |
| 1. 页面骨架 | 新增列表、编辑、设置三个公共页面，根界面接公共路由 | 两端都能通过相同操作切换页面 |
| 2. 基础交互 | 补按钮、输入框，接新建、打开、返回 | 中文输入、焦点、禁用与点击行为正常 |
| 3. 内存闭环 | 使用稳定 ID，实现内存中的增删改查 | 新建后能打开、修改、返回并看到更新 |
| 4. 草稿与返回 | 明确取消、保存、未保存返回的行为 | 切页不会意外丢输入，系统返回行为明确 |
| 5. 本地保存 | 增加存储接口及平台实现 | 重启后笔记仍存在，错误可反馈 |
| 6. 自适应与平台交互 | 中立宽度观测、单栏/双栏、滚动、主题、安全区和必要的专属操作 | 跨布局不丢草稿，手机与桌面交互均可用，H5 宽窄视口正常 |
| 7. 扩展平台 | iOS、鸿蒙及其他宿主独立验证 | 记录各平台实际编译与运行结果 |

第一步先完成“列表 → 新建编辑 → 返回列表”，再加入设置页。多页面不等于必须同时显示多个页面；桌面以后需要左右分栏时，可让公共根界面根据中立的窗口尺寸信息选择布局。

## 9. 开发与验证方式

启动方式保持不变：

```powershell
# 桌面
.\gradlew.bat :desktopApp:run
```

Android 在 IDE 中运行 `androidApp`，默认进入 `index`。

H5 本次运行验证地址为 `http://localhost:8080/`。当前 HTML 还会单独从 `http://127.0.0.1:8083/nativevue2.js` 加载共享业务脚本；宿主编译通过不等于业务 bundle 已更新。修改公共页面后，需要确保共享业务 JS 已重新生成、业务脚本服务可访问，再刷新页面。这里的端口是当前开发配置，不是所有环境的固定要求。

此前 H5 控制台观察到外部 libpag 脚本的 `import.meta` 错误；三行文本仍正常显示，该问题未在本轮修复，后续使用相关动画能力前需独立处理。

需要验证编译时，由开发者按改动范围选择任务：

```powershell
.\gradlew.bat :shared:compileKotlinCompose :desktopApp:compileKotlin :androidApp:compileDebugKotlin :shared:compileKotlinJs --console=plain
# H5 宿主源码编译检查；不等同于浏览器分发产物的完整构建
.\gradlew.bat :h5App:compileKotlinJs --console=plain
```

编译通过只能说明源码及依赖链路通过检查。共享界面的字体、间距、按钮响应、中文输入、滚动、键盘遮挡和返回行为仍需分别运行 Android 和 desktop 验证。

Windows 上的主构建不能替代 iOS 或鸿蒙验收。鸿蒙继续使用独立构建脚本与工具链；当前环境此前未能离线解析其 Kotlin 插件，不能据此宣称已经验证通过。

按照当前协作约定，自动修改代码后不默认发起编译，由开发者自行运行，除非另行要求。

## 10. 每次新增页面的操作清单

1. 在 `commonMain/app/功能目录` 新建页面。
2. 优先复用公共组件和公共功能区块。
3. 缺少底层组件能力时，先补中立声明和两端实现。
4. 为页面定义必要的状态参数和事件回调。
5. 在公共路由和根界面中接入页面。
6. 处理离开页面时的草稿、保存及返回行为。
7. 确定窄、宽布局的组合方式，共享选中项和草稿状态。
8. 在 Android、desktop 和 H5 运行确认，再继续增加功能；iOS、鸿蒙按各自环境独立验收。

日常界面开发主要发生在 `commonMain`。两端 `app` 容器通常只在主题、生命周期、窗口或宿主交互发生变化时调整。
