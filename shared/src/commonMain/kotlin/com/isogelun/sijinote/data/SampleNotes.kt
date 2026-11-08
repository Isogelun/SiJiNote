package com.isogelun.sijinote.data

import com.isogelun.sijinote.model.Note
import com.isogelun.sijinote.model.NoteBlock
import com.isogelun.sijinote.model.NoteKind

/**
 * 设计稿对应的演示数据。
 *
 * 目前只在内存中存在，重启后回到初始状态；正式存储、加密与同步仍由 [NoteRepository] 的
 * 后续实现负责，不以演示数据代替数据安全能力。
 */
fun sampleNotes(): List<Note> = listOf(
    Note(
        id = "note-plan",
        title = "2025 产品规划 · 第三季度",
        content = "本季度我们围绕「让知识更容易被找到」这一条主线推进，把散落在对话、文档与收藏里的内容重新组织起来。",
        tag = "产品",
        updatedLabel = "10:24",
        updatedAt = "8 月 12 日 10:24",
        kind = NoteKind.Doc,
        favorite = true,
        blocks = listOf(
            NoteBlock.Paragraph(
                "本季度我们围绕「让知识更容易被找到」这一条主线推进，把散落在对话、文档与收藏里的内容重新组织起来，让每一次记录都能自然沉淀为可复用的资产。"
            ),
            NoteBlock.Heading("本季度核心目标"),
            NoteBlock.Todo("统一侧边栏与标签体系，降低 30% 的查找路径", done = true),
            NoteBlock.Todo("上线编辑器专注模式与设置面板的完整交互", done = true),
            NoteBlock.Todo("建立跨端同步状态反馈，让保存与协作可见", done = true),
            NoteBlock.Quote(
                "设计的目标不是让界面更热闹，而是让人更容易进入专注——留白、层级与克制的强调色，比任何装饰都重要。"
            ),
            NoteBlock.Heading("关键结果与排期"),
            NoteBlock.Paragraph(
                "七月完成信息架构评审，八月进入视觉细化与组件搭建，九月进行灰度验证。每个阶段以可用性测试作为出口标准，确保改动真正解决查找与记录的问题。"
            )
        )
    ),
    Note(
        id = "note-review",
        title = "设计评审纪要 · 侧边栏改版",
        content = "确认三栏结构、标签色板与空状态文案，下周进入视觉细化。",
        tag = "设计",
        updatedLabel = "09:12",
        kind = NoteKind.Note,
        blocks = listOf(
            NoteBlock.Paragraph("确认三栏结构、标签色板与空状态文案，下周进入视觉细化。"),
            NoteBlock.Paragraph("导航栏固定 64 宽，只保留图标与短标签，避免与二级内容争夺注意力。")
        )
    ),
    Note(
        id = "note-whitespace",
        title = "随手记：一些关于「留白」的想法",
        content = "好的界面不需要填满，呼吸感本身就是信息层级的一部分。",
        tag = "灵感",
        updatedLabel = "昨天",
        kind = NoteKind.Note,
        favorite = true,
        blocks = listOf(
            NoteBlock.Paragraph("好的界面不需要填满，呼吸感本身就是信息层级的一部分。"),
            NoteBlock.Quote("删掉一个装饰，通常比再加一个动效更能提升密度感。")
        )
    ),
    Note(
        id = "note-competitor",
        title = "需求池 · 竞品体验拆解",
        content = "把熟悉的东西变得陌生，是重新设计的起点，也是创造力的入口。",
        tag = "调研",
        updatedLabel = "8 月 9 日",
        kind = NoteKind.Excerpt,
        blocks = listOf(
            NoteBlock.Paragraph("把熟悉的东西变得陌生，是重新设计的起点，也是创造力的入口。"),
            NoteBlock.Paragraph("对比了三款产品的检索路径：最短两次点击即可命中目标笔记。")
        )
    ),
    Note(
        id = "note-quick-entry",
        title = "需求池 · 移动端快捷录入",
        content = "信息架构方案已通过评审，视觉细化由设计侧本周内出第一版。",
        tag = "移动端",
        updatedLabel = "8 月 6 日",
        kind = NoteKind.Note,
        blocks = listOf(
            NoteBlock.Paragraph("信息架构方案已通过评审，视觉细化由设计侧本周内出第一版。"),
            NoteBlock.Todo("补齐 390 宽度下的四屏高保真"),
            NoteBlock.Todo("确认底部导航选中态的对比度")
        )
    ),
    Note(
        id = "note-weekly",
        title = "产品设计周会纪要",
        content = "今天和设计、研发同步了 Q4 的整体排期。设计系统 2.0 会先落地颜色与字体令牌。",
        tag = "工作",
        updatedLabel = "10:24",
        updatedAt = "2024 年 10 月 28 日 · 10:24",
        kind = NoteKind.Note,
        pinned = true,
        blocks = listOf(
            NoteBlock.Paragraph(
                "今天和设计、研发同步了 Q4 的整体排期。设计系统 2.0 会先落地颜色与字体令牌，组件库随后分批补齐，确保桌面端与移动端共用同一套基础。"
            ),
            NoteBlock.Paragraph(
                "本周的重点是把图标库首批 200 枚交付，并把命名规范整理成文档，方便后续多人协作时保持一致。移动端适配由我这边出四屏高保真。"
            ),
            NoteBlock.Heading("本周待办"),
            NoteBlock.Todo("交付图标库首批 200 枚", done = true),
            NoteBlock.Todo("整理组件命名规范文档"),
            NoteBlock.Todo("评审移动端四屏设计方案"),
            NoteBlock.Quote("移动端优先，先保证 390 宽度下的信息密度，再回头补足桌面端的细节。"),
            NoteBlock.Paragraph(
                "下周一开走查，重点看列表页与编辑器在窄屏下的层级是否清晰，以及底部导航的选中态是否足够明确。"
            )
        )
    ),
    Note(
        id = "note-books",
        title = "周末读书清单",
        content = "《设计中的设计》《写给大家看的设计书》，外加两本闲书。",
        tag = "生活",
        updatedLabel = "昨天",
        kind = NoteKind.Note,
        blocks = listOf(
            NoteBlock.Paragraph("《设计中的设计》《写给大家看的设计书》，外加两本闲书。"),
            NoteBlock.Todo("读完《设计中的设计》第三章", done = true),
            NoteBlock.Todo("整理一份版式规则清单")
        )
    ),
    Note(
        id = "note-app-idea",
        title = "App 改版灵感碎片",
        content = "底部导航改成胶囊式，选中态用实心强调色，留白再松一点。",
        tag = "灵感",
        updatedLabel = "周三",
        kind = NoteKind.Note,
        favorite = true,
        blocks = listOf(
            NoteBlock.Paragraph("底部导航改成胶囊式，选中态用实心强调色，留白再松一点。"),
            NoteBlock.Paragraph("胶囊内左右各留 20，图标与文字同色，未选中的项只保留 60% 透明度。")
        )
    ),
    Note(
        id = "note-kyoto",
        title = "京都行程 · 四日",
        content = "Day1 祇园白川，Day2 岚山竹林，务必提前订好晚餐。",
        tag = "旅行",
        updatedLabel = "10 月 21 日",
        kind = NoteKind.Note,
        blocks = listOf(
            NoteBlock.Paragraph("Day1 祇园白川，Day2 岚山竹林，务必提前订好晚餐。"),
            NoteBlock.Todo("预约岚山小火车"),
            NoteBlock.Todo("确认民宿入住时间")
        )
    ),
    Note(
        id = "note-design-system",
        title = "设计系统 2.0 排期",
        content = "颜色与字体令牌先行，组件库随后分批补齐。",
        tag = "工作",
        updatedLabel = "8 月 11 日",
        kind = NoteKind.Doc,
        blocks = listOf(
            NoteBlock.Paragraph("颜色与字体令牌先行，组件库随后分批补齐。"),
            NoteBlock.Paragraph("令牌层冻结后，桌面端与移动端各自只做映射，不再新增语义。")
        )
    ),
    Note(
        id = "note-naming",
        title = "组件命名规范 v1",
        content = "统一前缀与层级，覆盖桌面端与移动端共用组件。",
        tag = "工作",
        updatedLabel = "8 月 8 日",
        kind = NoteKind.Doc,
        blocks = listOf(
            NoteBlock.Paragraph("统一前缀与层级，覆盖桌面端与移动端共用组件。"),
            NoteBlock.Paragraph("命名只描述职责，不描述外观；外观通过令牌表达。")
        )
    ),
    Note(
        id = "note-icon-list",
        title = "图标库首批交付清单",
        content = "200 枚线性图标，统一 1.8 描边与 24 栅格，覆盖导航与编辑器。",
        tag = "工作",
        updatedLabel = "8 月 7 日",
        kind = NoteKind.Excerpt,
        blocks = listOf(
            NoteBlock.Paragraph("200 枚线性图标，统一 1.8 描边与 24 栅格，覆盖导航与编辑器。"),
            NoteBlock.Todo("导出 SVG 与 Symbol 两套", done = true)
        )
    )
)
