import 'note.dart';
import 'note_repository.dart';

/// 内存仓储，只用于测试和静态演示，不落盘。
class MemoryNoteRepository implements NoteRepository {
  MemoryNoteRepository({List<Note>? initial})
      : _notes = {
          for (final note in initial ?? const <Note>[]) note.id: note,
        };

  final Map<String, Note> _notes;

  @override
  Future<List<Note>> listNotes() async {
    final notes = _notes.values.toList()
      ..sort((a, b) => b.updatedAt.compareTo(a.updatedAt));
    return notes;
  }

  @override
  Future<Note?> findNote(String id) async => _notes[id];

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
    _notes[note.id] = note;
    return note;
  }

  @override
  Future<void> saveNote(Note note) async {
    _notes[note.id] = note;
  }

  @override
  Future<void> deleteNote(String id) async {
    _notes.remove(id);
  }
}
