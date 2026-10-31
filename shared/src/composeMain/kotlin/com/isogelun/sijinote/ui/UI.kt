package com.isogelun.sijinote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun UiText(text: String) {
    Text(text)
}

@Composable
actual fun UiColumn(
    paddingDp: Int,
    spacingDp: Int,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(paddingDp.dp),
        verticalArrangement = Arrangement.spacedBy(spacingDp.dp)
    ) {
        content()
    }
}
