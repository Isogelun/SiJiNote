package com.isogelun.sijinote.app.note

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiText

/** 编辑页骨架；输入状态、读取笔记和保存操作后续接入。 */
@Composable
fun NoteEditorScreen(noteId: String?) {
    UiColumn {
        UiText(if (noteId == null) "新建笔记" else "编辑笔记")
    }
}
