package com.isogelun.sijinote.app.favorites

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.app.note.components.NoteCard
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

/** 移动端收藏页。 */
@Composable
fun MobileFavoritesPage(state: AppState) {
    val favorites = state.favoriteNotes

    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface,
        paddingHorizontalDp = 20,
        scrollable = true,
        spacingDp = 0
    ) {
        UiSpacer(heightDp = 16)
        UiText(text = "收藏", role = UiTextRole.Title)
        UiSpacer(heightDp = 6)
        UiText(
            text = "共 ${favorites.size} 篇",
            role = UiTextRole.Caption,
            color = UiColorRole.TextTertiary
        )
        UiSpacer(heightDp = 18)

        favorites.forEach { note ->
            NoteCard(
                note = note,
                selected = false,
                titleRole = UiTextRole.Subheading,
                onClick = { state.openEditor(note.id) }
            )
            UiSpacer(heightDp = 12)
        }

        if (favorites.isEmpty()) {
            UiText(
                text = "还没有收藏的笔记",
                role = UiTextRole.Label,
                color = UiColorRole.TextTertiary
            )
        }

        UiSpacer(heightDp = 24)
    }
}
