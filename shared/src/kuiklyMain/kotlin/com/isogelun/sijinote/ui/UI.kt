package com.isogelun.sijinote.ui

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp

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
