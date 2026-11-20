import 'package:material_ui/material_ui.dart';

import '../../../theme/app_theme.dart';
import '../../../ui/components/components.dart';

enum EditorMode { edit, preview }

/// 编辑页顶栏：返回、撤销重做、格式按钮与「编辑 / 预览」切换。
class EditorToolbar extends StatelessWidget {
  const EditorToolbar({
    super.key,
    required this.mode,
    required this.onModeChanged,
    required this.onBack,
    required this.onUndo,
    required this.onRedo,
    required this.canUndo,
    required this.canRedo,
    required this.onHeading,
    required this.onBold,
    required this.onItalic,
    required this.onStrike,
    required this.onLink,
  });

  final EditorMode mode;
  final ValueChanged<EditorMode> onModeChanged;
  final VoidCallback onBack;
  final VoidCallback onUndo;
  final VoidCallback onRedo;
  final bool canUndo;
  final bool canRedo;
  final ValueChanged<int> onHeading;
  final VoidCallback onBold;
  final VoidCallback onItalic;
  final VoidCallback onStrike;
  final VoidCallback onLink;

  @override
  Widget build(BuildContext context) {
    final editing = mode == EditorMode.edit;

    return SizedBox(
      height: 56,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Row(
          children: [
            _ToolIcon(
              icon: SijiIcons.back,
              tooltip: '返回',
              onTap: onBack,
            ),
            const _ToolGap(),
            _ToolIcon(
              icon: null,
              materialIcon: Icons.undo,
              tooltip: '撤销',
              onTap: editing && canUndo ? onUndo : null,
            ),
            _ToolIcon(
              icon: null,
              materialIcon: Icons.redo,
              tooltip: '重做',
              onTap: editing && canRedo ? onRedo : null,
            ),
            const _ToolGap(),
            for (var level = 1; level <= 3; level++)
              _ToolText(
                label: 'H$level',
                onTap: editing ? () => onHeading(level) : null,
              ),
            const _ToolGap(),
            _ToolText(
              label: 'B',
              bold: true,
              onTap: editing ? onBold : null,
            ),
            _ToolText(
              label: 'I',
              italic: true,
              onTap: editing ? onItalic : null,
            ),
            _ToolText(
              label: 'S',
              strike: true,
              onTap: editing ? onStrike : null,
            ),
            const _ToolGap(),
            _ToolIcon(
              icon: null,
              materialIcon: Icons.link,
              tooltip: '插入链接',
              onTap: editing ? onLink : null,
            ),
            const Spacer(),
            SizedBox(
              width: 132,
              child: SijiSegmentedControl(
                segments: const ['编辑', '预览'],
                selectedIndex: mode == EditorMode.edit ? 0 : 1,
                height: 32,
                onChanged: (index) => onModeChanged(
                  index == 0 ? EditorMode.edit : EditorMode.preview,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ToolGap extends StatelessWidget {
  const _ToolGap();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 1,
      height: 14,
      margin: const EdgeInsets.symmetric(horizontal: 8),
      color: AppColors.hairline,
    );
  }
}

class _ToolText extends StatelessWidget {
  const _ToolText({
    required this.label,
    required this.onTap,
    this.bold = false,
    this.italic = false,
    this.strike = false,
  });

  final String label;
  final VoidCallback? onTap;
  final bool bold;
  final bool italic;
  final bool strike;

  @override
  Widget build(BuildContext context) {
    final enabled = onTap != null;

    return Tooltip(
      message: label,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(6),
        child: SizedBox(
          width: 28,
          height: 28,
          child: Center(
            child: Text(
              label,
              style: TextStyle(
                fontSize: 12,
                height: 1,
                fontWeight: bold ? FontWeight.w700 : FontWeight.w500,
                fontStyle: italic ? FontStyle.italic : FontStyle.normal,
                decoration: strike ? TextDecoration.lineThrough : null,
                color: enabled ? AppColors.muted : AppColors.railDisabled,
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _ToolIcon extends StatelessWidget {
  const _ToolIcon({
    required this.icon,
    required this.tooltip,
    required this.onTap,
    this.materialIcon,
  });

  final SijiIcons? icon;
  final IconData? materialIcon;
  final String tooltip;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final color = onTap == null ? AppColors.railDisabled : AppColors.muted;

    return Tooltip(
      message: tooltip,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(6),
        child: SizedBox(
          width: 28,
          height: 28,
          child: Center(
            child: icon != null
                ? SijiIcon(icon!, size: 18, color: color)
                : Icon(materialIcon, size: 17, color: color),
          ),
        ),
      ),
    );
  }
}
