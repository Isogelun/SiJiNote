# SiJiNote 跨框架共享 UI 方案

> 历史设计文档：本文的 shared-core / shared-ui 双模块方案未采用。当前工程使用 shared 单模块的 commonMain、kuiklyMain、composeMain 分层。请先阅读 [当前实现快照](shared_ui_single_module_zh.md) 和 [后续开发指南](shared_ui_development_guide_zh.md)；本文保留为设计讨论参考，不作为当前目录及实施任务依据。

更新日期：2026-10-31（Asia/Shanghai）。

状态：设计基线，尚未完成工程实现及跨端验证。本文记录已讨论的方向，不代表两套框架的依赖、编译和运行兼容性已经验收。

## 1. 目标与技术路线

手机和平板使用 Kuikly Compose，Windows、macOS、Linux 桌面使用官方 Compose Multiplatform。通过中立组件接口尽量共享同一套页面源码，在编译时选择对应组件实现。

桌面不采用 H5、Electron 或 WebView。官方 Compose 桌面使用 JVM 和 Skia 绘制界面，并非操作系统自带控件。

继续使用现有工程，不因加入桌面端而重新创建整个项目。现有 Kuikly 工程先保留，新增最小验证链路后再逐步整理。

### 复用范围

| 层次 | 内容 | 维护方式 |
| --- | --- | --- |
| 业务层 | 笔记模型、API、加密、同步、冲突与恢复状态 | 各端共享 |
| 页面层 | 页面结构、业务状态展示、事件连接 | 尽量共享 |
| 组件适配层 | 文本、按钮、布局、列表、输入框等 | Kuikly、官方 Compose 分别实现 |
| 平台层 | 启动入口、窗口、文件访问、安全存储等 | 按平台实现 |

不预设界面复用比例，不强求所有页面布局和交互完全一致。

## 2. 核心机制：声明、分别实现、统一调用

`expect/actual` 的作用是：公共源码声明一个接口，各端分别编写实现，共享页面统一调用公共名称。编译器为当前 target 匹配实现。

```text
共享业务状态
    ↓
共享页面
    ↓
中立 UI 接口
    ├── 手机／平板构建 → Kuikly 实现
    └── 桌面构建      → 官方 Compose 实现
```

它不会自动转换组件、补齐功能或处理两套框架的参数差异。基础组件适配需要分别编写，使用这些接口的页面尽量只写一份。

本方案不使用运行时组件注册表，也不采用构建时替换包名、生成第二份页面源码的方式。

## 3. 模块与源码集结构

建议结构如下，新增目录和模块需要在实施阶段创建：

```text
SiJiNote/
├── shared-core/                   独立 KMP 业务模块
├── shared-ui/                     中立 UI 和实现所在的 KMP 模块
│   └── src/
│       ├── commonMain/            公共接口、中立类型、共享页面
│       ├── kuiklyMain/            Kuikly actual 和移动端专属组件
│       ├── androidMain/           Android UI 适配
│       ├── iosMain/               iOS UI 适配
│       └── desktopMain/           官方 Compose actual 和桌面专属组件
├── shared/                        现有 Kuikly 页面入口、路由、宿主桥接
├── desktopApp/                    桌面窗口、菜单、启动入口
├── androidApp/                    Android 宿主
├── iosApp/                        iOS 宿主
└── docs/
```

现有其他模板模块不因本文而自动删除、禁用或纳入新 UI 的支持范围。

### 源码集关系

```text
shared-ui/commonMain
    ├── kuiklyMain
    │   ├── androidMain
    │   └── iosMain → 各 iOS target
    └── desktopMain → 桌面 JVM target
```

- `kuiklyMain` 是自定义中间源码集，不是新平台；需要配置各源码集的 `dependsOn` 关系。
- `desktopMain` 对应命名为 desktop 的 JVM target，供三种桌面系统使用。
- 公共 `expect` 与其 `actual` 放在同一个 KMP 模块的对应源码集中。不能把 `expect` 放在 `shared-ui`，再依靠普通模块依赖，让 `shared`、`desktopApp` 中的 `actual` 补齐它。
- `shared` 和 `desktopApp` 调用 `shared-ui`，不负责补该模块的 `actual`。
- 共享页面可以依赖 `shared-core`；业务模块不依赖 UI 或宿主。

### 依赖隔离

- `commonMain` 只使用中立接口、中立类型、业务模型，以及经核对的 Compose Runtime 等公共依赖。
- Kuikly UI 依赖放在 `kuiklyMain` 等对应源码集。
- 官方 Compose UI 依赖放在 `desktopMain`。
- 避免把任一端 UI 类型暴露到公共组件签名中。

首轮必须验证 Kuikly 插件、元数据解析和源码集组合是否允许上述结构正常构建。若框架约束阻碍该结构，先记录实际错误再调整，不能假定增加目录就已完成配置。

