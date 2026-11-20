import 'package:material_ui/material_ui.dart';

import '../../theme/app_theme.dart';

/// 复选框，16×16，圆角 4。
class SijiCheckbox extends StatelessWidget {
  const SijiCheckbox({
    super.key,
    required this.value,
    required this.onChanged,
    this.label,
  });

  final bool value;
  final ValueChanged<bool>? onChanged;
  final String? label;

  @override
  Widget build(BuildContext context) {
    final enabled = onChanged != null;

    return InkWell(
      onTap: enabled ? () => onChanged!(!value) : null,
      borderRadius: BorderRadius.circular(4),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 4),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: 16,
              height: 16,
              decoration: BoxDecoration(
                color: value
                    ? (enabled ? AppColors.primary : AppColors.disabled)
                    : AppColors.surface,
                borderRadius: BorderRadius.circular(4),
                border: value
                    ? null
                    : Border.all(
                        color: enabled ? AppColors.border : AppColors.disabled,
                      ),
              ),
              child: value
                  ? const Icon(Icons.check, size: 12, color: Colors.white)
                  : null,
            ),
            if (label != null) ...[
              const SizedBox(width: 8),
              Text(
                label!,
                style: TextStyle(
                  fontSize: 13,
                  height: 1.4,
                  color: enabled ? AppColors.text : AppColors.subtle,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

/// 开关，40×24。
class SijiSwitch extends StatelessWidget {
  const SijiSwitch({super.key, required this.value, required this.onChanged});

  final bool value;
  final ValueChanged<bool>? onChanged;

  @override
  Widget build(BuildContext context) {
    final enabled = onChanged != null;

    return GestureDetector(
      onTap: enabled ? () => onChanged!(!value) : null,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 120),
        width: 40,
        height: 24,
        padding: const EdgeInsets.all(3),
        alignment: value ? Alignment.centerRight : Alignment.centerLeft,
        decoration: BoxDecoration(
          color: value
              ? (enabled ? AppColors.primaryInk : AppColors.disabled)
              : const Color(0xFFEDE9E0),
          borderRadius: BorderRadius.circular(999),
        ),
        child: Container(
          width: 18,
          height: 18,
          decoration: const BoxDecoration(
            color: Colors.white,
            shape: BoxShape.circle,
          ),
        ),
      ),
    );
  }
}

/// 分段控件，用于「编辑 / 预览」这类二选一。
class SijiSegmentedControl extends StatelessWidget {
  const SijiSegmentedControl({
    super.key,
    required this.segments,
    required this.selectedIndex,
    required this.onChanged,
    this.height = 40,
  });

  final List<String> segments;
  final int selectedIndex;
  final ValueChanged<int> onChanged;
  final double height;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: height,
      padding: const EdgeInsets.all(3),
      decoration: BoxDecoration(
        color: AppColors.disabled,
        borderRadius: BorderRadius.circular(AppRadius.control),
      ),
      child: Row(
        children: [
          for (var i = 0; i < segments.length; i++)
            Expanded(
              child: GestureDetector(
                onTap: () => onChanged(i),
                behavior: HitTestBehavior.opaque,
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 120),
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: i == selectedIndex
                        ? AppColors.surface
                        : Colors.transparent,
                    borderRadius: BorderRadius.circular(6),
                    border: i == selectedIndex
                        ? Border.all(color: AppColors.border)
                        : null,
                  ),
                  child: Text(
                    segments[i],
                    style: TextStyle(
                      fontSize: 13,
                      height: 1.3,
                      color: i == selectedIndex
                          ? AppColors.text
                          : AppColors.muted,
                      fontWeight: i == selectedIndex
                          ? FontWeight.w500
                          : FontWeight.w400,
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
