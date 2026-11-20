import 'package:flutter_test/flutter_test.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';
import 'package:siji_note/data/sqlite_note_repository.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  test('SQLite 仓储：建表、种子、保存、删除', () async {
    sqfliteFfiInit();
    final repository = await SqliteNoteRepository.open(
      databasePath: inMemoryDatabasePath,
    );

    final seeded = await repository.listNotes();
    expect(seeded.length, 3);

    final target = seeded.first;
    await repository.saveNote(
      target.copyWith(
        title: '改过的标题',
        content: '改过的正文',
        updatedAt: DateTime.now(),
      ),
    );

    final reloaded = await repository.findNote(target.id);
    expect(reloaded, isNotNull);
    expect(reloaded!.title, '改过的标题');
    expect(reloaded.content, '改过的正文');

    final created = await repository.createNote(title: '新笔记');
    expect((await repository.listNotes()).length, 4);

    await repository.deleteNote(created.id);
    expect((await repository.listNotes()).length, 3);

    await repository.close();
  });
}
