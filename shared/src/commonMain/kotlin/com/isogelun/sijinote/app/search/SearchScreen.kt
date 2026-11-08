package com.isogelun.sijinote.app.search

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.AppState
import com.isogelun.sijinote.app.components.IconAction
import com.isogelun.sijinote.app.note.components.NoteCard
import com.isogelun.sijinote.ui.UiAlign
import com.isogelun.sijinote.ui.UiBox
import com.isogelun.sijinote.ui.UiColorRole
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiIcon
import com.isogelun.sijinote.ui.UiIconName
import com.isogelun.sijinote.ui.UiJustify
import com.isogelun.sijinote.ui.UiRow
import com.isogelun.sijinote.ui.UiSpacer
import com.isogelun.sijinote.ui.UiTextField
import com.isogelun.sijinote.ui.UiText
import com.isogelun.sijinote.ui.UiTextRole

private val recentSearches = listOf("设计系统", "周会", "移动端", "读书清单")
private val browsableTags = listOf("工作", "灵感", "生活", "旅行", "会议")

/** 移动端搜索页：激活态输入框、最近搜索、标签浏览与结果列表。 */
@Composable
fun MobileSearchPage(state: AppState) {
    val results = state.searchResults(state.searchQuery)

    UiColumn(
        fillMaxWidth = true,
        fillMaxHeight = true,
        background = UiColorRole.Surface,
        paddingHorizontalDp = 20,
        scrollable = true,
        spacingDp = 0
    ) {
        UiSpacer(heightDp = 16)
        UiText(text = "搜索", role = UiTextRole.Title)
        UiSpacer(heightDp = 16)

        UiRow(
            fillMaxWidth = true,
            heightDp = 46,
            paddingHorizontalDp = 14,
            background = UiColorRole.Surface,
            cornerRadiusDp = 14,
            borderColor = UiColorRole.Primary,
            align = UiAlign.Center
        ) {
            UiIcon(name = UiIconName.Search, sizeDp = 16, color = UiColorRole.Primary)
            UiSpacer(widthDp = 8)
            Flex(1f) {
                UiTextField(
                    value = state.searchQuery,
                    onValueChange = { state.searchQuery = it },
                    placeholder = "搜索笔记或标签",
                    role = UiTextRole.Label,
                    fillMaxWidth = true
                )
            }
            IconAction(
                icon = UiIconName.Close,
                sizeDp = 13,
                color = UiColorRole.TextTertiary,
                hitBoxDp = 20,
                onClick = { state.searchQuery = "" }
            )
        }

        UiSpacer(heightDp = 18)
        SectionHeader(title = "最近搜索", action = "清除", onAction = { state.searchQuery = "" })
        UiSpacer(heightDp = 10)
        UiRow(spacingDp = 8, align = UiAlign.Center) {
            recentSearches.forEach { keyword ->
                SearchChip(
                    text = keyword,
                    highlighted = keyword == state.searchQuery,
                    onClick = { state.searchQuery = keyword }
                )
            }
        }

        UiSpacer(heightDp = 20)
        SectionHeader(title = "按标签浏览", action = "全部 12 个", onAction = {})
        UiSpacer(heightDp = 10)
        UiRow(spacingDp = 8, align = UiAlign.Center) {
            browsableTags.forEach { tag ->
                SearchChip(text = tag, highlighted = false, onClick = { state.searchQuery = tag })
            }
        }

        UiSpacer(heightDp = 22)
        UiText(
            text = "搜索结果 · ${results.size} 篇",
            role = UiTextRole.Caption,
            color = UiColorRole.TextTertiary
        )
        UiSpacer(heightDp = 12)

        results.forEach { note ->
            NoteCard(
                note = note,
                selected = false,
                titleRole = UiTextRole.Subheading,
                onClick = { state.openEditor(note.id) }
            )
            UiSpacer(heightDp = 12)
        }

        if (results.isEmpty()) {
            UiText(
                text = "没有匹配的笔记，换个关键词试试",
                role = UiTextRole.Label,
                color = UiColorRole.TextTertiary
            )
            UiSpacer(heightDp = 12)
        }

        UiSpacer(heightDp = 24)
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    UiRow(
        fillMaxWidth = true,
        justify = UiJustify.SpaceBetween,
        align = UiAlign.Center
    ) {
        UiText(text = title, role = UiTextRole.Caption, color = UiColorRole.TextSecondary)
        UiBox(onClick = onAction) {
            UiText(text = action, role = UiTextRole.Micro, color = UiColorRole.Primary)
        }
    }
}

@Composable
private fun SearchChip(text: String, highlighted: Boolean, onClick: () -> Unit) {
    UiBox(
        heightDp = 32,
        paddingHorizontalDp = 14,
        cornerRadiusDp = 16,
        background = if (highlighted) UiColorRole.PrimarySoft else UiColorRole.SurfaceTag,
        align = UiAlign.Center,
        onClick = onClick
    ) {
        UiText(
            text = text,
            role = UiTextRole.Label,
            color = if (highlighted) UiColorRole.Primary else UiColorRole.TextSecondary
        )
    }
}
