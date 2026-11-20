import 'note.dart';

/// 首次启动写入的引导笔记，内容取自设计稿，不是占位文案。
List<Note> buildSeedNotes({DateTime? now}) {
  final base = now ?? DateTime.now();

  return [
    Note(
      id: 'note-welcome',
      title: '欢迎使用思迹笔记',
      content: '思迹笔记把「记录」和「找回」当作一件事的两面：写时不打断你，'
          '找时不让内容消失。\n\n'
          '> 把已确认的结论沉淀下来，未确认的留在批注里。\n\n'
          '所以一期只做三件事：能写、能找、能回头改。多余的入口一律先不做。',
      createdAt: base.subtract(const Duration(days: 1)),
      updatedAt: base.subtract(const Duration(hours: 2)),
    ),
    Note(
      id: 'note-markdown',
      title: 'Markdown 速查',
      content: '# 常用语法\n\n'
          '- **加粗**：`**文字**`\n'
          '- *斜体*：`*文字*`\n'
          '- ~~删除线~~：`~~文字~~`\n\n'
          '> 引用用 `>` 开头，适合放结论。',
      createdAt: base.subtract(const Duration(days: 3)),
      updatedAt: base.subtract(const Duration(days: 1)),
    ),
    Note(
      id: 'note-annotation',
      title: '批注怎么用',
      content: '批注只针对单段内容，最多 500 字，并且只有自己可见。\n\n'
          '选中一段文字后添加批注；改稿之后如果原文位置变化，批注会跟着重新定位。',
      createdAt: base.subtract(const Duration(days: 5)),
      updatedAt: base.subtract(const Duration(days: 3)),
    ),
  ];
}
