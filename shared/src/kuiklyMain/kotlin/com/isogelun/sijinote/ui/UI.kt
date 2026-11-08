package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxWithConstraints
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.ColumnScope
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Path
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.graphics.StrokeCap
import com.tencent.kuikly.compose.ui.graphics.StrokeJoin
import com.tencent.kuikly.compose.ui.graphics.drawscope.Fill
import com.tencent.kuikly.compose.ui.graphics.drawscope.Stroke
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp

/**
 * Kuikly 渚х殑涓珛缁勪欢瀹炵幇锛岃鐩?Android / iOS / H5 / 楦胯挋銆? *
 * 涓庢闈㈠疄鐜扮殑宸埆鍙湪鍖呭悕鍜屾粴鍔ㄥ鍣細Kuikly 娌℃湁鍏紑鐨?verticalScroll 淇グ绗︼紝
 * 鍙粴鍔ㄥ垪浣跨敤 LazyColumn 鎵胯浇鍗曚釜鍐呭椤广€? */

private fun uiColor(role: UiColorRole): Color = Color(role.argb.toInt())

private fun uiWeight(weight: UiFontWeight): FontWeight = when (weight) {
    UiFontWeight.Normal -> FontWeight.Normal
    UiFontWeight.Medium -> FontWeight.Medium
    UiFontWeight.SemiBold -> FontWeight.SemiBold
    UiFontWeight.Bold -> FontWeight.Bold
}

private fun Modifier.uiPadding(paddingDp: Int, paddingHorizontalDp: Int, paddingVerticalDp: Int): Modifier {
    val horizontal = if (paddingHorizontalDp >= 0) paddingHorizontalDp else paddingDp
    val vertical = if (paddingVerticalDp >= 0) paddingVerticalDp else paddingDp
    if (horizontal == 0 && vertical == 0) return this
    return padding(horizontal = horizontal.dp, vertical = vertical.dp)
}

private fun Modifier.uiSurface(
    background: UiColorRole?,
    cornerRadiusDp: Int,
    borderColor: UiColorRole?
): Modifier {
    var modifier = this
    val shape = if (cornerRadiusDp > 0) RoundedCornerShape(cornerRadiusDp.dp) else null
    if (background != null) {
        modifier = if (shape != null) {
            modifier.background(uiColor(background), shape)
        } else {
            modifier.background(uiColor(background))
        }
    }
    if (borderColor != null) {
        modifier = if (shape != null) {
            modifier.border(1.dp, uiColor(borderColor), shape)
        } else {
            modifier.border(1.dp, uiColor(borderColor))
        }
    }
    return modifier
}

private fun Modifier.uiClickable(onClick: (() -> Unit)?): Modifier =
    if (onClick != null) clickable { onClick() } else this

private fun columnArrangement(justify: UiJustify): Arrangement.Vertical = when (justify) {
    UiJustify.Start -> Arrangement.Top
    UiJustify.Center -> Arrangement.Center
    UiJustify.End -> Arrangement.Bottom
    UiJustify.SpaceBetween -> Arrangement.SpaceBetween
}

private fun rowArrangement(justify: UiJustify): Arrangement.Horizontal = when (justify) {
    UiJustify.Start -> Arrangement.Start
    UiJustify.Center -> Arrangement.Center
    UiJustify.End -> Arrangement.End
    UiJustify.SpaceBetween -> Arrangement.SpaceBetween
}

/** `Arrangement.spacedBy` 的第二个参数是 Alignment，而不是 Arrangement。 */
private fun verticalJustify(justify: UiJustify): Alignment.Vertical = when (justify) {
    UiJustify.Start -> Alignment.Top
    UiJustify.Center -> Alignment.CenterVertically
    UiJustify.End -> Alignment.Bottom
    UiJustify.SpaceBetween -> Alignment.CenterVertically
}

private fun horizontalJustify(justify: UiJustify): Alignment.Horizontal = when (justify) {
    UiJustify.Start -> Alignment.Start
    UiJustify.Center -> Alignment.CenterHorizontally
    UiJustify.End -> Alignment.End
    UiJustify.SpaceBetween -> Alignment.CenterHorizontally
}

private fun horizontalAlignment(align: UiAlign): Alignment.Horizontal = when (align) {
    UiAlign.Start -> Alignment.Start
    UiAlign.Center -> Alignment.CenterHorizontally
    UiAlign.End -> Alignment.End
}

