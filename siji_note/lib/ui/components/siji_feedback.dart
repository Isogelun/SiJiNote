import 'package:material_ui/material_ui.dart';

import '../../theme/app_theme.dart';

/// 标签，用于「引导库」这类弱提示。
class SijiTag extends StatelessWidget {
  const SijiTag({super.key, required this.label, this.highlighted = true});

  final String label;
  final bool highlighted;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 18,
      padding: const EdgeInsets.symmetric(horizontal: 7),
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: highlighted ? AppColors.selected : AppColors.disabled,
        borderRadius: BorderRadius.circular(5),
      ),
      child: Text(
        label,
        style: TextStyle(
          fontSize: 9,
          height: 1.2,
          color: highlighted ? AppColors.primary : AppColors.muted,
        ),
      ),
    );
  }
}

/// 保存状态：本地安全与云端成功是两件事，颜色区分状态。
enum SijiSaveStatus { unsaved, saving, saved, offline, conflict }

class SijiStatusPill extends StatelessWidget {
  const SijiStatusPill({super.key, required this.status, this.label});

  final SijiSaveStatus status;

  /// 覆盖默认文案，例如「已保存 · 刚刚」。
  final String? label;

  Color get _color {
    switch (status) {
      case SijiSaveStatus.unsaved:
        return const Color(0xFF9A9A9A);
      case SijiSaveStatus.saving:
        return AppColors.primary;
      case SijiSaveStatus.saved:
        return AppColors.success;
      case SijiSaveStatus.offline:
        return AppColors.offline;
      case SijiSaveStatus.conflict:
        return AppColors.danger;
    }
  }

  String get _defaultLabel {
    switch (status) {
      case SijiSaveStatus.unsaved:
        return '未保存';
      case SijiSaveStatus.saving:
        return '保存中…';
      case SijiSaveStatus.saved:
        return '已保存';
      case SijiSaveStatus.offline:
        return '离线 · 已存本地';
      case SijiSaveStatus.conflict:
        return '内容已在其他设备更新';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 6,
          height: 6,
          decoration: BoxDecoration(color: _color, shape: BoxShape.circle),
        ),
        const SizedBox(width: 6),
        Text(
          label ?? _defaultLabel,
          style: TextStyle(fontSize: 12, height: 1.5, color: _color),
        ),
      ],
    );
  }
}

/// 图标按钮：点击区域不小于 32，移动端建议 44。
class SijiIconButton extends StatelessWidget {
  const SijiIconButton({
    super.key,
    required this.icon,
    required this.onPressed,
    this.tooltip,
    this.size = 32,
  });

  final IconData icon;
  final VoidCallback? onPressed;
  final String? tooltip;
  final double size;

  @override
  Widget build(BuildContext context) {
    final button = SizedBox(
      width: size,
      height: size,
      child: IconButton(
        onPressed: onPressed,
        icon: Icon(icon, size: 16),
        color: AppColors.subtle,
        disabledColor: const Color(0xFFBDBDBD),
        padding: EdgeInsets.zero,
        constraints: BoxConstraints.tightFor(width: size, height: size),
        splashRadius: size / 2,
        style: IconButton.styleFrom(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
        ),
      ),
    );

    if (tooltip == null) return button;
    return Tooltip(message: tooltip!, child: button);
  }
}
