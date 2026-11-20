import 'package:material_ui/material_ui.dart';

import '../../theme/app_theme.dart';

enum SijiButtonVariant { primary, secondary, danger }

enum SijiButtonSize {
  /// 目录栏、工具条内的小按钮，高 30。
  small(30, 12),

  /// 目录栏底部、内容区次级操作，高 32。
  medium(32, 12),

  /// 桌面默认，高 40。
  regular(40, 13),

  /// 移动端主操作，高 44。
  large(44, 14);

  const SijiButtonSize(this.height, this.fontSize);

  final double height;
  final double fontSize;
}

/// 基础按钮。
///
/// 设计稿：桌面 40 高，移动 44–48，圆角 8，文字居中。
class SijiButton extends StatelessWidget {
  const SijiButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.variant = SijiButtonVariant.primary,
    this.size = SijiButtonSize.regular,
    this.loading = false,
    this.icon,
    this.expand = true,
    this.radius = AppRadius.control,
  });

  final String label;
  final VoidCallback? onPressed;
  final SijiButtonVariant variant;
  final SijiButtonSize size;

  /// 加载态：保留按钮宽度，左侧显示进度圈。
  final bool loading;
  final IconData? icon;

  /// 是否占满父级宽度，默认是。
  final bool expand;

  final double radius;

  @override
  Widget build(BuildContext context) {
    final enabled = onPressed != null && !loading;
    final colors = _colors;

    final child = Row(
      mainAxisSize: expand ? MainAxisSize.max : MainAxisSize.min,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        if (loading) ...[
          SizedBox(
            width: 12,
            height: 12,
            child: CircularProgressIndicator(
              strokeWidth: 1.5,
              color: variant == SijiButtonVariant.secondary
                  ? AppColors.muted
                  : Colors.white,
            ),
          ),
          const SizedBox(width: 6),
        ] else if (icon != null) ...[
          Icon(icon, size: size.fontSize + 2, color: colors.foreground),
          const SizedBox(width: 6),
        ],
        Flexible(
          child: Text(
            label,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: TextStyle(
              fontSize: size.fontSize,
              height: 1.3,
              fontWeight:
                  variant == SijiButtonVariant.primary ||
                      variant == SijiButtonVariant.danger
                  ? FontWeight.w500
                  : FontWeight.w400,
              color: enabled ? colors.foreground : const Color(0xFFA3A3A3),
            ),
          ),
        ),
      ],
    );

    return SizedBox(
      height: size.height,
      child: TextButton(
        onPressed: enabled ? onPressed : null,
        style: TextButton.styleFrom(
          backgroundColor: enabled ? colors.background : AppColors.disabled,
          foregroundColor: colors.foreground,
          disabledBackgroundColor: AppColors.disabled,
          disabledForegroundColor: const Color(0xFFA3A3A3),
          padding: EdgeInsets.symmetric(
            horizontal: size.height <= 32 ? 12 : 16,
          ),
          minimumSize: Size.zero,
          tapTargetSize: MaterialTapTargetSize.shrinkWrap,
          side: enabled && colors.border != null
              ? BorderSide(color: colors.border!)
              : BorderSide.none,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(radius),
          ),
        ),
        child: child,
      ),
    );
  }

  _ButtonColors get _colors {
    switch (variant) {
      case SijiButtonVariant.primary:
        return const _ButtonColors(
          background: AppColors.primary,
          foreground: Colors.white,
        );
      case SijiButtonVariant.secondary:
        return const _ButtonColors(
          background: AppColors.surface,
          foreground: AppColors.muted,
          border: AppColors.border,
        );
      case SijiButtonVariant.danger:
        return const _ButtonColors(
          background: AppColors.danger,
          foreground: Colors.white,
        );
    }
  }
}

class _ButtonColors {
  const _ButtonColors({
    required this.background,
    required this.foreground,
    this.border,
  });

  final Color background;
  final Color foreground;
  final Color? border;
}
