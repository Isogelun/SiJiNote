import 'package:material_ui/material_ui.dart';

import '../../../theme/app_theme.dart';
import '../../../ui/components/components.dart';
import '../home_data.dart';

/// 中间 296px 目录栏：返回总览、知识库切换、文件夹分区与笔记列表。
class LibraryPanel extends StatelessWidget {
  const LibraryPanel({
    super.key,
    required this.library,
    required this.sections,
    required this.selectedSection,
    required this.onSectionSelected,
    required this.notes,
    required this.selectedNoteIndex,
    required this.onNoteSelected,
    required this.onBack,
    required this.onSwitchLibrary,
    required this.onNewNote,
    required this.onNewFolder,
  });

  final LibraryInfo library;
  final List<String> sections;
  final int selectedSection;
  final ValueChanged<int> onSectionSelected;

  /// 全部笔记；按 [selectedSection] 过滤后展示，回调仍返回在全量列表中的下标。
  final List<NoteItem> notes;
  final int? selectedNoteIndex;
  final ValueChanged<int> onNoteSelected;
  final VoidCallback onBack;
  final VoidCallback onSwitchLibrary;
  final VoidCallback onNewNote;
  final VoidCallback onNewFolder;

  @override
  Widget build(BuildContext context) {
    final section = sections[selectedSection];
    final visible = [
      for (var i = 0; i < notes.length; i++)
        if (notes[i].folder == section) i,
    ];

    return Container(
      width: 296,
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
            _BackRow(onTap: onBack),
            _LibrarySwitcher(name: library.name, onTap: onSwitchLibrary),
            Padding(
              padding: const EdgeInsets.fromLTRB(12, 4, 12, 0),
              child: SizedBox(
                height: 44,
                child: Row(
                  children: [
                    for (var i = 0; i < sections.length; i++) ...[
                      if (i > 0) const SizedBox(width: 6),
                      Expanded(
                        child: _SectionTab(
                          label: sections[i],
                          selected: i == selectedSection,
                          onTap: () => onSectionSelected(i),
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            ),
            SizedBox(
              height: 44,
              child: Padding(
                padding: const EdgeInsets.only(left: 16, right: 12),
                child: Row(
                  children: [
                    Expanded(
                      child: Text(
                        '$section · ${visible.length} 篇笔记',
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: AppText.caption.copyWith(color: AppColors.muted),
                      ),
                    ),
                    Tooltip(
                      message: '新建笔记',
                      child: InkWell(
                        onTap: onNewNote,
                        borderRadius: BorderRadius.circular(AppRadius.control),
                        child: const SizedBox.square(
                          dimension: 32,
                          child: Center(
                            child: SijiIcon(
                              SijiIcons.plus,
                              color: AppColors.primary,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            Expanded(
              child: visible.isEmpty
                  ? Padding(
                      padding: const EdgeInsets.fromLTRB(20, 8, 20, 0),
                      child: Text('这里还没有笔记', style: AppText.caption),
                    )
                  : ListView.separated(
                      padding: const EdgeInsets.symmetric(horizontal: 8),
                      itemCount: visible.length,
                      separatorBuilder: (_, _) => const SizedBox(height: 4),
                      itemBuilder: (context, i) {
                        final index = visible[i];
                        return _NoteRow(
                          note: notes[index],
                          selected: index == selectedNoteIndex,
                          onTap: () => onNoteSelected(index),
                        );
                      },
                    ),
            ),
            Container(
              height: 56,
              color: AppColors.bg,
              padding: const EdgeInsets.all(12),
              child: Row(
                children: [
                  Expanded(
                    child: SijiButton(
                      label: '+ 新建文件夹',
                      variant: SijiButtonVariant.secondary,
                      size: SijiButtonSize.medium,
                      radius: 10,
                      onPressed: onNewFolder,
                    ),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: SijiButton(
                      label: '+ 新建笔记',
                      size: SijiButtonSize.medium,
                      radius: 10,
                      onPressed: onNewNote,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _BackRow extends StatelessWidget {
  const _BackRow({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: const SizedBox(
        height: 44,
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: 16),
          child: Row(
            children: [
              SijiIcon(SijiIcons.back, color: AppColors.muted),
              SizedBox(width: 8),
              Text(
                '知识库总览',
                style: TextStyle(
                  fontSize: 13,
                  height: 1.5,
                  color: AppColors.muted,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _LibrarySwitcher extends StatelessWidget {
  const _LibrarySwitcher({required this.name, required this.onTap});

  final String name;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: AppColors.bg,
      child: InkWell(
        onTap: onTap,
        child: SizedBox(
          height: 56,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              children: [
                const SijiIcon(SijiIcons.notebook, color: AppColors.primary),
                const SizedBox(width: 10),
                Expanded(
                  child: Text(
                    name,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontSize: 16,
                      height: 1.4,
                      fontWeight: FontWeight.w700,
                      color: AppColors.text,
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                const SijiIcon(SijiIcons.chevronDown, color: AppColors.muted),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _SectionTab extends StatelessWidget {
  const _SectionTab({
    required this.label,
    required this.selected,
    required this.onTap,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final radius = BorderRadius.circular(AppRadius.control);
    return Material(
      color: selected ? AppColors.selected : Colors.transparent,
      borderRadius: radius,
      child: InkWell(
        onTap: onTap,
        borderRadius: radius,
        child: SizedBox(
          height: 32,
          child: Center(
            child: Text(
              label,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: TextStyle(
                fontSize: 12,
                height: 1.4,
                color: selected ? AppColors.primaryInk : AppColors.muted,
              ),
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
    required this.selected,
    required this.onTap,
  });

  final NoteItem note;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final radius = BorderRadius.circular(AppRadius.control);
    final color = selected ? AppColors.primaryInk : AppColors.text;
    return Material(
      color: selected ? AppColors.selected : Colors.transparent,
      borderRadius: radius,
      child: InkWell(
        onTap: onTap,
        borderRadius: radius,
        child: SizedBox(
          height: 40,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12),
            child: Row(
              children: [
                SijiIcon(
                  SijiIcons.note,
                  color: selected ? AppColors.primaryInk : AppColors.muted,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    note.title,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: AppText.itemTitle.copyWith(color: color),
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
