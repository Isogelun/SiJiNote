package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable

/**
 * 跨框架中立组件接口。
 *
 * 公共页面只调用这里的名称，具体实现位于：
 * - `shared/src/kuiklyMain/.../ui/Ui.kt`（Kuikly：Android / iOS / H5 / 鸿蒙）
 * - `shared/src/composeMain/.../ui/Ui.kt`（官方 Compose：桌面）
 *
 * 约定：
 * - 尺寸一律使用 Int dp，颜色使用 [UiColorRole]，不暴露任一框架的 Modifier / Color / Dp。
 * - 默认参数只写在 expect 声明中，actual 不重复声明。
 */

enum class UiAlign { Start, Center, End }

enum class UiJustify { Start, Center, End, SpaceBetween }

/** 列作用域：为子项分配剩余高度。 */
interface UiColumnScope {
    @Composable
    fun Flex(weight: Float, content: @Composable () -> Unit)
}

/** 行作用域：为子项分配剩余宽度。 */
interface UiRowScope {
    @Composable
    fun Flex(weight: Float, content: @Composable () -> Unit)
}

@Composable
expect fun UiText(
    text: String,
    role: UiTextRole = UiTextRole.Body,
    color: UiColorRole? = null,
    weight: UiFontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    fillMaxWidth: Boolean = false
)

@Composable
expect fun UiColumn(
    paddingDp: Int = 0,
    paddingHorizontalDp: Int = -1,
    paddingVerticalDp: Int = -1,
    spacingDp: Int = 0,
    widthDp: Int = -1,
    maxWidthDp: Int = -1,
    heightDp: Int = -1,
    fillMaxWidth: Boolean = false,
    fillMaxHeight: Boolean = false,
    background: UiColorRole? = null,
    cornerRadiusDp: Int = 0,
    borderColor: UiColorRole? = null,
    align: UiAlign = UiAlign.Start,
    justify: UiJustify = UiJustify.Start,
    scrollable: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable UiColumnScope.() -> Unit
)

@Composable
expect fun UiRow(
    paddingDp: Int = 0,
    paddingHorizontalDp: Int = -1,
    paddingVerticalDp: Int = -1,
    spacingDp: Int = 0,
    heightDp: Int = -1,
    fillMaxWidth: Boolean = false,
    fillMaxHeight: Boolean = false,
    background: UiColorRole? = null,
    cornerRadiusDp: Int = 0,
    borderColor: UiColorRole? = null,
    align: UiAlign = UiAlign.Center,
    justify: UiJustify = UiJustify.Start,
    onClick: (() -> Unit)? = null,
    content: @Composable UiRowScope.() -> Unit
)

@Composable
expect fun UiBox(
    widthDp: Int = -1,
    heightDp: Int = -1,
    paddingDp: Int = 0,
    paddingHorizontalDp: Int = -1,
    paddingVerticalDp: Int = -1,
    fillMaxWidth: Boolean = false,
    fillMaxHeight: Boolean = false,
    background: UiColorRole? = null,
    cornerRadiusDp: Int = 0,
    borderColor: UiColorRole? = null,
    align: UiAlign = UiAlign.Start,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {}
)

@Composable
expect fun UiSpacer(widthDp: Int = 0, heightDp: Int = 0)

@Composable
expect fun UiDivider(
    color: UiColorRole = UiColorRole.Divider,
    thicknessDp: Int = 1
)

@Composable
expect fun UiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    role: UiTextRole = UiTextRole.Body,
    color: UiColorRole = UiColorRole.TextPrimary,
    singleLine: Boolean = true,
    fillMaxWidth: Boolean = true
)

@Composable
expect fun UiIcon(
    name: UiIconName,
    sizeDp: Int = 18,
    color: UiColorRole = UiColorRole.TextSecondary,
    strokeWidthDp: Float = 1.6f
)

/**
 * 根据可用宽度选择布局形态。
 *
 * 供公共根界面在“桌面三栏”和“移动单栏”之间切换，测量的是内容容器宽度，
 * 窗口缩放或屏幕旋转都会触发布局更新。
 */
@Composable
expect fun UiAdaptiveLayout(
    breakpointDp: Int = 900,
    content: @Composable (isWide: Boolean) -> Unit
)