## 4. 三类组件名称与使用规则

| 类型 | 示例 | 使用位置 |
| --- | --- | --- |
| 公共接口 | `UiText`、`UiColumn`、`NoteCard` | 共享页面 |
| Kuikly 专属组件 | `SpecialCard_K` | 移动端实现、移动端入口或专属页面 |
| Compose 专属组件 | `SpecialCard_C` | 桌面实现、桌面入口或专属页面 |

`_K`、`_C` 只是命名约定，没有自动选择能力。真正的选择由源码集和 `expect/actual` 完成。

共享页面使用公共名称，不直接引用 `_K`、`_C` 专属实现。平台专属代码可以直接使用对应框架。

简单组件可直接在 `actual` 中调用框架组件，不要求为每个接口额外创建一个 `_K` 或 `_C` 函数。

## 5. expect/actual 示例

以下为机制示例，并非已在当前项目验证通过的实现。

公共声明，位于 `shared-ui/src/commonMain/kotlin/`：

```kotlin
package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable

@Composable
expect fun UiText(text: String)
```

移动端实现，位于 `shared-ui/src/kuiklyMain/kotlin/`：

```kotlin
package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable

@Composable
actual fun UiText(text: String) {
    com.tencent.kuikly.compose.material3.Text(text = text)
}
```

桌面实现，位于 `shared-ui/src/desktopMain/kotlin/`：

```kotlin
package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable

@Composable
actual fun UiText(text: String) {
    androidx.compose.material3.Text(text = text)
}
```

页面统一调用 `UiText("我的笔记")`。声明与实现需要使用相同包名、兼容的签名，并满足 Kotlin 和 Compose 编译器规则。

## 6. 中立参数与组件 API

公共接口不直接使用两套框架各自的 `Modifier`、`Color`、`Dp`、`TextStyle`、`Painter`、`ColumnScope` 或 `RowScope`。

按实际需求在 `commonMain` 中定义中立类型，例如：

```kotlin
enum class UiTextRole { Title, Body, Caption }
enum class UiColorRole { Primary, Text, Surface }
```

共享页面表达设计意图，各端映射到对应的字体、颜色和组件配置。公共类型名称应与框架类型明确区分。

第一阶段优先采用具名参数表达需求，例如 `paddingDp`、`spacingDp`、宽高、填满宽度和 `onClick`。数值参数必须明确单位、默认行为和适用范围。

不设计覆盖整个框架的 `UiModifier` 链。若组件参数不断增加平台专有选项，应考虑把差异移到更高层的业务组件中。

## 7. 布局与作用域能力

共享的是页面排列结构；真正执行布局的组件仍由各端实现。

第一阶段先支持文本、行列布局、固定尺寸、内边距、间距和基础宽度填充。公共 `content` 可先采用 `@Composable () -> Unit`。

`weight`、作用域内 `align`、`matchParentSize` 涉及框架各自的布局作用域，不能直接把两套类型当作通用接口，也不能假定普通 content lambda 自动拥有这些能力。

实施顺序：

1. 跑通无作用域依赖的基础页面。
2. 显式验证“标题固定、列表或编辑区占满剩余空间”的兄弟节点布局。
3. 根据实际结果设计最小中立作用域或更高层布局组件；必要时该处保留平台布局。

伸缩布局是笔记产品的必要能力，应在早期验证，但不在第一步复制整套 Compose 布局作用域。

## 8. 特殊 Card 与页面插槽

### 两端都有，外观或交互不同

共享页面调用 `NoteCard(note, onClick)`；移动端 actual 使用 Kuikly，桌面 actual 使用官方 Compose。可在内部进一步调用 `NoteCard_K`、`NoteCard_C`。

公共接口传递数据和事件，不传某一框架的 UI 类型。

### 只在手机端出现

手机专属组件 `SpecialCard_K` 放在移动端源码中，由手机入口插入共享页面的具名扩展位置。桌面入口传空内容。

```kotlin
// 结构示意：UiColumn、NoteList 等需先实现
@Composable
fun NoteScreen(extraContent: @Composable () -> Unit) {
    UiColumn {
        UiText("我的笔记")
        extraContent()
        NoteList()
    }
}

// 手机入口
// NoteScreen(extraContent = { SpecialCard_K() })

// 桌面入口
// NoteScreen(extraContent = {})
```

不需要字符串名称注册插槽。内容必须与当前 target 的组件实现匹配，不能将 Kuikly UI 内容插入官方 Compose 桌面组件树。

如果整页结构差异很大，允许分别维护平台页面，继续共享业务状态和适合共用的组件。

## 9. 默认参数与 Composable lambda

