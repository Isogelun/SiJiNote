/// 主界面的静态元信息。
///
/// 笔记内容、数量、更新时间来自本地数据库，只有知识库名称、简介、
/// 文件夹名称这些尚未建表的字段保留在这里。
class NoteItem {
  const NoteItem({
    required this.title,
    required this.updatedLabel,
    required this.folder,
  });

  final String title;
  final String updatedLabel;

  /// 所属文件夹名；未分类笔记为 [HomeData.uncategorized]。
  final String folder;
}

class NoteFolder {
  const NoteFolder({required this.name, required this.noteCount});

  final String name;
  final int noteCount;
}

class LibraryInfo {
  const LibraryInfo({
    required this.name,
    required this.intro,
    required this.badge,
    required this.folderCount,
    required this.noteCount,
    required this.updatedLabel,
  });

  final String name;
  final String intro;
  final String badge;
  final int folderCount;
  final int noteCount;
  final String updatedLabel;

  LibraryInfo copyWith({
    int? folderCount,
    int? noteCount,
    String? updatedLabel,
  }) {
    return LibraryInfo(
      name: name,
      intro: intro,
      badge: badge,
      folderCount: folderCount ?? this.folderCount,
      noteCount: noteCount ?? this.noteCount,
      updatedLabel: updatedLabel ?? this.updatedLabel,
    );
  }
}

abstract final class HomeData {
  static const uncategorized = '未分类';

  /// 一期文件夹只有一层，先固定一个引导文件夹。
  static const folderName = '上手指南';

  static const library = LibraryInfo(
    name: '我的第一个知识库',
    intro: '从「上手指南」文件夹开始：了解笔记、Markdown 与批注。',
    badge: '引导库',
    folderCount: 1,
    noteCount: 0,
    updatedLabel: '暂无',
  );

  /// 目录栏分区：各文件夹 + 未分类。
  static const sections = <String>[folderName, uncategorized];

  static const trashCount = 5;

  static const userInitial = '林';
}
