package com.isogelun.sijinote.app.components

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.route.AppSection
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiIconName
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

/** 分区到图标的映射，供桌面导航栏与移动端底部导航共用。 */
fun sectionIcon(section: AppSection): UiIconName = when (section) {
    AppSection.Library -> UiIconName.Layers
    AppSection.Notes -> UiIconName.Note
    AppSection.Favorites -> UiIconName.Star
    AppSection.Search -> UiIconName.Search
    AppSection.Settings -> UiIconName.Gear
}

/** 小角标：笔记类型、标签。 */
@Composable
fun TagChip(
    text: String,
    heightDp: Int = 22,
    background: UiColorRole = UiColorRole.SurfaceTag,
    color: UiColorRole = UiColorRole.TextSecondary,
    onClick: (() -> Unit)? = null
) {
    UiBox(
        heightDp = heightDp,
        paddingHorizontalDp = 8,
        background = background,
        cornerRadiusDp = 6,
        align = UiAlign.Center,
        onClick = onClick
    ) {
        UiText(text = text, role = UiTextRole.Micro, color = color)
    }
}

/** 可点击的图标按钮，命中区域按 hitBoxDp 放大，视觉图标保持 sizeDp。 */
@Composable
fun IconAction(
    icon: UiIconName,
    onClick: () -> Unit,
    sizeDp: Int = 18,
    color: UiColorRole = UiColorRole.TextSecondary,
    hitBoxDp: Int = 28,
    strokeWidthDp: Float = 1.6f
) {
    UiBox(
        widthDp = hitBoxDp,
        heightDp = hitBoxDp,
        cornerRadiusDp = 8,
        align = UiAlign.Center,
        onClick = onClick
    ) {
        UiIcon(name = icon, sizeDp = sizeDp, color = color, strokeWidthDp = strokeWidthDp)
    }
}

/** 实心圆形主按钮，用于新建笔记。 */
@Composable
fun PrimaryCircleButton(
    icon: UiIconName,
    sizeDp: Int,
    onClick: () -> Unit
) {
    UiBox(
        widthDp = sizeDp,
        heightDp = sizeDp,
        cornerRadiusDp = sizeDp / 2,
        background = UiColorRole.Primary,
        align = UiAlign.Center,
        onClick = onClick
    ) {
        UiIcon(name = icon, sizeDp = sizeDp / 2, color = UiColorRole.OnPrimary, strokeWidthDp = 2f)
    }
}

/** 深色圆形头像，用首字代替真实图片。 */
@Composable
fun InitialAvatar(
    text: String,
    sizeDp: Int = 32,
    background: UiColorRole = UiColorRole.Ink
) {
    UiBox(
        widthDp = sizeDp,
        heightDp = sizeDp,
        cornerRadiusDp = sizeDp / 2,
        background = background,
        align = UiAlign.Center
    ) {
        UiText(text = text, role = UiTextRole.Micro, color = UiColorRole.OnPrimary)
    }
}
