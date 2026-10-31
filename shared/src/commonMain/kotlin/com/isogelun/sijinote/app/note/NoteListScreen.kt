package com.isogelun.sijinote.app.note

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.note.components.NoteSummary
import com.isogelun.sijinote.model.Note
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiText

@Composable
fun NoteListScreen(notes: List<Note> = emptyList()) {
    UiColumn {
        UiText("四季笔记")
        UiText("我的笔记")
        if (notes.isEmpty()) {
            UiText("还没有笔记")
        } else {
            // 初始展示骨架；长列表的滚动和稳定 key 在列表组件适配后接入。
            notes.forEach { note -> NoteSummary(note) }
        }
    }
}
