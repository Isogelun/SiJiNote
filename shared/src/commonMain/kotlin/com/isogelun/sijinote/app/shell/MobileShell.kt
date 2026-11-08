package com.isogelun.sijinote.app.shell

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.app.components.sectionIcon
import com.isogelun.sijinote.app.favorites.MobileFavoritesPage
import com.isogelun.sijinote.app.note.MobileEditorPage
import com.isogelun.sijinote.app.note.MobileNoteListPage
import com.isogelun.sijinote.app.route.AppRoute
import com.isogelun.sijinote.app.route.AppSection
import com.isogelun.sijinote.app.search.MobileSearchPage
import com.isogelun.sijinote.app.settings.MobileSettingsPage
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiJustify
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

private val mobileTabs = listOf(
    AppSection.Notes,
    AppSection.Search,
    AppSection.Favorites,
    AppSection.Settings
)

/** 窄屏单栏：内容区 + 底部胶囊导航。 */
@Composable
fun MobileShell(state: AppState) {
    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface
    ) {
        Flex(1f) {
            when (val route = state.mobileRoute) {
                is AppRoute.Editor -> MobileEditorPage(state = state, noteId = route.noteId)
                AppRoute.Notes -> MobileNoteListPage(state)
                AppRoute.Search -> MobileSearchPage(state)
                AppRoute.Favorites -> MobileFavoritesPage(state)
                AppRoute.Settings -> MobileSettingsPage(state)
            }
        }
        MobileBottomNav(state)
    }
}

@Composable
private fun MobileBottomNav(state: AppState) {
    UiBox(
        fillMaxWidth = true,
        paddingHorizontalDp = 14,
        paddingVerticalDp = 8,
        background = UiColorRole.Surface
    ) {
        UiRow(
            fillMaxWidth = true,
            heightDp = 62,
            paddingHorizontalDp = 8,
            background = UiColorRole.Surface,
            cornerRadiusDp = 31,
            borderColor = UiColorRole.Divider,
            align = UiAlign.Center,
            justify = UiJustify.SpaceBetween
        ) {
            mobileTabs.forEach { section ->
                BottomNavItem(section = section, state = state)
            }
        }
    }
}

@Composable
private fun BottomNavItem(section: AppSection, state: AppState) {
    val active = when (section) {
        AppSection.Notes -> state.mobileRoute is AppRoute.Editor || state.mobileRoute == AppRoute.Notes
        AppSection.Search -> state.mobileRoute == AppRoute.Search
        AppSection.Favorites -> state.mobileRoute == AppRoute.Favorites
        AppSection.Settings -> state.mobileRoute == AppRoute.Settings
        AppSection.Library -> false
    }

    UiColumn(
        widthDp = if (active) 100 else 72,
        heightDp = 48,
        spacingDp = 2,
        cornerRadiusDp = 24,
        background = if (active) UiColorRole.Primary else null,
        align = UiAlign.Center,
        justify = UiJustify.Center,
        onClick = {
            state.section = if (section == AppSection.Notes) AppSection.Library else section
            state.mobileRoute = when (section) {
                AppSection.Notes -> AppRoute.Notes
                AppSection.Search -> AppRoute.Search
                AppSection.Favorites -> AppRoute.Favorites
                AppSection.Settings -> AppRoute.Settings
                AppSection.Library -> AppRoute.Notes
            }
        }
    ) {
        UiIcon(
            name = sectionIcon(section),
            sizeDp = 18,
            color = if (active) UiColorRole.OnPrimary else UiColorRole.TextSecondary
        )
        UiText(
            text = section.label,
            role = UiTextRole.Micro,
            color = if (active) UiColorRole.OnPrimary else UiColorRole.TextSecondary
        )
    }
}
