package com.isogelun.sijinote.app.note

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.app.components.InitialAvatar
import com.isogelun.sijinote.app.components.IconAction
import com.isogelun.sijinote.app.components.TagChip
import com.isogelun.sijinote.app.note.components.NoteBlocks
import com.isogelun.sijinote.app.note.components.toggleTodoAt
import com.isogelun.sijinote.app.route.AppRoute
import com.isogelun.sijinote.model.Note
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
import com.isogelun.sijinote.ui.UiTextField
import com.isogelun.sijinote.ui.UiTextRole

/** 桌面右侧主区：工具栏 + 文档排版列 + 状态栏。 */
@Composable
fun DesktopEditorPane(state: AppState) {
    val note = state.selectedNote
    if (note == null) {
        EmptyEditorPane()
        return
    }

    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface
    ) {
        EditorToolbar(note = note, onToggleFavorite = { state.toggleFavorite(note.id) })
        UiDivider(color = UiColorRole.Divider)

        Flex(1f) {
            UiColumn(
                paddingDp = 40,
                fillMaxWidth = true,
                fillMaxHeight = true,
                scrollable = true
            ) {
                UiColumn(fillMaxWidth = true, maxWidthDp = 470, spacingDp = 0) {                    UiText(
                        text = note.title.ifBlank { "未命名笔记" },
                        role = UiTextRole.Display,
                        maxLines = 3,
                        fillMaxWidth = true
                    )
                    UiSpacer(heightDp = 16)
                    DocumentMeta(note)
                    UiSpacer(heightDp = 20)
                    UiDivider(color = UiColorRole.Divider)
                    UiSpacer(heightDp = 24)
                    NoteBlocks(
                        blocks = note.blocks,
                        onToggleTodo = { index ->
                            state.updateNote(note.copy(blocks = note.blocks.toggleTodoAt(index)))
                        }
                    )
                }
            }
        }

        UiDivider(color = UiColorRole.Divider)
        EditorStatusBar(wordCount = wordCount(note))
    }
}

/** 移动端编辑页：顶部返回与完成、可编辑标题、正文块、底部格式栏。 */
@Composable
fun MobileEditorPage(state: AppState, noteId: String?) {
    val note = noteId?.let { id -> state.notes.firstOrNull { it.id == id } }

    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface
    ) {
        UiRow(
            heightDp = 56,
            fillMaxWidth = true,
            paddingHorizontalDp = 20,
            justify = UiJustify.SpaceBetween,
            align = UiAlign.Center
        ) {
            IconAction(
                icon = UiIconName.ChevronLeft,
                sizeDp = 20,
                color = UiColorRole.TextPrimary,
                hitBoxDp = 32,
                onClick = { state.mobileRoute = AppRoute.Notes }
            )
            UiText(text = "编辑中", role = UiTextRole.Label, color = UiColorRole.TextTertiary)
            UiBox(
                heightDp = 32,
                paddingHorizontalDp = 18,
                cornerRadiusDp = 16,
                background = UiColorRole.Primary,
                align = UiAlign.Center,
                onClick = { state.mobileRoute = AppRoute.Notes }
            ) {
                UiText(text = "完成", role = UiTextRole.Label, color = UiColorRole.OnPrimary)
            }
        }

        Flex(1f) {
            UiColumn(
                paddingHorizontalDp = 20,
                fillMaxWidth = true,
                fillMaxHeight = true,
                spacingDp = 0,
                scrollable = true
            ) {
                if (note == null) {
                    UiText(text = "笔记不存在", role = UiTextRole.Heading)
                } else {
                    UiSpacer(heightDp = 8)
                    UiTextField(
                        value = note.title,
                        onValueChange = { value -> state.updateNote(note.copy(title = value)) },
                        placeholder = "无标题笔记",
                        role = UiTextRole.Title,
                        fillMaxWidth = true
                    )
                    UiSpacer(heightDp = 12)
                    DocumentMeta(note)
                    UiSpacer(heightDp = 16)
                    UiDivider(color = UiColorRole.Divider)
                    UiSpacer(heightDp = 18)
                    if (note.blocks.isEmpty()) {
                        UiText(
                            text = "开始写点什么…",
                            role = UiTextRole.Body,
                            color = UiColorRole.TextTertiary
                        )
                    } else {
                        NoteBlocks(
                            blocks = note.blocks,
                            onToggleTodo = { index ->
                                state.updateNote(note.copy(blocks = note.blocks.toggleTodoAt(index)))
                            }
                        )
                    }
                }
            }
        }

        UiColumn(fillMaxWidth = true, background = UiColorRole.Surface) {
            UiDivider(color = UiColorRole.Divider)
            UiRow(
                heightDp = 54,
                fillMaxWidth = true,
                paddingHorizontalDp = 24,
                justify = UiJustify.SpaceBetween,
                align = UiAlign.Center
            ) {
                IconAction(UiIconName.TextH, onClick = {}, sizeDp = 20, hitBoxDp = 32)
                IconAction(UiIconName.TextB, onClick = {}, sizeDp = 20, hitBoxDp = 32)
                IconAction(UiIconName.TextI, onClick = {}, sizeDp = 20, hitBoxDp = 32)
                IconAction(UiIconName.ListBullet, onClick = {}, sizeDp = 20, hitBoxDp = 32)
                IconAction(UiIconName.Image, onClick = {}, sizeDp = 20, hitBoxDp = 32)
                IconAction(UiIconName.Link, onClick = {}, sizeDp = 20, hitBoxDp = 32)
            }
            UiBox(fillMaxWidth = true, heightDp = 22)
        }
    }
}

