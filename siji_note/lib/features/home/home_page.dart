import 'package:material_ui/material_ui.dart';

import '../../data/note.dart';
import '../../data/note_repository.dart';
import '../../theme/app_theme.dart';
import '../../ui/components/components.dart';
import '../editor/note_editor_view.dart';
import 'home_data.dart';
import 'widgets/library_home_view.dart';
import 'widgets/library_panel.dart';
import 'widgets/nav_rail.dart';

/// 思迹笔记主界面：图标栏 + 目录栏 + 知识库首页。
///
/// 窄于 1024 时收起目录栏，只保留图标栏与内容区。
class HomePage extends StatefulWidget {
  const HomePage({super.key, required this.repository});

  final NoteRepository repository;

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  static const _destinations = <RailDestination>[
    RailDestination(icon: SijiIcons.library, label: '知识库'),
    RailDestination(icon: SijiIcons.star, label: '收藏'),
    RailDestination(
      icon: SijiIcons.storage,
      label: '存储',
      enabled: false,
      disabledHint: '即将上线',
    ),
    RailDestination(
      icon: SijiIcons.trash,
      label: '回收站',
      badgeCount: HomeData.trashCount,
    ),
  ];

  List<Note> _notes = const [];
  String? _selectedNoteId;
  int _selectedSection = 0;

  @override
  void initState() {
    super.initState();
    _reload();
  }

  Future<void> _reload() async {
    final notes = await widget.repository.listNotes();
    if (!mounted) return;
    setState(() {
      _notes = notes;
      if (_selectedNoteId != null &&
          !notes.any((note) => note.id == _selectedNoteId)) {
        _selectedNoteId = null;
      }
    });
  }

  Future<void> _createNote() async {
    final note = await widget.repository.createNote();
    final notes = await widget.repository.listNotes();
    if (!mounted) return;
    setState(() {
      _notes = notes;
      _selectedNoteId = note.id;
      _selectedSection = 0;
    });
  }

  int? get _selectedNoteIndex {
    if (_selectedNoteId == null) return null;
    final index = _notes.indexWhere((note) => note.id == _selectedNoteId);
    return index < 0 ? null : index;
  }

  Note? get _editingNote {
    for (final note in _notes) {
      if (note.id == _selectedNoteId) return note;
    }
    return null;
  }

  void _handleSaved(Note saved) {
    final next = [
      for (final note in _notes) note.id == saved.id ? saved : note,
    ]..sort((a, b) => b.updatedAt.compareTo(a.updatedAt));
    setState(() => _notes = next);
  }

  String get _latestUpdatedLabel {
    if (_notes.isEmpty) return '暂无';
    final latest = _notes
        .map((note) => note.updatedAt)
        .reduce((a, b) => a.isAfter(b) ? a : b);
    return _relativeLabel(latest);
  }

  @override
  Widget build(BuildContext context) {
    final items = [
      for (final note in _notes)
        NoteItem(
          title: note.title.isEmpty ? '无标题' : note.title,
          updatedLabel: _relativeLabel(note.updatedAt),
          folder: HomeData.folderName,
        ),
    ];
    final folders = [
      NoteFolder(name: HomeData.folderName, noteCount: _notes.length),
    ];
    final library = HomeData.library.copyWith(
      folderCount: folders.length,
      noteCount: _notes.length,
      updatedLabel: _latestUpdatedLabel,
    );

    return Scaffold(
      backgroundColor: AppColors.shell,
      body: Padding(
        padding: const EdgeInsets.all(10),
        child: LayoutBuilder(
          builder: (context, constraints) {
            final showLibraryPanel = constraints.maxWidth + 20 >= 1024;

            return Row(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                NavRail(
                  destinations: _destinations,
                  selectedIndex: 0,
                  onSelected: (_) {},
                  userInitial: HomeData.userInitial,
                  onSettings: () {},
                ),
                const SizedBox(width: 10),
                if (showLibraryPanel) ...[
                  LibraryPanel(
                    library: library,
                    sections: HomeData.sections,
                    selectedSection: _selectedSection,
                    onSectionSelected: (index) {
                      setState(() => _selectedSection = index);
                    },
                    onBack: () {},
                    onSwitchLibrary: () {},
                    notes: items,
                    selectedNoteIndex: _selectedNoteIndex,
                    onNoteSelected: (index) {
                      setState(() => _selectedNoteId = _notes[index].id);
                    },
                    onNewNote: _createNote,
                    onNewFolder: () {},
                  ),
                  const SizedBox(width: 10),
                ],
                Expanded(
                  child: _editingNote == null
                      ? LibraryHomeView(
                          library: library,
                          folders: folders,
                          onBack: () {},
                          onChangeCover: () {},
                          notes: items,
                          selectedNoteIndex: _selectedNoteIndex,
                          onNoteSelected: (index) {
                            setState(() => _selectedNoteId = _notes[index].id);
                          },
                          onNewNote: _createNote,
                          onNewFolder: () {},
                        )
                      : NoteEditorView(
                          key: ValueKey(_editingNote!.id),
                          note: _editingNote!,
                          repository: widget.repository,
                          onBack: () {
                            setState(() => _selectedNoteId = null);
                          },
                          onSaved: _handleSaved,
                        ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}

String _relativeLabel(DateTime time) {
  final diff = DateTime.now().difference(time);
  if (diff.inMinutes < 1) return '刚刚';
  if (diff.inMinutes < 60) return '${diff.inMinutes} 分钟前';
  if (diff.inHours < 24) return '${diff.inHours} 小时前';
  if (diff.inDays <= 1) return '昨天';
  if (diff.inDays < 30) return '${diff.inDays} 天前';
  return '${time.month} 月 ${time.day} 日';
}
