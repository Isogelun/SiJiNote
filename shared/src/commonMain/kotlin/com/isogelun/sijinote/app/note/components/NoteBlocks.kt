package com.isogelun.sijinote.app.note.components

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.model.NoteBlock
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiIconName
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

/**
 * 正文块渲染。
 *
 * 间距按块类型手工给出，而不是依赖统一 spacing：标题需要更大的上留白，
 * 列表项之间则保持紧凑，这样两端渲染结果一致。
 */
@Composable
fun NoteBlocks(
    blocks: List<NoteBlock>,
    onToggleTodo: ((Int) -> Unit)? = null
) {
    UiColumn(spacingDp = 0, fillMaxWidth = true) {
        blocks.forEachIndexed { index, block ->
            when (block) {
                is NoteBlock.Heading -> {
                    UiSpacer(heightDp = 12)
                    UiText(text = block.text, role = UiTextRole.Subheading, fillMaxWidth = true)
                    UiSpacer(heightDp = 12)
                }

                is NoteBlock.Paragraph -> {
                    UiText(text = block.text, role = UiTextRole.Body, fillMaxWidth = true)
                    UiSpacer(heightDp = 18)
                }

                is NoteBlock.Todo -> {
                    UiRow(
                        fillMaxWidth = true,
                        spacingDp = 12,
                        align = UiAlign.Center,
                        onClick = onToggleTodo?.let { toggle -> { toggle(index) } }
                    ) {
                        UiBox(
                            widthDp = 18,
                            heightDp = 18,
                            cornerRadiusDp = 5,
                            background = if (block.done) UiColorRole.Primary else null,
                            borderColor = if (block.done) null else UiColorRole.DividerStrong,
                            align = UiAlign.Center
                        ) {
                            if (block.done) {
                                UiIcon(
                                    name = UiIconName.Check,
                                    sizeDp = 12,
                                    color = UiColorRole.OnPrimary,
                                    strokeWidthDp = 2.2f
                                )
                            }
                        }
                        UiText(
                            text = block.text,
                            role = UiTextRole.Body,
                            color = if (block.done) UiColorRole.TextTertiary else UiColorRole.TextPrimary,
                            fillMaxWidth = true
                        )
                    }
                    UiSpacer(heightDp = 10)
                }

                is NoteBlock.Quote -> {
                    UiSpacer(heightDp = 4)
                    UiBox(
                        fillMaxWidth = true,
                        paddingDp = 16,
                        background = UiColorRole.SurfaceField,
                        cornerRadiusDp = 4
                    ) {
                        UiText(
                            text = block.text,
                            role = UiTextRole.Body,
                            color = UiColorRole.TextSecondary,
                            fillMaxWidth = true
                        )
                    }
                    UiSpacer(heightDp = 20)
                }
            }
        }
    }
}

/** 切换某个待办块的完成状态，返回新的块列表。 */
fun List<NoteBlock>.toggleTodoAt(index: Int): List<NoteBlock> {
    val target = getOrNull(index) as? NoteBlock.Todo ?: return this
    return mapIndexed { position, block ->
        if (position == index) target.copy(done = !target.done) else block
    }
}
