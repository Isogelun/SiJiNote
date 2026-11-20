import 'package:material_ui/material_ui.dart';

import '../../../theme/app_theme.dart';
import '../../../ui/components/components.dart';

class RailDestination {
  const RailDestination({
    required this.icon,
    required this.label,
    this.enabled = true,
    this.badgeCount = 0,
    this.disabledHint,
  });

  final SijiIcons icon;
  final String label;
  final bool enabled;

  /// 大于 0 时在图标右上角显示数量角标。
  final int badgeCount;

  /// 禁用项悬停提示，例如「即将上线」。
  final String? disabledHint;
}

/// 左侧 72px 图标栏，直接铺在窗口外壳底色上。
class NavRail extends StatelessWidget {
  const NavRail({
    super.key,
    required this.destinations,
    required this.selectedIndex,
    required this.onSelected,
    required this.userInitial,
    required this.onSettings,
  });

  final List<RailDestination> destinations;
  final int selectedIndex;
  final ValueChanged<int> onSelected;
  final String userInitial;
  final VoidCallback onSettings;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 72,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(10, 16, 10, 14),
        child: Column(
          children: [
            const _Logo(),
            const SizedBox(height: 22),
            for (var i = 0; i < destinations.length; i++) ...[
              _RailItem(
                destination: destinations[i],
                selected: i == selectedIndex,
                onTap: () => onSelected(i),
              ),
              const SizedBox(height: 4),
            ],
            const Spacer(),
            Container(width: 24, height: 1, color: AppColors.hairline),
            const SizedBox(height: 18),
            Container(
              width: 28,
              height: 28,
              alignment: Alignment.center,
              decoration: const BoxDecoration(
                color: AppColors.selected,
                shape: BoxShape.circle,
              ),
              child: Text(
                userInitial,
                style: const TextStyle(
                  fontSize: 11,
                  height: 1,
                  fontWeight: FontWeight.w500,
                  color: AppColors.primary,
                ),
              ),
            ),
            const SizedBox(height: 4),
            _RailItem(
              destination: const RailDestination(
                icon: SijiIcons.settings,
                label: '设置',
              ),
              selected: false,
              onTap: onSettings,
            ),
          ],
        ),
      ),
    );
  }
}

class _Logo extends StatelessWidget {
  const _Logo();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 32,
      height: 32,
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: AppColors.logo,
        borderRadius: BorderRadius.circular(9),
      ),
      child: const SijiIcon(SijiIcons.logo, size: 16, color: Colors.white),
    );
  }
}

class _RailItem extends StatelessWidget {
  const _RailItem({
    required this.destination,
    required this.selected,
    required this.onTap,
  });

  final RailDestination destination;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final enabled = destination.enabled;
    final color = selected ? AppColors.primary : AppColors.railIcon;
    final labelColor = enabled ? color : AppColors.railDisabled;
    final radius = BorderRadius.circular(AppRadius.control);

    final item = Material(
      color: selected ? AppColors.selected : Colors.transparent,
      borderRadius: radius,
      child: InkWell(
        onTap: enabled ? onTap : null,
        borderRadius: radius,
        hoverColor: AppColors.hairline,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 7),
          child: Column(
            children: [
              Stack(
                clipBehavior: Clip.none,
                children: [
                  SijiIcon(destination.icon, color: color),
                  if (destination.badgeCount > 0)
                    Positioned(
                      left: 13,
                      top: -3,
                      child: _Badge(count: destination.badgeCount),
                    ),
                ],
              ),
              const SizedBox(height: 3),
              Text(
                destination.label,
                style: AppText.railLabel.copyWith(color: labelColor),
              ),
            ],
          ),
        ),
      ),
    );

    return SizedBox(
      width: double.infinity,
      child: !enabled && destination.disabledHint != null
          ? Tooltip(message: destination.disabledHint!, child: item)
          : item,
    );
  }
}

class _Badge extends StatelessWidget {
  const _Badge({required this.count});

  final int count;

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: const BoxConstraints(minWidth: 12),
      height: 12,
      padding: const EdgeInsets.symmetric(horizontal: 3),
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: AppColors.badge,
        borderRadius: BorderRadius.circular(6),
      ),
      child: Text(
        count > 99 ? '99+' : '$count',
        style: const TextStyle(
          fontSize: 8,
          height: 1,
          fontWeight: FontWeight.w500,
          color: Colors.white,
        ),
      ),
    );
  }
}