历史上存在 `expect/actual`、默认参数和 Composable lambda 组合导致桌面运行或编译错误的报告，不能据此认定当前版本仍然失败，也不能未经验证便忽略。

约束如下：

- `actual` 不重复声明默认参数。
- 中立默认值定义在公共层。
- 复杂组件可由公共普通 Composable 提供默认参数，再调用参数完整的内部 expect 函数。
- 验证显式传参、省略默认参数、嵌套 Composable 内容和状态更新。

接口简化不能替代实际编译和运行检查。

## 10. 状态、业务与平台能力

共享页面接收状态和事件回调。加密、上传、同步重试、冲突处理等放在 `shared-core`，不放在基础 UI 组件中。

文件选择、文件读写、密钥安全存储、剪贴板、拖放、窗口、快捷键、移动生命周期等通过独立平台接口或宿主代码实现。

产品约束继续沿用：默认端到端加密，客户端持有解密能力；必要的加密草稿、待发送操作和同步状态需要持久化。此 UI 方案不改变服务端 Java、PostgreSQL 和内容存储路线。

## 11. 版本策略

以当前实际工程配置为起点，统一核对 Kotlin、Compose 编译器、Compose Runtime、Kuikly 和桌面 Compose 组合，不逐项升级到各自最新版本。

Kuikly 文档中的 API 对齐说明不等于本方案已获得官方互通保证，也不能作为本项目页面复用率的结论。

首次仅验证 Android 和 Windows。iOS、macOS、Linux 的构建、运行和安装包需要分别验证；Windows 验证通过不代表其他平台已经验收。

## 12. 实施阶段与验收条件

| 阶段 | 工作 | 通过条件 |
| --- | --- | --- |
| 1. 构建链路 | 添加最小 shared-ui、桌面入口，接入现有移动宿主 | 同一公共组件在 Android、桌面分别编译和调用 |
| 2. 基础界面 | 文本、按钮、基础布局、输入框、插槽 | 同一页面两端运行，状态和事件正确 |
| 3. 布局与交互 | 剩余空间分配、滚动、中文输入、焦点、窗口缩放 | 没有阻碍笔记操作的问题 |
| 4. 产品组件 | 列表、Card、对话框、编辑器 | 公共接口覆盖实际需求，特殊实现归属清楚 |
| 5. 业务闭环 | 后端连接、初始化或配对、解锁、新建、加密保存、列表读取 | 两端使用同一业务逻辑完成闭环 |
| 6. 平台扩展 | iOS、macOS、Linux 实机运行和打包 | 分平台记录结果及限制 |

首轮额外检查：

- 公共层未泄漏框架 UI 类型。
- 移动端和桌面 UI 依赖按源码集隔离。
- 同一公共 Composable 能通过两端编译器并在各自组件树中运行。
- 默认参数和 Composable lambda 组合在桌面运行正常。
- 手机特殊 Card 可以插入，桌面可以省略。
- 占满剩余空间的布局有显式验证结果。

仅编译成功不代表中文输入、焦点、滚动或编辑体验通过。编写验证报告时区分本轮结果、历史结果和未验证项目。

## 13. 适配层规模控制

只封装产品实际需要的组件和行为，不重做整个 Compose 框架。

出现大量平台判断、不断膨胀的参数、为单个控件封装大量底层类型，或难以稳定处理输入与布局时，应重新评估局部设计。

优先把差异集中到 `NoteEditor`、`NoteCard` 等业务组件，或局部平台布局。若适配成本过高，应记录可运行验证及具体原因，再决定调整共享边界；不通过扩大抽象掩盖兼容问题。

## 14. 当前状态与下一步

本文仅形成设计基线，不执行模块创建、现有代码迁移或框架升级。

下一项工程工作是制作最小 Android／Windows 验证：同一公共页面使用中立接口，两端分别连接 Kuikly 和官方 Compose，完成基础组件、输入、插槽和伸缩布局验证后，再扩大共享范围。

## 15. 参考资料

- [Kotlin：Expected and actual declarations](https://kotlinlang.org/docs/multiplatform/multiplatform-expect-actual.html)
- [Kotlin：源码集关系与项目依赖](https://kotlinlang.org/docs/multiplatform/multiplatform-advanced-project-structure.html)
- [Kuikly 官方源码与 Compose 改造说明](https://github.com/Tencent-TDS/KuiklyUI/blob/main/README-zh_CN.md)
- [Kuikly Compose 能力说明](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/Compose/status.md)
- [Compose 默认参数与 Composable 参数组合的历史 Issue](https://github.com/JetBrains/compose-multiplatform/issues/2806)
- [官方 Compose 桌面打包说明](https://kotlinlang.org/docs/multiplatform/compose-native-distribution.html)

外部文档会随版本变化；实施时应以项目固定版本的源码、依赖及实际测试为准。
