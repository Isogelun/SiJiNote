import 'package:flutter/foundation.dart';
import 'package:material_ui/material_ui.dart';

import '../../../theme/app_theme.dart';
import '../../../ui/components/components.dart';
import '../home_data.dart';

/// 右侧知识库首页。
class LibraryHomeView extends StatefulWidget {
  const LibraryHomeView({
    super.key,
    required this.library,
    required this.folders,
    required this.notes,
    required this.selectedNoteIndex,
    required this.onNoteSelected,
    required this.onBack,
    required this.onNewNote,
    required this.onNewFolder,
    required this.onChangeCover,
  });

  final LibraryInfo library;
  final List<NoteFolder> folders;
  final List<NoteItem> notes;
  final int? selectedNoteIndex;
  final ValueChanged<int> onNoteSelected;
  final VoidCallback onBack;
  final VoidCallback onNewNote;
  final VoidCallback onNewFolder;
  final VoidCallback onChangeCover;

  @override
  State<LibraryHomeView> createState() => _LibraryHomeViewState();
}

class _LibraryHomeViewState extends State<LibraryHomeView> {
  final Set<String> _collapsed = {};

  @override
  Widget build(BuildContext context) {
    final library = widget.library;
    final notes = widget.notes;

    return Container(
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(AppRadius.card),
      ),
      clipBehavior: Clip.antiAlias,
      child: Material(
        color: Colors.transparent,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _TopBar(libraryName: library.name, onBack: widget.onBack),
            _Cover(onTap: widget.onChangeCover),
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.fromLTRB(40, 26, 40, 40),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Row(
                      children: [
                        Flexible(
                          child: Text(
                            library.name,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: AppText.pageTitle.copyWith(
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                        ),
                        const SizedBox(width: 8),
                        SijiTag(label: library.badge),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(library.intro, style: AppText.body),
                    const SizedBox(height: 10),
                    Text(
                      '${library.noteCount} 篇笔记 · 最近更新 ${library.updatedLabel}',
                      style: AppText.meta,
                    ),
                    const SizedBox(height: 24),
                    Container(height: 1, color: AppColors.line),
                    const SizedBox(height: 18),
                    Row(
                      children: [
                        const Text('文件夹与笔记', style: AppText.sectionTitle),
                        const SizedBox(width: 6),
                        Expanded(
                          child: Text(
                            '${library.folderCount} 个文件夹 · ${library.noteCount} 篇',
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: AppText.meta,
                          ),
                        ),
                        const SizedBox(width: 12),
                        DecoratedBox(
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(
                              AppRadius.control,
                            ),
                            boxShadow: const [
                              BoxShadow(
                                color: Color(0x401F3D38),
                                offset: Offset(0, 2),
                                blurRadius: 6,
                              ),
                            ],
                          ),
                          child: SijiButton(
                            label: '+ 新建笔记',
                            size: SijiButtonSize.small,
                            expand: false,
                            onPressed: widget.onNewNote,
                          ),
                        ),
                        const SizedBox(width: 12),
                        SizedBox(
                          width: 108,
                          child: SijiButton(
                            label: '+ 新建文件夹',
                            variant: SijiButtonVariant.secondary,
                            size: SijiButtonSize.medium,
                            radius: 10,
                            onPressed: widget.onNewFolder,
                          ),
                        ),
                      ],
                    ),
                    for (final folder in widget.folders) ...[
                      _FolderRow(
                        folder: folder,
                        expanded: !_collapsed.contains(folder.name),
                        onTap: () => setState(() {
                          if (!_collapsed.remove(folder.name)) {
                            _collapsed.add(folder.name);
                          }
                        }),
                      ),
                      if (!_collapsed.contains(folder.name))
                        for (var i = 0; i < notes.length; i++)
                          if (notes[i].folder == folder.name)
                            _NoteRow(
                              note: notes[i],
                              indent: 44,
                              selected: i == widget.selectedNoteIndex,
                              onTap: () => widget.onNoteSelected(i),
                            ),
                    ],
                    for (var i = 0; i < notes.length; i++)
                      if (notes[i].folder == HomeData.uncategorized)
                        _NoteRow(
                          note: notes[i],
                          indent: 12,
                          selected: i == widget.selectedNoteIndex,
                          onTap: () => widget.onNoteSelected(i),
                        ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _TopBar extends StatelessWidget {
  const _TopBar({required this.libraryName, required this.onBack});

  final String libraryName;
  final VoidCallback onBack;

  @override
  Widget build(BuildContext context) {
    const crumb = TextStyle(
      fontSize: 13,
      height: 1.4,
      fontWeight: FontWeight.w500,
      color: AppColors.text,
    );

    return SizedBox(
      height: 56,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24),
        child: Row(
          children: [
            const SijiIcon(
              SijiIcons.library,
              size: 16,
              color: AppColors.iconMuted,
            ),
            const SizedBox(width: 8),
            InkWell(
              onTap: onBack,
              borderRadius: BorderRadius.circular(4),
              child: const Text('‹ 知识库总览', style: crumb),
            ),
            const Text(' / ', style: crumb),
            Expanded(
              child: Text(
                libraryName,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
                style: crumb,
              ),
            ),
            const SizedBox(width: 16),
            const _SearchField(),
          ],
        ),
      ),
    );
  }
}

class _SearchField extends StatelessWidget {
  const _SearchField();

  @override
  Widget build(BuildContext context) {
    final shortcut = defaultTargetPlatform == TargetPlatform.macOS
        ? '⌘ K'
        : 'Ctrl K';

    return SizedBox(
      width: 240,
      height: 32,
      child: TextField(
        style: const TextStyle(fontSize: 12, color: AppColors.text),
        cursorHeight: 14,
        decoration: InputDecoration(
          isDense: true,
          hintText: '搜索笔记',
          hintStyle: const TextStyle(fontSize: 11, color: AppColors.subtle),
          prefixIcon: const Padding(
            padding: EdgeInsets.only(left: 10, right: 8),
            child: SijiIcon(
              SijiIcons.search,
              size: 14,
              color: AppColors.iconMuted,
            ),
          ),
          prefixIconConstraints: const BoxConstraints(minWidth: 32),
          suffixIcon: Padding(
            padding: const EdgeInsets.only(right: 7),
            child: Container(
              height: 18,
              padding: const EdgeInsets.symmetric(horizontal: 5),
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: AppColors.surface,
                border: Border.all(color: AppColors.hairline),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                shortcut,
                style: const TextStyle(
                  fontSize: 9,
                  height: 1.2,
                  color: AppColors.subtle,
                ),
              ),
            ),
          ),
          suffixIconConstraints: BoxConstraints(
            maxWidth: defaultTargetPlatform == TargetPlatform.macOS ? 38 : 52,
            maxHeight: 18,
          ),
          contentPadding: const EdgeInsets.symmetric(vertical: 9),
          filled: true,
          fillColor: AppColors.surface,
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.control),
            borderSide: const BorderSide(color: AppColors.searchBorder),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(AppRadius.control),
            borderSide: const BorderSide(color: AppColors.primary),
          ),
        ),
      ),
    );
  }
}

class _Cover extends StatelessWidget {
  const _Cover({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: AppColors.disabled,
      child: InkWell(
        onTap: onTap,
        child: const SizedBox(
          height: 180,
          child: Padding(
            padding: EdgeInsets.fromLTRB(20, 0, 20, 14),
            child: Align(
              alignment: Alignment.bottomRight,
              child: Text(
                '封面 · 点击更换',
                style: TextStyle(
                  fontSize: 10,
                  height: 1.4,
                  color: AppColors.subtle,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _FolderRow extends StatelessWidget {
  const _FolderRow({
    required this.folder,
    required this.expanded,
    required this.onTap,
  });

  final NoteFolder folder;
  final bool expanded;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final radius = BorderRadius.circular(AppRadius.control);
    return Material(
      color: AppColors.bg,
      borderRadius: radius,
      child: InkWell(
        onTap: onTap,
        borderRadius: radius,
        child: SizedBox(
          height: 48,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12),
            child: Row(
              children: [
                const SijiIcon(SijiIcons.folder, color: AppColors.muted),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    folder.name,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontSize: 14,
                      height: 1.4,
                      color: AppColors.text,
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Text(
                  '${folder.noteCount} 篇笔记',
                  style: AppText.caption.copyWith(color: AppColors.muted),
                ),
                const SizedBox(width: 10),
                AnimatedRotation(
                  turns: expanded ? 0 : -0.25,
                  duration: const Duration(milliseconds: 150),
                  child: const SijiIcon(
                    SijiIcons.chevronDown,
                    color: AppColors.muted,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _NoteRow extends StatelessWidget {
  const _NoteRow({
    required this.note,
    required this.indent,
    required this.selected,
    required this.onTap,
  });

  final NoteItem note;
  final double indent;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final radius = BorderRadius.circular(6);
    return Material(
      color: selected ? AppColors.selected : Colors.transparent,
      borderRadius: radius,
      child: InkWell(
        onTap: onTap,
        borderRadius: radius,
        child: SizedBox(
          height: 46,
          child: Padding(
            padding: EdgeInsets.only(left: indent, right: 10),
            child: Row(
              children: [
                SijiIcon(
                  SijiIcons.note,
                  size: 16,
                  strokeWidth: 1.4,
                  color: selected ? AppColors.primaryInk : AppColors.iconMuted,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    note.title,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: AppText.itemTitle.copyWith(
                      color: selected ? AppColors.primaryInk : AppColors.text,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Text(note.updatedLabel, style: AppText.meta),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
