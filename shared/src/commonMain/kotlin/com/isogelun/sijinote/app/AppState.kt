package com.isogelun.sijinote.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.isogelun.sijinote.app.route.AppRoute
import com.isogelun.sijinote.app.route.AppSection
import com.isogelun.sijinote.data.sampleNotes
import com.isogelun.sijinote.model.Note

/**
 * 应用级界面状态。
 *
 * 宽窄两种布局共用同一个状态持有者：选中笔记、搜索词、筛选条件与移动端路由都放在这里，
 * 跨断点切换时不会因为布局分支重建而丢失。
 *
 * 当前数据只在内存中，重启后回到示例初始值；持久化与同步仍待数据层实现。
 */
class AppState(initialNotes: List<Note>) {

    val notes = mutableStateListOf<Note>().also { it.addAll(initialNotes) }

    /** 桌面左栏当前分区。 */
    var section by mutableStateOf(AppSection.Library)

    /** 桌面右侧主区展示的笔记。 */
    var selectedNoteId by mutableStateOf(initialNotes.firstOrNull()?.id)

    /** 移动端当前页面。 */
    var mobileRoute by mutableStateOf<AppRoute>(AppRoute.Notes)

    var searchQuery by mutableStateOf("设计")

    /** 移动端列表的筛选标签。 */
    var activeFilter by mutableStateOf("全部")

    private var createdCount = 0

    val pinnedNotes: List<Note> get() = notes.filter { it.pinned }

    val recentNotes: List<Note> get() = notes.filter { !it.pinned }

    val favoriteNotes: List<Note> get() = notes.filter { it.favorite }

    val selectedNote: Note? get() = notes.firstOrNull { it.id == selectedNoteId }

    /** 当前分区对应的笔记集合，桌面列表与移动端列表共用。 */
    fun notesForSection(section: AppSection): List<Note> = when (section) {
        AppSection.Library -> notes
        AppSection.Notes -> recentNotes
        AppSection.Favorites -> favoriteNotes
        AppSection.Search -> searchResults(searchQuery)
        AppSection.Settings -> emptyList()
    }

    fun notesForFilter(filter: String): List<Note> =
        if (filter == "全部" || filter.isBlank()) {
            notes
        } else {
            notes.filter { it.tag == filter || it.kind.label == filter }
        }

    fun searchResults(query: String): List<Note> {
        val keyword = query.trim()
        if (keyword.isEmpty()) return emptyList()
        return notes.filter { note ->
            note.title.contains(keyword, ignoreCase = true) ||
                note.content.contains(keyword, ignoreCase = true) ||
                note.tag.contains(keyword, ignoreCase = true)
        }
    }

    fun openNote(id: String) {
        selectedNoteId = id
    }

    fun openEditor(noteId: String?) {
        if (noteId != null) selectedNoteId = noteId
        mobileRoute = AppRoute.Editor(noteId)
    }

    fun newNoteId(): String {
        createdCount += 1
        val id = "note-new-$createdCount"
        notes.add(
            0,
            Note(
                id = id,
                title = "",
                content = "",
                tag = "工作",
                updatedLabel = "刚刚"
            )
        )
        return id
    }

    fun updateNote(note: Note) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index >= 0) notes[index] = note
    }

    fun toggleFavorite(id: String) {
        val index = notes.indexOfFirst { it.id == id }
        if (index >= 0) notes[index] = notes[index].copy(favorite = !notes[index].favorite)
    }
}

@Composable
fun rememberAppState(): AppState = remember { AppState(sampleNotes()) }
