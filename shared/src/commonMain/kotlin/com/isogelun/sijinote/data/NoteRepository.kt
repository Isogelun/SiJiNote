package com.isogelun.sijinote.data

import com.isogelun.sijinote.model.Note

/** 数据访问约定，目前尚无存储实现；保存失败由实现抛出异常，调用方负责反馈。 */
interface NoteRepository {
    suspend fun getNotes(): List<Note>

    suspend fun getNote(id: String): Note?

    /** 按稳定 id 新增或更新。 */
    suspend fun saveNote(note: Note)

    /** 删除指定 id 的笔记；不存在时不执行操作。 */
    suspend fun deleteNote(id: String)
}
