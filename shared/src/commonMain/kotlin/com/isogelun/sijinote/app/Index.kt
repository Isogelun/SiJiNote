package com.isogelun.sijinote.app

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.shell.DesktopShell
import com.isogelun.sijinote.app.shell.MobileShell
import com.isogelun.sijinote.ui.UiAdaptiveLayout

/**
 * 应用根界面。
 *
 * 按内容容器宽度在中立布局之间切换：宽屏为三栏工作区，窄屏为单栏加底部导航。
 * 两种形态共享同一个 [AppState]，跨断点时选中笔记与草稿保持一致。
 */
@Composable
fun IndexScreen() {
    val state = rememberAppState()

    UiAdaptiveLayout(breakpointDp = 900) { isWide ->
        if (isWide) {
            DesktopShell(state)
        } else {
            MobileShell(state)
        }
    }
}
