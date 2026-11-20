import 'dart:io';

import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart'
    show databaseFactoryFfi, sqfliteFfiInit;

import 'note.dart';
import 'note_repository.dart';
import 'note_seed.dart';

/// SQLite 本地仓储。
///
/// Windows / macOS / Linux 走 sqflite_common_ffi，Android 走 sqflite 默认实现。
class SqliteNoteRepository implements NoteRepository {
  SqliteNoteRepository._(this._db);

  final Database _db;

  static const _fileName = 'siji_note.db';
  static const _table = 'notes';

  /// [databasePath] 仅供测试传入内存库路径；正式运行传空，使用应用数据目录。
  static Future<SqliteNoteRepository> open({String? databasePath}) async {
    await _ensureDesktopFactory();

    final path = databasePath ?? await _defaultPath();
    final db = await openDatabase(
      path,
      version: 1,
      onCreate: _onCreate,
    );

    final repository = SqliteNoteRepository._(db);
    await repository._seedIfEmpty();
    return repository;
  }

  static Future<String> _defaultPath() async {
    final directory = await getApplicationSupportDirectory();
    return p.join(directory.path, _fileName);
  }

  static Future<void> _ensureDesktopFactory() async {
    if (!Platform.isWindows && !Platform.isLinux && !Platform.isMacOS) {
      return;
    }
    sqfliteFfiInit();
    databaseFactory = databaseFactoryFfi;
  }

  static Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
      CREATE TABLE notes (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        content TEXT NOT NULL,
        created_at INTEGER NOT NULL,
        updated_at INTEGER NOT NULL
      )
    ''');
    await db.execute(
      'CREATE INDEX idx_notes_updated_at ON notes(updated_at DESC)',
    );
  }

  Future<void> _seedIfEmpty() async {
    final count =
        Sqflite.firstIntValue(await _db.rawQuery('SELECT COUNT(*) FROM $_table')) ??
            0;
    if (count > 0) return;

    final batch = _db.batch();
    for (final note in buildSeedNotes()) {
      batch.insert(_table, note.toMap());
    }
    await batch.commit(noResult: true);
  }

  @override
  Future<List<Note>> listNotes() async {
    final rows = await _db.query(_table, orderBy: 'updated_at DESC');
    return rows.map(Note.fromMap).toList();
  }

  @override
  Future<Note?> findNote(String id) async {
    final rows = await _db.query(
      _table,
      where: 'id = ?',
      whereArgs: [id],
      limit: 1,
    );
    if (rows.isEmpty) return null;
    return Note.fromMap(rows.first);
  }

  @override
  Future<Note> createNote({String title = '无标题', String content = ''}) async {
    final now = DateTime.now();
    final note = Note(
      id: newNoteId(),
      title: title,
      content: content,
      createdAt: now,
      updatedAt: now,
    );
    await _db.insert(_table, note.toMap());
    return note;
  }

  @override
  Future<void> saveNote(Note note) async {
    await _db.insert(
      _table,
      note.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  @override
  Future<void> deleteNote(String id) async {
    await _db.delete(_table, where: 'id = ?', whereArgs: [id]);
  }

  Future<void> close() => _db.close();
}
