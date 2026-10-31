package com.isogelun.sijinote.app.route

/** 公共页面标识；返回栈与页面切换事件后续在公共层接入。 */
sealed interface AppRoute {
    data object Notes : AppRoute

    /** null 表示新建，非 null 表示打开已有笔记。 */
    data class Editor(val noteId: String?) : AppRoute

    data object Settings : AppRoute
}
