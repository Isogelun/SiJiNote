package com.isogelun.sijinote.app

import androidx.compose.runtime.Composable
import com.isogelun.sijinote.app.note.NoteEditorScreen
import com.isogelun.sijinote.app.note.NoteListScreen
import com.isogelun.sijinote.app.route.AppRoute
import com.isogelun.sijinote.app.settings.SettingsScreen

@Composable
fun IndexScreen(route: AppRoute = AppRoute.Notes) {
    when (route) {
        AppRoute.Notes -> NoteListScreen()
        is AppRoute.Editor -> NoteEditorScreen(noteId = route.noteId)
        AppRoute.Settings -> SettingsScreen()
    }
}
