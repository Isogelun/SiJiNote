package com.isogelun.sijinote.app.route

/** 桌面左栏与移动端底部导航的分区标识。 */
enum class AppSection(val label: String) {
    Library("知识库"),
    Notes("笔记"),
    Favorites("收藏"),
    Search("搜索"),
    Settings("设置")
}

/**
 * 移动端页面标识：窄屏一次只显示一个页面。
 *
 * 宽屏不使用这里的分支，直接由左栏分区加选中笔记组合出三栏布局；
 * 两种形态共用同一份业务状态，跨阈值切换不会丢失草稿。
 */
sealed interface AppRoute {
    data object Notes : AppRoute
    data object Favorites : AppRoute
    data object Search : AppRoute
    data object Settings : AppRoute

    /** null 表示新建，非 null 表示打开已有笔记。 */
    data class Editor(val noteId: String?) : AppRoute
}
