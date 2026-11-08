package com.isogelun.sijinote.ui

/**
 * 设计稿（Ardot · 思迹笔记）中的颜色令牌。
 *
 * 公共层只引用角色名，channels 由两端 actual 映射为各自框架的 Color，
 * 避免 `androidx.compose.ui.graphics` 或 `com.tencent.kuikly.*` 类型进入 commonMain。
 */
enum class UiColorRole(val argb: Long) {
    /** 桌面工作区最外层底色 */
    Background(0xFFE9EBEF),

    /** 左侧导航栏底色 */
    SurfacePanel(0xFFF2F4F7),

    /** 笔记列表底色 */
    SurfaceList(0xFFFAFBFC),

    /** 正文/编辑器底色 */
    Surface(0xFFFFFFFF),

    /** 列表选中卡片底色 */
    SurfaceSelected(0xFFF4F6FC),

    /** 搜索框等输入区域底色 */
    SurfaceField(0xFFF2F4F7),

    /** 标签、徽标底色 */
    SurfaceTag(0xFFEDF0F4),

    /** 品牌主色（导航选中、主按钮） */
    Primary(0xFF35439B),

    /** 主色的浅色底（选中项背景、AI 标记） */
    PrimarySoft(0xFFE8EBF7),

    OnPrimary(0xFFFFFFFF),

    TextPrimary(0xFF16181B),
    TextSecondary(0xFF5A616A),
    TextTertiary(0xFF9AA1AA),

    Divider(0xFFEAEDF1),
    DividerStrong(0xFFE2E6EB),

    /** 深色实心块（品牌标记、头像） */
    Ink(0xFF1B1D22)
}

/** 字重令牌，两端分别映射为各自框架的 FontWeight。 */
enum class UiFontWeight { Normal, Medium, SemiBold, Bold }

/**
 * 文本层级令牌：字号、行高与默认字重、默认颜色集中在这里维护。
 *
 * 页面只表达层级意图（标题、正文、说明），具体字体由两端 actual 决定。
 */
enum class UiTextRole(
    val sizeSp: Int,
    val lineHeightSp: Int,
    val weight: UiFontWeight,
    val defaultColor: UiColorRole
) {
    Display(34, 46, UiFontWeight.Bold, UiColorRole.TextPrimary),
    Title(24, 34, UiFontWeight.SemiBold, UiColorRole.TextPrimary),
    Heading(18, 26, UiFontWeight.SemiBold, UiColorRole.TextPrimary),
    Subheading(16, 24, UiFontWeight.SemiBold, UiColorRole.TextPrimary),
    Body(15, 26, UiFontWeight.Normal, UiColorRole.TextPrimary),
    BodyStrong(15, 26, UiFontWeight.Medium, UiColorRole.TextPrimary),
    Label(13, 20, UiFontWeight.Medium, UiColorRole.TextSecondary),
    Caption(12, 18, UiFontWeight.Normal, UiColorRole.TextTertiary),
    Micro(11, 16, UiFontWeight.Medium, UiColorRole.TextTertiary)
}

/** 图标名称。实际矢量形状见 [iconVector]，两端 actual 只负责按 24x24 网格描边绘制。 */
enum class UiIconName {
    Plus,
    Search,
    Star,
    Gear,
    Layers,
    Note,
    More,
    Share,
    Sort,
    Filter,
    ChevronLeft,
    ChevronRight,
    Check,
    Folder,
    Image,
    Link,
    ListBullet,
    TextH,
    TextB,
    TextI,
    Close,
    User
}

data class UiIconPoint(val x: Float, val y: Float)

/** 折线；closed 为 true 时视为闭合多边形。坐标位于 24x24 设计网格。 */
data class UiIconPath(val points: List<UiIconPoint>, val closed: Boolean = false)

/** 圆形；filled 为 true 时填充，否则描边。 */
data class UiIconCircle(val cx: Float, val cy: Float, val r: Float, val filled: Boolean = false)

data class UiIconVector(
    val paths: List<UiIconPath> = emptyList(),
    val circles: List<UiIconCircle> = emptyList()
)

private fun path(vararg coords: Float, closed: Boolean = false): UiIconPath {
    val points = ArrayList<UiIconPoint>(coords.size / 2)
    var index = 0
    while (index + 1 < coords.size) {
        points.add(UiIconPoint(coords[index], coords[index + 1]))
        index += 2
    }
    return UiIconPath(points, closed)
}

private fun circle(cx: Float, cy: Float, r: Float, filled: Boolean = false) = UiIconCircle(cx, cy, r, filled)

private val gearTeeth: List<UiIconPath> = listOf(
    path(18.3f, 12f, 20.6f, 12f),
    path(16.4f, 16.4f, 18f, 18f),
    path(12f, 18.3f, 12f, 20.6f),
    path(7.6f, 16.4f, 6f, 18f),
    path(5.7f, 12f, 3.4f, 12f),
    path(7.6f, 7.6f, 6f, 6f),
    path(12f, 5.7f, 12f, 3.4f),
    path(16.4f, 7.6f, 18f, 6f)
)

