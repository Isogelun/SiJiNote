import 'dart:math';

import 'note.dart';

/// 本地笔记仓储。
///
/// 页面只依赖这个接口：正式运行用 SQLite 实现，测试用内存实现。
abstract interface class NoteRepository {
  /// 按最近更新时间倒序返回全部笔记。
  Future<List<Note>> listNotes();

  Future<Note?> findNote(String id);

  Future<Note> createNote({String title = '无标题', String content = ''});

  Future<void> saveNote(Note note);

  Future<void> deleteNote(String id);
}

/// 生成不会与已有数据冲突的本地 id。
String newNoteId() {
  final now = DateTime.now().microsecondsSinceEpoch.toRadixString(36);
  final salt = Random().nextInt(1 << 24).toRadixString(36);
  return 'note-$now-$salt';
}
