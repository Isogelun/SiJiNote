package com.isogelun.sijinote.app

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.ui.UiColumn
import com.isogelun.sijinote.ui.UiText

@Composable
fun IndexScreen() {
    UiColumn {
        UiText("四季笔记")
        UiText("我的笔记")
        UiText("还没有笔记")
    }
}