package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable

@Composable
expect fun UiText(text: String)

@Composable
expect fun UiColumn(
    paddingDp: Int = 16,
    spacingDp: Int = 12,
    content: @Composable () -> Unit
)
