package com.isogelun.sijinote.app.note.components

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.model.Note
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiText

@Composable
internal fun NoteSummary(note: Note) {
    UiColumn(paddingDp = 0, spacingDp = 4) {
        UiText(note.title.ifBlank { "未命名笔记" })
        UiText(note.content.lineSequence().firstOrNull().orEmpty().take(80))
    }
}
