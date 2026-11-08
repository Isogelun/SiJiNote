package com.isogelun.sijinote.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 官方 Compose（桌面）侧的中立组件实现。 */

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
    if (scrollable) modifier = modifier.verticalScroll(rememberScrollState())

    Column(
        modifier = modifier,
        verticalArrangement = if (spacingDp > 0) {
            Arrangement.spacedBy(spacingDp.dp, verticalJustify(justify))
        } else {
            columnArrangement(justify)
        },
        horizontalAlignment = horizontalAlignment(align)
    ) {
        val columnScope = this
        content(object : UiColumnScope {
            @Composable
            override fun Flex(weight: Float, content: @Composable () -> Unit) {
                with(columnScope) {
                    Box(modifier = Modifier.weight(weight).fillMaxWidth()) { content() }
                }
            }
        })
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
                with(rowScope) {
                    Box(modifier = Modifier.weight(weight).fillMaxHeight()) { content() }
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
