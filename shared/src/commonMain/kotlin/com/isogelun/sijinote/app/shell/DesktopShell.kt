package com.isogelun.sijinote.app.shell

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.app.components.InitialAvatar
import com.isogelun.sijinote.app.components.sectionIcon
import com.isogelun.sijinote.app.note.DesktopEditorPane
import com.isogelun.sijinote.app.note.NoteListPanel
import com.isogelun.sijinote.app.route.AppSection
import com.isogelun.sijinote.app.settings.DesktopSettingsPane
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiDivider
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiJustify
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

/** 宽屏三栏：导航栏 64 + 笔记列表 376 + 主区自适应。 */
@Composable
fun DesktopShell(state: AppState) {
    UiRow(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Background
    ) {
        NavRail(state)

        if (state.section == AppSection.Settings) {
            Flex(1f) {
                DesktopSettingsPane(state)
            }
        } else {
            NoteListPanel(state = state, widthDp = 376)
            Flex(1f) {
                DesktopEditorPane(state)
            }
        }
    }
}

@Composable
private fun NavRail(state: AppState) {
    UiColumn(
        widthDp = 64,
        fillMaxHeight = true,
        paddingVerticalDp = 16,
        paddingHorizontalDp = 8,
        spacingDp = 4,
        background = UiColorRole.SurfacePanel,
        align = UiAlign.Center
    ) {
        UiBox(
            widthDp = 32,
            heightDp = 32,
            cornerRadiusDp = 9,
            background = UiColorRole.Ink,
            align = UiAlign.Center
        ) {
            UiText(text = "思", role = UiTextRole.Label, color = UiColorRole.OnPrimary)
        }

        UiSpacer(heightDp = 4)

        NavRailItem(AppSection.Library, state)
        NavRailItem(AppSection.Notes, state)
        NavRailItem(AppSection.Favorites, state)
        NavRailItem(AppSection.Search, state)

        Flex(1f) { UiSpacer() }

        UiDivider(color = UiColorRole.DividerStrong)
        NavRailItem(AppSection.Settings, state)

        UiSpacer(heightDp = 6)
        InitialAvatar(text = "林", sizeDp = 32, background = UiColorRole.Primary)
    }
}

@Composable
private fun NavRailItem(section: AppSection, state: AppState) {
    val active = state.section == section
    UiColumn(
        widthDp = 48,
        heightDp = 49,
        spacingDp = 3,
        cornerRadiusDp = 8,
        background = if (active) UiColorRole.PrimarySoft else null,
        align = UiAlign.Center,
        justify = UiJustify.Center,
        onClick = { state.section = section }
    ) {
        UiIcon(
            name = sectionIcon(section),
            sizeDp = 18,
            color = if (active) UiColorRole.Primary else UiColorRole.TextSecondary
        )
        UiText(
            text = section.label,
            role = UiTextRole.Micro,
            color = if (active) UiColorRole.Primary else UiColorRole.TextSecondary
        )
    }
}
