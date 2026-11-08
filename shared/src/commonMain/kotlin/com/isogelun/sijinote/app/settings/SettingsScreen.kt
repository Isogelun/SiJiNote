package com.isogelun.sijinote.app.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiDivider
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiIconName
import com.isogelun.sijinote.ui.UiJustify
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

/** 桌面主区设置面板。 */
@Composable
fun DesktopSettingsPane(state: AppState) {
    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface,
        paddingDp = 40,
        scrollable = true
    ) {
        UiColumn(fillMaxWidth = true, maxWidthDp = 470, spacingDp = 0) {
            UiText(text = "设置", role = UiTextRole.Display)
            UiSpacer(heightDp = 24)
            SettingsContent(state)
            UiSpacer(heightDp = 32)
        }
    }
}

/** 移动端设置页。 */
@Composable
fun MobileSettingsPage(state: AppState) {
    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface,
        paddingHorizontalDp = 20,
        paddingVerticalDp = 16,
        scrollable = true
    ) {
        UiText(text = "设置", role = UiTextRole.Title)
        UiSpacer(heightDp = 20)
        SettingsContent(state)
        UiSpacer(heightDp = 24)
    }
}

@Composable
private fun SettingsContent(state: AppState) {
    var darkMode by remember { mutableStateOf(false) }
    var autoSync by remember { mutableStateOf(true) }

    UiColumn(fillMaxWidth = true, spacingDp = 22) {
        AccountCard()

        SettingsGroup(title = "编辑器偏好") {
            SettingsRow(label = "默认字体", value = "Inter", showChevron = true)
            UiDivider(color = UiColorRole.Divider)
            SettingsRow(label = "正文字号", value = "15 px", showChevron = true)
        }

        SettingsGroup(title = "外观") {
            SettingsRow(label = "深色模式") {
                ToggleSwitch(checked = darkMode, onChange = { darkMode = it })
            }
            UiDivider(color = UiColorRole.Divider)
            SettingsRow(label = "主题色", value = "品牌蓝", showChevron = true) {
                UiBox(widthDp = 10, heightDp = 10, cornerRadiusDp = 5, background = UiColorRole.Primary)
            }
        }

        SettingsGroup(title = "同步与备份") {
            SettingsRow(label = "自动同步") {
                ToggleSwitch(checked = autoSync, onChange = { autoSync = it })
            }
            UiDivider(color = UiColorRole.Divider)
            SettingsRow(label = "上次同步", value = "今天 10:24")
        }

        UiRow(fillMaxWidth = true, justify = UiJustify.Center) {
            UiText(
                text = "林间笔记 · 版本 2.4.0",
                role = UiTextRole.Caption,
                color = UiColorRole.TextTertiary
            )
        }
    }
}

@Composable
private fun AccountCard() {
    UiRow(
        fillMaxWidth = true,
        paddingDp = 16,
        spacingDp = 12,
        background = UiColorRole.Surface,
        cornerRadiusDp = 12,
        borderColor = UiColorRole.Divider,
        align = UiAlign.Center,
        justify = UiJustify.SpaceBetween
    ) {
        UiRow(spacingDp = 12, align = UiAlign.Center) {
            UiBox(
                widthDp = 40,
                heightDp = 40,
                cornerRadiusDp = 20,
                background = UiColorRole.PrimarySoft,
                align = UiAlign.Center
            ) {
                UiIcon(name = UiIconName.User, sizeDp = 20, color = UiColorRole.Primary)
            }
            UiColumn(spacingDp = 2) {
                UiText(text = "林间笔记", role = UiTextRole.Subheading)
                UiText(
                    text = "linjian@notes.app · 已同步",
                    role = UiTextRole.Caption,
                    color = UiColorRole.TextTertiary
                )
            }
        }
        UiIcon(name = UiIconName.ChevronRight, sizeDp = 14, color = UiColorRole.TextTertiary)
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    UiColumn(fillMaxWidth = true, spacingDp = 8) {
        UiText(text = title, role = UiTextRole.Caption, color = UiColorRole.TextTertiary)
        UiColumn(
            fillMaxWidth = true,
            background = UiColorRole.Surface,
            cornerRadiusDp = 12,
            borderColor = UiColorRole.Divider,
            spacingDp = 0
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String? = null,
    showChevron: Boolean = false,
    trailing: (@Composable () -> Unit)? = null
) {
    UiRow(
        fillMaxWidth = true,
        heightDp = 56,
        paddingHorizontalDp = 16,
        justify = UiJustify.SpaceBetween,
        align = UiAlign.Center
    ) {
        UiText(text = label, role = UiTextRole.Body)
        UiRow(spacingDp = 8, align = UiAlign.Center) {
            if (trailing != null) trailing()
            if (value != null) {
                UiText(text = value, role = UiTextRole.Label, color = UiColorRole.TextTertiary)
            }
            if (showChevron) {
                UiIcon(name = UiIconName.ChevronRight, sizeDp = 14, color = UiColorRole.TextTertiary)
            }
        }
    }
}

/** 中立开关：用胶囊加圆点实现，两端表现一致。 */
@Composable
private fun ToggleSwitch(checked: Boolean, onChange: (Boolean) -> Unit) {
    UiBox(
        widthDp = 44,
        heightDp = 26,
        paddingDp = 3,
        cornerRadiusDp = 13,
        background = if (checked) UiColorRole.Primary else UiColorRole.DividerStrong,
        align = if (checked) UiAlign.End else UiAlign.Start,
        onClick = { onChange(!checked) }
    ) {
        UiBox(
            widthDp = 20,
            heightDp = 20,
            cornerRadiusDp = 10,
            background = UiColorRole.Surface
        )
    }
}
