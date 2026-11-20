import 'dart:math' as math;

import 'package:material_ui/material_ui.dart';

import '../../data/note.dart';
import '../../data/note_repository.dart';
import '../../theme/app_theme.dart';
import '../../ui/components/components.dart';
import 'editor_session.dart';
import 'widgets/editor_toolbar.dart';
import 'widgets/markdown_preview.dart';

/// 笔记编辑页：源码编辑与预览共用一块区域，保存状态显示在底部。
class NoteEditorView extends StatefulWidget {
  const NoteEditorView({
    super.key,
    required this.note,
    required this.repository,
    required this.onBack,
    this.onSaved,
  });

  final Note note;
  final NoteRepository repository;
  final VoidCallback onBack;
  final ValueChanged<Note>? onSaved;

  @override
  State<NoteEditorView> createState() => _NoteEditorViewState();
}

class _NoteEditorViewState extends State<NoteEditorView> {
  late final EditorSession _session;
  final UndoHistoryController _undo = UndoHistoryController();
  final FocusNode _bodyFocus = FocusNode();
  EditorMode _mode = EditorMode.edit;

  TextEditingController get _content => _session.contentController;

  @override
  void initState() {
    super.initState();
    _session = EditorSession(
      repository: widget.repository,
      note: widget.note,
      onSaved: widget.onSaved,
    );
  }

  @override
  void dispose() {
    _undo.dispose();
    _bodyFocus.dispose();
    _session.dispose();
    super.dispose();
  }

  void _handleModeChanged(EditorMode mode) {
    if (mode == _mode) return;
    if (mode == EditorMode.preview) {
      // 切到预览前先把改动落盘，避免预览与数据库不一致。
      _session.save();
    }
    setState(() => _mode = mode);
  }

  void _wrapSelection(String before, String after) {
    final value = _content.value;
    final selection = value.selection;
    final start = selection.isValid ? selection.start : value.text.length;
    final end = selection.isValid ? selection.end : value.text.length;
    final selected = value.text.substring(start, end);
    final replacement = '$before$selected$after';

    _content.value = TextEditingValue(
      text: value.text.replaceRange(start, end, replacement),
      selection: TextSelection(
        baseOffset: start + before.length,
        extentOffset: start + before.length + selected.length,
      ),
    );
    _bodyFocus.requestFocus();
  }

  void _toggleHeading(int level) {
    final value = _content.value;
    final offset = value.selection.isValid
        ? value.selection.start
        : value.text.length;
    final lineStart = value.text.substring(0, offset).lastIndexOf('\n') + 1;
    final nextBreak = value.text.indexOf('\n', offset);
    final lineEnd = nextBreak < 0 ? value.text.length : nextBreak;
    final line = value.text.substring(lineStart, lineEnd);

    final existing = RegExp(r'^(#{1,6})\s+').firstMatch(line);
    final body = existing == null ? line : line.substring(existing.end);
    final prefix = '#' * level;

    final newLine = existing != null && existing.group(1)!.length == level
        ? body
        : '$prefix $body';

    _content.value = TextEditingValue(
      text: value.text.replaceRange(lineStart, lineEnd, newLine),
      selection: TextSelection.collapsed(
        offset: math.min(lineStart + newLine.length, lineStart + newLine.length),
      ),
    );
    _bodyFocus.requestFocus();
  }

