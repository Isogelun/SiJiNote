package com.isogelun.sijinote.model

/** 笔记条目的类型标记，对应设计稿卡片右下角的角标。 */
enum class NoteKind(val label: String) {
    Note("笔记"),
    Doc("文档"),
    Excerpt("摘录")
}

/** 正文块。渲染层按块类型决定层级与间距，避免把排版信息塞进纯文本。 */
sealed interface NoteBlock {
    data class Paragraph(val text: String) : NoteBlock

    data class Heading(val text: String) : NoteBlock

    data class Todo(val text: String, val done: Boolean = false) : NoteBlock

    data class Quote(val text: String) : NoteBlock
}

/** 初始笔记模型，id 由创建笔记的业务逻辑提供并保持稳定。 */
data class Note(
    val id: String,
    val title: String = "",
    val content: String = "",
    val tag: String = "",
    /** 列表里的短时间标签，例如 10:24、昨天。 */
    val updatedLabel: String = "",
    /** 文档区展示的完整更新时间；为空时回退到 [updatedLabel]。 */
    val updatedAt: String = "",
    val kind: NoteKind = NoteKind.Note,
    val blocks: List<NoteBlock> = emptyList(),
    val pinned: Boolean = false,
    val favorite: Boolean = false
) {
    /** 列表摘要：取正文首段并压缩空白，超出长度补省略号。 */
    val summary: String
        get() {
            val source = blocks.filterIsInstance<NoteBlock.Paragraph>().firstOrNull()?.text
                ?: content
            val flat = source.replace('\n', ' ').replace("  ", " ").trim()
            return if (flat.length <= 80) flat else flat.take(80) + "…"
        }
}
