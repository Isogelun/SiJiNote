package com.isogelun.sijinote.app.note

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.app.components.IconAction
import com.isogelun.sijinote.app.components.PrimaryCircleButton
import com.isogelun.sijinote.app.note.components.NoteCard
import com.isogelun.sijinote.app.route.AppRoute
import com.isogelun.sijinote.app.route.AppSection
import com.isogelun.sijinote.model.Note
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiIconName
import com.isogelun.sijinote.ui.UiJustify
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextField
import com.isogelun.sijinote.ui.UiTextRole

/** 桌面中栏：列表头部 + 分组卡片。 */
@Composable
fun NoteListPanel(state: AppState, widthDp: Int) {
    val section = state.section
    val notes = state.notesForSection(section)
    val title = when (section) {
        AppSection.Library -> "产品知识库"
        AppSection.Notes -> "全部笔记"
        AppSection.Favorites -> "收藏"
        AppSection.Search -> "搜索结果"
        AppSection.Settings -> "设置"
    }

    UiColumn(
        widthDp = widthDp,
        fillMaxHeight = true,
        background = UiColorRole.SurfaceList
    ) {
        UiColumn(paddingDp = 20, spacingDp = 14, fillMaxWidth = true) {
            UiRow(fillMaxWidth = true, align = UiAlign.Center, justify = UiJustify.SpaceBetween) {
                UiRow(spacingDp = 8, align = UiAlign.Center) {
                    UiText(text = title, role = UiTextRole.Heading, maxLines = 1)
                    UiText(text = "${notes.size} 篇", role = UiTextRole.Caption)
                }
                UiRow(spacingDp = 10, align = UiAlign.Center) {
                    IconAction(icon = UiIconName.Sort, onClick = {}, sizeDp = 16, hitBoxDp = 22)
                    IconAction(icon = UiIconName.Filter, onClick = {}, sizeDp = 16, hitBoxDp = 22)
                    UiBox(
                        widthDp = 24,
                        heightDp = 24,
                        cornerRadiusDp = 6,
                        background = UiColorRole.Primary,
                        align = UiAlign.Center,
                        onClick = { state.openEditor(state.newNoteId()) }
                    ) {
                        UiIcon(
                            name = UiIconName.Plus,
                            sizeDp = 14,
                            color = UiColorRole.OnPrimary,
                            strokeWidthDp = 2f
                        )
                    }
                }
            }

            DesktopSearchField(
                placeholder = "在产品知识库中搜索或问 AI…",
                query = if (section == AppSection.Search) state.searchQuery else null,
                onQueryChange = { state.searchQuery = it },
                onClick = {
                    state.section = AppSection.Search
                    state.mobileRoute = AppRoute.Search
                }
            )

            FilterChipsRow(
                items = listOf("全部", "笔记", "文档", "摘录"),
                selected = state.activeFilter,
                solidSelected = false,
                onSelect = { state.activeFilter = it }
            )
        }

        Flex(1f) {
            UiColumn(
                paddingDp = 12,
                spacingDp = 8,
                fillMaxWidth = true,
                fillMaxHeight = true,
                scrollable = true
            ) {
                if (section == AppSection.Library) {
                    if (notes.isNotEmpty()) {
                        GroupLabel("2025 产品规划")
                        notes.take(3).forEach { note ->
                            NoteCard(
                                note = note,
                                selected = note.id == state.selectedNoteId,
                                onClick = { state.openNote(note.id) }
                            )
                        }
                    }
                    if (notes.size > 3) {
                        UiSpacer(heightDp = 6)
                        GroupLabel("需求池")
                        notes.drop(3).forEach { note ->
                            NoteCard(
                                note = note,
                                selected = note.id == state.selectedNoteId,
                                onClick = { state.openNote(note.id) }
                            )
                        }
                    }
                } else {
                    GroupLabel(
                        when (section) {
                            AppSection.Favorites -> "已收藏"
                            AppSection.Search -> "搜索结果"
                            else -> "最近编辑"
                        }
                    )
                    notes.forEach { note ->
                        NoteCard(
                            note = note,
                            selected = note.id == state.selectedNoteId,
                            onClick = { state.openNote(note.id) }
                        )
                    }
                }

                if (notes.isEmpty()) {
                    UiSpacer(heightDp = 12)
                    UiText(
                        text = "没有找到相关笔记",
                        role = UiTextRole.Label,
                        color = UiColorRole.TextTertiary
                    )
                }
            }
        }
    }
}