/** 图标矢量定义，集中维护，避免两端各写一份路径。 */
fun iconVector(name: UiIconName): UiIconVector = when (name) {
    UiIconName.Plus -> UiIconVector(
        paths = listOf(path(12f, 5f, 12f, 19f), path(5f, 12f, 19f, 12f))
    )

    UiIconName.Search -> UiIconVector(
        paths = listOf(path(15.2f, 15.2f, 19.5f, 19.5f)),
        circles = listOf(circle(10.5f, 10.5f, 6.5f))
    )

    UiIconName.Star -> UiIconVector(
        paths = listOf(
            path(
                12f, 3.5f,
                14.12f, 9.09f,
                20.09f, 9.37f,
                15.42f, 13.11f,
                17f, 18.88f,
                12f, 15.6f,
                7f, 18.88f,
                8.58f, 13.11f,
                3.91f, 9.37f,
                9.88f, 9.09f,
                closed = true
            )
        )
    )

    UiIconName.Gear -> UiIconVector(
        paths = gearTeeth,
        circles = listOf(circle(12f, 12f, 3.4f))
    )

    UiIconName.Layers -> UiIconVector(
        paths = listOf(
            path(12f, 3.6f, 20f, 8.2f, 12f, 12.8f, 4f, 8.2f, closed = true),
            path(4f, 12.2f, 12f, 16.8f, 20f, 12.2f),
            path(4f, 16f, 12f, 20.6f, 20f, 16f)
        )
    )

    UiIconName.Note -> UiIconVector(
        paths = listOf(
            path(5.5f, 4f, 18.5f, 4f, 18.5f, 20f, 5.5f, 20f, closed = true),
            path(9f, 9f, 15f, 9f),
            path(9f, 13f, 15f, 13f),
            path(9f, 17f, 12.5f, 17f)
        )
    )

    UiIconName.More -> UiIconVector(
        circles = listOf(
            circle(5.5f, 12f, 1.6f, filled = true),
            circle(12f, 12f, 1.6f, filled = true),
            circle(18.5f, 12f, 1.6f, filled = true)
        )
    )

    UiIconName.Share -> UiIconVector(
        paths = listOf(
            path(12f, 15f, 12f, 3.6f),
            path(7.8f, 7.6f, 12f, 3.4f, 16.2f, 7.6f),
            path(6.5f, 12.5f, 6.5f, 20f, 17.5f, 20f, 17.5f, 12.5f)
        )
    )

    UiIconName.Sort -> UiIconVector(
        paths = listOf(
            path(4f, 7f, 20f, 7f),
            path(4f, 12f, 15f, 12f),
            path(4f, 17f, 10f, 17f)
        )
    )

    UiIconName.Filter -> UiIconVector(
        paths = listOf(
            path(3.5f, 5f, 20.5f, 5f, 14f, 12.5f, 14f, 19.5f, 10f, 16.5f, 10f, 12.5f, closed = true)
        )
    )

    UiIconName.ChevronLeft -> UiIconVector(
        paths = listOf(path(15f, 4.5f, 8f, 12f, 15f, 19.5f))
    )

    UiIconName.ChevronRight -> UiIconVector(
        paths = listOf(path(9f, 4.5f, 16f, 12f, 9f, 19.5f))
    )

    UiIconName.Check -> UiIconVector(
        paths = listOf(path(5f, 12.5f, 9.8f, 17.3f, 19f, 6.8f))
    )

    UiIconName.Folder -> UiIconVector(
        paths = listOf(
            path(3f, 6f, 9f, 6f, 11f, 8.6f, 21f, 8.6f, 21f, 19f, 3f, 19f, closed = true)
        )
    )

    UiIconName.Image -> UiIconVector(
        paths = listOf(
            path(4f, 5f, 20f, 5f, 20f, 19f, 4f, 19f, closed = true),
            path(5.2f, 16.6f, 10.5f, 11.2f, 14.2f, 14.9f, 16.4f, 12.7f, 20f, 16.2f)
        ),
        circles = listOf(circle(9f, 9.8f, 1.7f))
    )

    UiIconName.Link -> UiIconVector(
        paths = listOf(path(10.6f, 13.4f, 13.4f, 10.6f)),
        circles = listOf(circle(9f, 15f, 3.4f), circle(15f, 9f, 3.4f))
    )

    UiIconName.ListBullet -> UiIconVector(
        paths = listOf(
            path(9f, 6.5f, 20f, 6.5f),
            path(9f, 12f, 20f, 12f),
            path(9f, 17.5f, 20f, 17.5f)
        ),
        circles = listOf(
            circle(4.6f, 6.5f, 1.4f, filled = true),
            circle(4.6f, 12f, 1.4f, filled = true),
            circle(4.6f, 17.5f, 1.4f, filled = true)
        )
    )

    UiIconName.TextH -> UiIconVector(
        paths = listOf(
            path(5.5f, 6f, 5.5f, 18f),
            path(15.5f, 6f, 15.5f, 18f),
            path(5.5f, 12f, 15.5f, 12f)
        )
    )

    UiIconName.TextB -> UiIconVector(
        paths = listOf(
            path(7f, 5.5f, 13f, 5.5f, 16f, 7f, 16f, 10f, 13f, 11.6f, 7f, 11.6f),
            path(13f, 11.6f, 17f, 13.2f, 17f, 16.8f, 13f, 18.5f, 7f, 18.5f),
            path(7f, 5.5f, 7f, 18.5f)
        )
    )

    UiIconName.TextI -> UiIconVector(
        paths = listOf(
            path(9f, 6f, 15f, 6f),
            path(12f, 6f, 12f, 18f),
            path(9f, 18f, 15f, 18f)
        )
    )

    UiIconName.Close -> UiIconVector(
        paths = listOf(path(6.5f, 6.5f, 17.5f, 17.5f), path(17.5f, 6.5f, 6.5f, 17.5f))
    )

    UiIconName.User -> UiIconVector(
        paths = listOf(path(5.2f, 20f, 5.2f, 17.5f, 8.4f, 14.8f, 15.6f, 14.8f, 18.8f, 17.5f, 18.8f, 20f)),
        circles = listOf(circle(12f, 8.6f, 4f))
    )
}