private fun verticalAlignment(align: UiAlign): Alignment.Vertical = when (align) {
    UiAlign.Start -> Alignment.Top
    UiAlign.Center -> Alignment.CenterVertically
    UiAlign.End -> Alignment.Bottom
}

private fun boxAlignment(align: UiAlign): Alignment = when (align) {
    UiAlign.Start -> Alignment.TopStart
    UiAlign.Center -> Alignment.Center
    UiAlign.End -> Alignment.BottomEnd
}

/** 鍦ㄧ粰瀹?ColumnScope 涓睍寮€涓珛鍒楀唴瀹癸紝骞朵负 Flex 瀛愰」搴旂敤 weight銆?*/
@Composable
private fun ColumnScope.UiColumnContent(
    content: @Composable UiColumnScope.() -> Unit
) {
    val columnScope = this
    content(object : UiColumnScope {
        @Composable
        override fun Flex(weight: Float, content: @Composable () -> Unit) {
            Box(modifier = with(columnScope) { Modifier.weight(weight) }.fillMaxWidth()) {
                content()
            }
        }
    })
}

@Composable
actual fun UiText(
    text: String,
    role: UiTextRole,
    color: UiColorRole?,
    weight: UiFontWeight?,
    maxLines: Int,
    fillMaxWidth: Boolean
) {
    Text(
        text = text,
        modifier = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier,
        color = uiColor(color ?: role.defaultColor),
        fontSize = role.sizeSp.sp,
        lineHeight = role.lineHeightSp.sp,
        fontWeight = uiWeight(weight ?: role.weight),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
actual fun UiColumn(
    paddingDp: Int,
    paddingHorizontalDp: Int,
    paddingVerticalDp: Int,
    spacingDp: Int,
    widthDp: Int,
    maxWidthDp: Int,
    heightDp: Int,
    fillMaxWidth: Boolean,
    fillMaxHeight: Boolean,
    background: UiColorRole?,
    cornerRadiusDp: Int,
    borderColor: UiColorRole?,
    align: UiAlign,
    justify: UiJustify,
    scrollable: Boolean,
    onClick: (() -> Unit)?,
    content: @Composable UiColumnScope.() -> Unit
) {
    var modifier: Modifier = Modifier
    if (widthDp >= 0) modifier = modifier.width(widthDp.dp)
    if (heightDp >= 0) modifier = modifier.height(heightDp.dp)
    // widthIn 必须先于 fillMaxWidth：先固定父约束再填满，否则上限无法收窄。
    if (maxWidthDp >= 0) modifier = modifier.widthIn(max = maxWidthDp.dp)
    if (fillMaxWidth) modifier = modifier.fillMaxWidth()
    if (fillMaxHeight) modifier = modifier.fillMaxHeight()
    modifier = modifier
        .uiSurface(background, cornerRadiusDp, borderColor)
        .uiPadding(paddingDp, paddingHorizontalDp, paddingVerticalDp)
        .uiClickable(onClick)

    val arrangement = if (spacingDp > 0) {
        Arrangement.spacedBy(spacingDp.dp, verticalJustify(justify))
    } else {
        columnArrangement(justify)
    }
    val alignment = horizontalAlignment(align)

    if (scrollable) {
        LazyColumn(modifier = modifier) {
            item(key = "ui-column-content") {
                Column(
                    verticalArrangement = arrangement,
                    horizontalAlignment = alignment
                ) {
                    UiColumnContent(content)
                }
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = arrangement,
            horizontalAlignment = alignment
        ) {
            UiColumnContent(content)
        }
    }
}

@Composable
actual fun UiRow(
    paddingDp: Int,
    paddingHorizontalDp: Int,
    paddingVerticalDp: Int,
    spacingDp: Int,
    heightDp: Int,
    fillMaxWidth: Boolean,
    fillMaxHeight: Boolean,
    background: UiColorRole?,
    cornerRadiusDp: Int,
    borderColor: UiColorRole?,
    align: UiAlign,
    justify: UiJustify,
    onClick: (() -> Unit)?,
    content: @Composable UiRowScope.() -> Unit
) {
    var modifier: Modifier = Modifier
    if (heightDp >= 0) modifier = modifier.height(heightDp.dp)
    if (fillMaxWidth) modifier = modifier.fillMaxWidth()
    if (fillMaxHeight) modifier = modifier.fillMaxHeight()
    modifier = modifier
        .uiSurface(background, cornerRadiusDp, borderColor)
        .uiPadding(paddingDp, paddingHorizontalDp, paddingVerticalDp)
        .uiClickable(onClick)

    Row(
        modifier = modifier,
        horizontalArrangement = if (spacingDp > 0) {
            Arrangement.spacedBy(spacingDp.dp, horizontalJustify(justify))
        } else {
            rowArrangement(justify)
        },
        verticalAlignment = verticalAlignment(align)
    ) {
        val rowScope = this
        content(object : UiRowScope {
            @Composable
            override fun Flex(weight: Float, content: @Composable () -> Unit) {
                Box(modifier = with(rowScope) { Modifier.weight(weight) }.fillMaxHeight()) {
                    content()
                }
            }
        })
    }
}

@Composable
actual fun UiBox(
    widthDp: Int,
    heightDp: Int,
    paddingDp: Int,
    paddingHorizontalDp: Int,
    paddingVerticalDp: Int,
    fillMaxWidth: Boolean,
    fillMaxHeight: Boolean,
    background: UiColorRole?,
    cornerRadiusDp: Int,
    borderColor: UiColorRole?,
    align: UiAlign,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit
) {
    var modifier: Modifier = Modifier
    if (widthDp >= 0) modifier = modifier.width(widthDp.dp)
    if (heightDp >= 0) modifier = modifier.height(heightDp.dp)
    if (fillMaxWidth) modifier = modifier.fillMaxWidth()
    if (fillMaxHeight) modifier = modifier.fillMaxHeight()
    modifier = modifier
        .uiSurface(background, cornerRadiusDp, borderColor)
        .uiPadding(paddingDp, paddingHorizontalDp, paddingVerticalDp)
        .uiClickable(onClick)

    Box(modifier = modifier, contentAlignment = boxAlignment(align)) {
        content()
    }
}

@Composable
actual fun UiSpacer(widthDp: Int, heightDp: Int) {
    Spacer(modifier = Modifier.width(widthDp.dp).height(heightDp.dp))
}

@Composable
actual fun UiDivider(color: UiColorRole, thicknessDp: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(thicknessDp.dp)
            .background(uiColor(color))
    )
}

@Composable
actual fun UiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    role: UiTextRole,
    color: UiColorRole,
    singleLine: Boolean,
    fillMaxWidth: Boolean
) {
    var modifier: Modifier = Modifier
    if (fillMaxWidth) modifier = modifier.fillMaxWidth()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = TextStyle(
            color = uiColor(color),
            fontSize = role.sizeSp.sp,
            lineHeight = role.lineHeightSp.sp,
            fontWeight = uiWeight(role.weight)
        ),
        singleLine = singleLine,
        cursorBrush = SolidColor(uiColor(UiColorRole.Primary)),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = uiColor(UiColorRole.TextTertiary),
                        fontSize = role.sizeSp.sp,
                        fontWeight = uiWeight(role.weight),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
actual fun UiIcon(name: UiIconName, sizeDp: Int, color: UiColorRole, strokeWidthDp: Float) {
    val vector = iconVector(name)
    val tint = uiColor(color)

    Canvas(modifier = Modifier.size(sizeDp.dp)) {
        val scale = this.size.minDimension / 24f
        val stroke = Stroke(
            width = strokeWidthDp * scale,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        vector.circles.forEach { circle ->
            drawCircle(
                color = tint,
                radius = circle.r * scale,
                center = Offset(circle.cx * scale, circle.cy * scale),
                style = if (circle.filled) Fill else stroke
            )
        }

        vector.paths.forEach { iconPath ->
            if (iconPath.points.size < 2) return@forEach
            val path = Path()
            path.moveTo(iconPath.points[0].x * scale, iconPath.points[0].y * scale)
            for (index in 1 until iconPath.points.size) {
                path.lineTo(iconPath.points[index].x * scale, iconPath.points[index].y * scale)
            }
            if (iconPath.closed) path.close()
            drawPath(path = path, color = tint, style = stroke)
        }
    }
}

@Composable
actual fun UiAdaptiveLayout(breakpointDp: Int, content: @Composable (isWide: Boolean) -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        content(maxWidth.value >= breakpointDp)
    }
}
