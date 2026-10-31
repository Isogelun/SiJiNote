package com.isogelun.sijinote.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun runSiJiNoteDesktop() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "思迹笔记",
        state = rememberWindowState(
            width = 1000.dp,
            height = 700.dp
        )
    ) {
        MaterialTheme {
            IndexScreen()
        }
    }
}