  Future<void> _handleBack() async {
    await _session.save();
    if (!mounted) return;
    widget.onBack();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(AppRadius.card),
      ),
      clipBehavior: Clip.antiAlias,
      child: Column(
        children: [
          ValueListenableBuilder<UndoHistoryValue>(
            valueListenable: _undo,
            builder: (context, history, _) {
              return EditorToolbar(
                mode: _mode,
                onModeChanged: _handleModeChanged,
                onBack: _handleBack,
                onUndo: _undo.undo,
                onRedo: _undo.redo,
                canUndo: history.canUndo,
                canRedo: history.canRedo,
                onHeading: _toggleHeading,
                onBold: () => _wrapSelection('**', '**'),
                onItalic: () => _wrapSelection('*', '*'),
                onStrike: () => _wrapSelection('~~', '~~'),
                onLink: () => _wrapSelection('[', '](https://)'),
              );
            },
          ),
          Container(height: 1, color: AppColors.line),
          Expanded(
            child: _mode == EditorMode.edit
                ? _buildEditor()
                : MarkdownPreview(
                    source: _content.text,
                    padding: const EdgeInsets.fromLTRB(40, 24, 40, 40),
                    header: _buildPreviewHeader(),
                  ),
          ),
          Container(height: 1, color: AppColors.line),
          _buildStatusBar(),
        ],
      ),
    );
  }

  Widget _buildEditor() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(40, 24, 40, 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          TextField(
            key: const Key('editor-title'),
            controller: _session.titleController,
            cursorColor: AppColors.primary,
            style: AppText.pageTitle.copyWith(fontWeight: FontWeight.w700),
            decoration: const InputDecoration(
              hintText: '无标题',
              hintStyle: TextStyle(
                fontSize: 26,
                height: 1.35,
                fontWeight: FontWeight.w700,
                color: AppColors.railDisabled,
              ),
              border: InputBorder.none,
              isDense: true,
              contentPadding: EdgeInsets.zero,
            ),
          ),
          const SizedBox(height: 6),
          Text(
            '${_updatedLabel(_session.note.updatedAt)}更新',
            style: AppText.meta,
          ),
          const SizedBox(height: 18),
          Expanded(
            child: TextField(
              key: const Key('editor-body'),
              controller: _content,
              focusNode: _bodyFocus,
              undoController: _undo,
              expands: true,
              maxLines: null,
              minLines: null,
              textAlignVertical: TextAlignVertical.top,
              cursorColor: AppColors.primary,
              style: const TextStyle(
                fontSize: 13,
                height: 1.8,
                color: AppColors.text,
                fontFamily: 'Consolas',
                fontFamilyFallback: ['Menlo', 'monospace'],
              ),
              decoration: const InputDecoration(
                hintText: '开始写点什么…',
                hintStyle: TextStyle(fontSize: 13, color: AppColors.railDisabled),
                border: InputBorder.none,
                isDense: true,
                contentPadding: EdgeInsets.zero,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPreviewHeader() {
    final title = _session.titleController.text.trim();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text(
          title.isEmpty ? '无标题' : title,
          style: AppText.pageTitle.copyWith(fontWeight: FontWeight.w700),
        ),
        const SizedBox(height: 6),
        Text('${_updatedLabel(_session.note.updatedAt)}更新', style: AppText.meta),
        const SizedBox(height: 18),
      ],
    );
  }

  Widget _buildStatusBar() {
    return SizedBox(
      height: 40,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: AnimatedBuilder(
          animation: _session,
          builder: (context, _) {
            return Row(
              children: [
                SijiStatusPill(
                  status: switch (_session.state) {
                    EditorSaveState.unsaved => SijiSaveStatus.unsaved,
                    EditorSaveState.saving => SijiSaveStatus.saving,
                    EditorSaveState.saved => SijiSaveStatus.saved,
                  },
                  label: _statusLabel,
                ),
                const SizedBox(width: 8),
                Text('· ${_session.wordCount} 字', style: AppText.meta),
                const Spacer(),
                const Text('Markdown', style: AppText.meta),
              ],
            );
          },
        ),
      ),
    );
  }

  String get _statusLabel {
    switch (_session.state) {
      case EditorSaveState.unsaved:
        return '未保存';
      case EditorSaveState.saving:
        return '正在保存…';
      case EditorSaveState.saved:
        final savedAt = _session.savedAt;
        return savedAt == null ? '已存本地' : '已存本地 · 刚刚';
    }
  }
}

String _updatedLabel(DateTime time) {
  final diff = DateTime.now().difference(time);
  if (diff.inMinutes < 1) return '刚刚';
  if (diff.inMinutes < 60) return '${diff.inMinutes} 分钟前';
  if (diff.inHours < 24) return '${diff.inHours} 小时前';
  if (diff.inDays <= 1) return '昨天';
  return '${diff.inDays} 天前';
}
