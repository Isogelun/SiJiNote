package com.isogelun.sijinote.model

/** 初始笔记模型，id 由创建笔记的业务逻辑提供并保持稳定。 */
data class Note(
    val id: String,
    val title: String = "",
    val content: String = ""
)