@Composable
private fun EditorToolbar(note: Note, onToggleFavorite: () -> Unit) {
    UiRow(
        heightDp = 56,
        fillMaxWidth = true,
        paddingHorizontalDp = 24,
        justify = UiJustify.SpaceBetween,
        align = UiAlign.Center,
        background = UiColorRole.Surface
    ) {
        UiRow(spacingDp = 8, align = UiAlign.Center) {
            UiIcon(name = UiIconName.Folder, sizeDp = 16, color = UiColorRole.TextTertiary)
            UiText(text = "产品知识库", role = UiTextRole.Label, color = UiColorRole.TextSecondary)
            UiText(text = "/", role = UiTextRole.Label, color = UiColorRole.TextTertiary)
            UiText(
                text = note.title.ifBlank { "未命名笔记" },
                role = UiTextRole.Label,
                color = UiColorRole.TextPrimary,
                maxLines = 1
            )
        }
        UiRow(spacingDp = 14, align = UiAlign.Center) {
            IconAction(
                icon = UiIconName.Star,
                sizeDp = 18,
                color = if (note.favorite) UiColorRole.Primary else UiColorRole.TextSecondary,
                onClick = onToggleFavorite
            )
            IconAction(icon = UiIconName.Share, sizeDp = 18, onClick = {})
            IconAction(icon = UiIconName.More, sizeDp = 18, onClick = {})
            UiBox(widthDp = 1, heightDp = 18, background = UiColorRole.DividerStrong)
            UiBox(
                heightDp = 32,
                paddingHorizontalDp = 16,
                cornerRadiusDp = 8,
                background = UiColorRole.Primary,
                align = UiAlign.Center,
                onClick = {}
            ) {
                UiText(text = "分享", role = UiTextRole.Label, color = UiColorRole.OnPrimary)
            }
        }
    }
}

@Composable
private fun DocumentMeta(note: Note) {
    val time = note.updatedAt.ifBlank { note.updatedLabel }
    UiRow(spacingDp = 10, align = UiAlign.Center) {
        UiText(
            text = if (time.isBlank()) "刚刚更新" else "更新于 $time",
            role = UiTextRole.Caption
        )
        UiBox(
            widthDp = 3,
            heightDp = 3,
            cornerRadiusDp = 2,
            background = UiColorRole.TextTertiary
        )
        UiText(text = "${wordCount(note)} 字", role = UiTextRole.Caption)
        if (note.tag.isNotBlank()) {
            TagChip(
                text = "#${note.tag}",
                heightDp = 26,
                background = UiColorRole.PrimarySoft,
                color = UiColorRole.Primary
            )
        }
    }
}

@Composable
private fun EditorStatusBar(wordCount: Int) {
    UiRow(
        heightDp = 36,
        fillMaxWidth = true,
        paddingHorizontalDp = 24,
        justify = UiJustify.SpaceBetween,
        align = UiAlign.Center,
        background = UiColorRole.SurfaceList
    ) {
        UiText(
            text = "已保存 · $wordCount 字 · 阅读约 ${(wordCount / 320).coerceAtLeast(1)} 分钟",
            role = UiTextRole.Caption
        )
        UiRow(spacingDp = 8, align = UiAlign.Center) {
            InitialAvatar(text = "林", sizeDp = 20, background = UiColorRole.Primary)
            UiText(text = "2 人协作", role = UiTextRole.Caption)
        }
    }
}

@Composable
private fun EmptyEditorPane() {
    UiBox(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface,
        align = UiAlign.Center
    ) {
        UiText(text = "选择左侧的笔记开始阅读", role = UiTextRole.Label, color = UiColorRole.TextTertiary)
    }
}

private fun wordCount(note: Note): Int {
    val body = note.blocks.joinToString("") { block ->
        when (block) {
            is com.isogelun.sijinote.model.NoteBlock.Heading -> block.text
            is com.isogelun.sijinote.model.NoteBlock.Paragraph -> block.text
            is com.isogelun.sijinote.model.NoteBlock.Quote -> block.text
            is com.isogelun.sijinote.model.NoteBlock.Todo -> block.text
        }
    }
    val text = note.title + body
    return text.count { !it.isWhitespace() }
}
