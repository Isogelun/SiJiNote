package com.isogelun.sijinote.app

import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page

@Page("index", supportInLocal = true)
internal class Kuikly : ComposeContainer() {
    override fun willInit() {
        super.willInit()
        setContent {
            IndexScreen()
        }
    }
}
