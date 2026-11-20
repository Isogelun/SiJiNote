import 'package:material_ui/material_ui.dart';

/// 设计稿「思迹UI」变量表中的颜色令牌。
abstract final class AppColors {
  /// 页面底色（卡片之外）
  static const bg = Color(0xFFFAFAFA);

  /// 卡片、面板底色
  static const surface = Color(0xFFFFFFFF);

  /// 窗口外壳底色，图标栏直接铺在上面
  static const shell = Color(0xFFEBEBEB);

  /// 正文
  static const text = Color(0xFF1A1A1A);

  /// 次级文字
  static const muted = Color(0xFF5C5C5C);

  /// 辅助文字
  static const subtle = Color(0xFF6B6B6B);

  /// 分隔线、输入框描边
  static const border = Color(0xFFE4E0D6);

  /// 主色
  static const primary = Color(0xFF3E7A72);

  /// 主色文字
  static const primaryInk = Color(0xFF2F615B);

  /// 选中态底色
  static const selected = Color(0xFFE9F1EE);

  /// 禁用、占位底色
  static const disabled = Color(0xFFF0F0F0);

  /// 危险操作
  static const danger = Color(0xFFB04038);

  /// 保存成功状态点
  static const success = Color(0xFF2F7D5D);

  /// 离线状态点
  static const offline = Color(0xFFB5791F);

  /// 警告
  static const warning = Color(0xFF82632B);

  /// 图标栏 Logo 底色
  static const logo = Color(0xFF2F4F4A);

  /// 图标栏未选中图标与文字
  static const railIcon = Color(0xFF616161);

  /// 图标栏禁用项文字
  static const railDisabled = Color(0xFFC7C7C7);

  /// 回收站数量角标
  static const badge = Color(0xFFC96A55);

  /// 弱图标（面包屑、搜索、笔记行）
  static const iconMuted = Color(0xFF707070);

  /// 内容区分隔线
  static const line = Color(0xFFEAEAEA);

  /// 搜索框描边
  static const searchBorder = Color(0xFFE8EDEB);

  /// 快捷键角标、图标栏分隔线
  static const hairline = Color(0xFFE5E5E5);
}

abstract final class AppText {
  static const pageTitle = TextStyle(
    fontSize: 26,
    height: 1.35,
    fontWeight: FontWeight.w600,
    color: AppColors.text,
  );
  static const sectionTitle = TextStyle(
    fontSize: 13,
    height: 1.5,
    fontWeight: FontWeight.w500,
    color: AppColors.text,
  );
  static const body = TextStyle(
    fontSize: 13,
    height: 1.7,
    color: AppColors.muted,
  );
  static const itemTitle = TextStyle(
    fontSize: 13,
    height: 1.5,
    color: AppColors.text,
  );
  static const caption = TextStyle(
    fontSize: 12,
    height: 1.5,
    color: AppColors.subtle,
  );
  static const railLabel = TextStyle(
    fontSize: 10,
    height: 1.4,
    color: AppColors.railIcon,
  );
  static const meta = TextStyle(
    fontSize: 11,
    height: 1.5,
    color: AppColors.subtle,
  );
}

abstract final class AppRadius {
  static const card = 12.0;
  static const control = 8.0;
}

abstract final class AppTheme {
  static ThemeData get light {
    const scheme = ColorScheme.light(
      primary: AppColors.primary,
      onPrimary: Colors.white,
      surface: AppColors.surface,
      onSurface: AppColors.text,
      error: AppColors.danger,
    );

    return ThemeData(
      useMaterial3: true,
      colorScheme: scheme,
      scaffoldBackgroundColor: AppColors.shell,
      fontFamilyFallback: const [
        'PingFang SC',
        'Microsoft YaHei',
        'Noto Sans CJK SC',
      ],
      dividerColor: AppColors.border,
      textSelectionTheme: const TextSelectionThemeData(
        cursorColor: AppColors.primary,
        selectionColor: AppColors.selected,
        selectionHandleColor: AppColors.primary,
      ),
    );
  }
}
