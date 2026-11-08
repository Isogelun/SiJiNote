package com.isogelun.sijinote.app.note.components

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.components.TagChip
import com.isogelun.sijinote.model.Note
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiJustify
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

/**
 * 笔记卡片：标题、摘要、时间与类型角标。
 *
 * 选中态使用浅色底且不描边，未选中为白底细描边，与设计稿一致。
 */
@Composable
fun NoteCard(
    note: Note,
    selected: Boolean = false,
    titleRole: UiTextRole = UiTextRole.BodyStrong,
    onClick: () -> Unit
) {
    UiColumn(
        fillMaxWidth = true,
        paddingDp = 16,
        spacingDp = 8,
        background = if (selected) UiColorRole.SurfaceSelected else UiColorRole.Surface,
        cornerRadiusDp = 12,
        borderColor = if (selected) null else UiColorRole.Divider,
        onClick = onClick
    ) {
        UiText(
            text = note.title.ifBlank { "未命名笔记" },
            role = titleRole,
            maxLines = 1
        )
        if (note.summary.isNotBlank()) {
            UiText(
                text = note.summary,
                role = UiTextRole.Label,
                color = UiColorRole.TextSecondary,
                maxLines = 2
            )
        }
        UiRow(
            fillMaxWidth = true,
            justify = UiJustify.SpaceBetween,
            align = UiAlign.Center
        ) {
            UiText(text = note.updatedLabel, role = UiTextRole.Caption)
            TagChip(text = note.kind.label, heightDp = 24)
        }
    }
}