/** 移动端单栏笔记列表。 */
@Composable
fun MobileNoteListPage(state: AppState) {
    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface,
        paddingDp = 20,
        spacingDp = 14,
        scrollable = true
    ) {
        UiRow(fillMaxWidth = true, align = UiAlign.Center, justify = UiJustify.SpaceBetween) {
            UiColumn(spacingDp = 6) {
                UiText(text = "我的笔记", role = UiTextRole.Title)
                UiText(
                    text = "10 月 28 日 · 共 ${state.notes.size} 篇笔记",
                    role = UiTextRole.Caption
                )
            }
            PrimaryCircleButton(
                icon = UiIconName.Plus,
                sizeDp = 40,
                onClick = { state.openEditor(state.newNoteId()) }
            )
        }

        MobileSearchField(
            placeholder = "搜索笔记或标签",
            onClick = { state.mobileRoute = AppRoute.Search }
        )

        FilterChipsRow(
            items = listOf("全部", "工作", "灵感", "生活"),
            selected = state.activeFilter,
            solidSelected = true,
            onSelect = { state.activeFilter = it }
        )

        val filtered = state.notesForFilter(state.activeFilter)
        val pinned = filtered.filter { it.pinned }
        val rest = filtered.filterNot { it.pinned }

        if (pinned.isNotEmpty()) {
            GroupLabel("置顶笔记")
            pinned.forEach { note -> MobileNoteCard(state, note) }
        }

        UiRow(fillMaxWidth = true, align = UiAlign.Center, justify = UiJustify.SpaceBetween) {
            GroupLabel("最近编辑")
            UiText(text = "查看全部", role = UiTextRole.Micro, color = UiColorRole.Primary)
        }
        rest.forEach { note -> MobileNoteCard(state, note) }
    }
}

@Composable
private fun MobileNoteCard(state: AppState, note: Note) {
    NoteCard(
        note = note,
        selected = false,
        titleRole = UiTextRole.Subheading,
        onClick = { state.openEditor(note.id) }
    )
}

/** 分组小标题。 */
@Composable
fun GroupLabel(text: String) {
    UiText(text = text, role = UiTextRole.Caption, color = UiColorRole.TextTertiary)
}

/** 筛选标签行；桌面为浅底描边，移动端选中项为实心主色胶囊。 */
@Composable
fun FilterChipsRow(
    items: List<String>,
    selected: String,
    solidSelected: Boolean,
    onSelect: (String) -> Unit
) {
    UiRow(spacingDp = 8, align = UiAlign.Center) {
        items.forEach { item ->
            val active = item == selected
            UiBox(
                heightDp = 30,
                paddingHorizontalDp = 14,
                cornerRadiusDp = if (solidSelected) 15 else 8,
                background = when {
                    !active -> null
                    solidSelected -> UiColorRole.Primary
                    else -> UiColorRole.PrimarySoft
                },
                borderColor = if (active) null else UiColorRole.DividerStrong,
                align = UiAlign.Center,
                onClick = { onSelect(item) }
            ) {
                UiText(
                    text = item,
                    role = UiTextRole.Label,
                    color = when {
                        !active -> UiColorRole.TextSecondary
                        solidSelected -> UiColorRole.OnPrimary
                        else -> UiColorRole.Primary
                    }
                )
            }
        }
    }
}

@Composable
private fun DesktopSearchField(
    placeholder: String,
    query: String?,
    onQueryChange: (String) -> Unit,
    onClick: () -> Unit
) {
    UiRow(
        fillMaxWidth = true,
        heightDp = 40,
        paddingHorizontalDp = 12,
        background = UiColorRole.SurfaceField,
        cornerRadiusDp = 10,
        align = UiAlign.Center,
        onClick = if (query == null) onClick else null
    ) {
        UiIcon(name = UiIconName.Search, sizeDp = 16, color = UiColorRole.TextTertiary)
        UiSpacer(widthDp = 8)
        Flex(1f) {
            if (query == null) {
                UiText(
                    text = placeholder,
                    role = UiTextRole.Label,
                    color = UiColorRole.TextTertiary,
                    maxLines = 1
                )
            } else {
                UiTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = placeholder,
                    role = UiTextRole.Label,
                    fillMaxWidth = true
                )
            }
        }
        UiSpacer(widthDp = 8)
        UiBox(
            widthDp = 22,
            heightDp = 18,
            cornerRadiusDp = 5,
            background = UiColorRole.PrimarySoft,
            align = UiAlign.Center
        ) {
            UiText(text = "AI", role = UiTextRole.Micro, color = UiColorRole.Primary)
        }
    }
}

@Composable
private fun MobileSearchField(placeholder: String, onClick: () -> Unit) {
    UiRow(
        fillMaxWidth = true,
        heightDp = 44,
        paddingHorizontalDp = 14,
        background = UiColorRole.Surface,
        cornerRadiusDp = 12,
        borderColor = UiColorRole.DividerStrong,
        align = UiAlign.Center,
        onClick = onClick
    ) {
        UiIcon(name = UiIconName.Search, sizeDp = 16, color = UiColorRole.TextTertiary)
        UiSpacer(widthDp = 8)
        UiText(
            text = placeholder,
            role = UiTextRole.Label,
            color = UiColorRole.TextTertiary,
            maxLines = 1
        )
    }
}
