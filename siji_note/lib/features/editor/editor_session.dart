import 'dart:async';

import 'package:material_ui/material_ui.dart';

import '../../data/note.dart';
import '../../data/note_repository.dart';
import '../../utils/word_count.dart';

enum EditorSaveState { unsaved, saving, saved }

/// 编辑会话：持有标题与正文两个控制器，负责防抖保存、状态与字数。
///
/// 不依赖任何 UI，方便单独测试。
class EditorSession extends ChangeNotifier {
  EditorSession({
    required this.repository,
    required Note note,
    this.debounce = const Duration(milliseconds: 800),
    this.onSaved,
  })  : _note = note,
        titleController = TextEditingController(text: note.title),
        contentController = TextEditingController(text: note.content) {
    _wordCount = countWords(note.content);
    titleController.addListener(_handleChanged);
    contentController.addListener(_handleChanged);
  }

  final NoteRepository repository;
  final Duration debounce;
  final ValueChanged<Note>? onSaved;

  final TextEditingController titleController;
  final TextEditingController contentController;

  Note _note;
  Timer? _debounceTimer;
  EditorSaveState _state = EditorSaveState.saved;
  DateTime? _savedAt;
  int _wordCount = 0;
  bool _dirty = false;
  bool _saving = false;
  bool _disposed = false;
  bool _dirtyDuringSave = false;

  Note get note => _note;
  EditorSaveState get state => _state;
  DateTime? get savedAt => _savedAt;
  int get wordCount => _wordCount;

  void _handleChanged() {
    final words = countWords(contentController.text);
    final changed = words != _wordCount ||
        titleController.text != _note.title ||
        contentController.text != _note.content;
    _wordCount = words;
    if (!changed) return;

    _dirty = true;
    if (_saving) {
      _dirtyDuringSave = true;
    }
    _state = EditorSaveState.unsaved;
    _debounceTimer?.cancel();
    _debounceTimer = Timer(debounce, () => unawaited(save()));
    notifyListeners();
  }

  Future<void> save() async {
    _debounceTimer?.cancel();
    if (!_dirty || _saving) return;

    _saving = true;
    _state = EditorSaveState.saving;
    notifyListeners();

    // 先同步取快照，避免保存期间继续输入被一起写进旧版本。
    final pending = _note.copyWith(
      title: titleController.text,
      content: contentController.text,
      updatedAt: DateTime.now(),
    );
    _dirty = false;
    _dirtyDuringSave = false;

    try {
      await repository.saveNote(pending);
      _note = pending;
      _savedAt = DateTime.now();
      _state = EditorSaveState.saved;
      onSaved?.call(pending);
    } catch (_) {
      _dirty = true;
      _state = EditorSaveState.unsaved;
    } finally {
      _saving = false;
      if (!_disposed) notifyListeners();
      if (_dirtyDuringSave && !_disposed) {
        _debounceTimer?.cancel();
        _debounceTimer = Timer(debounce, () => unawaited(save()));
      }
    }
  }

  @override
  void dispose() {
    _debounceTimer?.cancel();
    // 页面关闭前兜底保存一次；save 会先同步取快照再写库。
    if (_dirty && !_saving) {
      unawaited(save());
    }
    titleController
      ..removeListener(_handleChanged)
      ..dispose();
    contentController
      ..removeListener(_handleChanged)
      ..dispose();
    _disposed = true;
    super.dispose();
  }
}
