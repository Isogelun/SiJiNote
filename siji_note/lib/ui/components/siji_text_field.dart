import 'package:material_ui/material_ui.dart';

import '../../theme/app_theme.dart';

/// 基础输入框。
///
/// 覆盖设计稿的默认 / 聚焦 / 错误 / 禁用 / 密码 / 多行计数六种形态。
class SijiTextField extends StatefulWidget {
  const SijiTextField({
    super.key,
    this.controller,
    this.hint = '',
    this.enabled = true,
    this.errorText,
    this.helperText,
    this.obscureText = false,
    this.maxLines = 1,
    this.maxLength,
    this.showCounter = false,
    this.onChanged,
  });

  final TextEditingController? controller;
  final String hint;
  final bool enabled;
  final String? errorText;
  final String? helperText;
  final bool obscureText;
  final int maxLines;

  /// 设置后限制输入长度；配合 [showCounter] 在底部展示「最多 N 字 · 当前 x / N」。
  final int? maxLength;
  final bool showCounter;
  final ValueChanged<String>? onChanged;

  @override
  State<SijiTextField> createState() => _SijiTextFieldState();
}

class _SijiTextFieldState extends State<SijiTextField> {
  late final FocusNode _focusNode;

  @override
  void initState() {
    super.initState();
    _focusNode = FocusNode()..addListener(_handleFocusChange);
  }

  @override
  void dispose() {
    _focusNode
      ..removeListener(_handleFocusChange)
      ..dispose();
    super.dispose();
  }

  void _handleFocusChange() => setState(() {});

  Color get _borderColor {
    if (!widget.enabled) return Colors.transparent;
    if (widget.errorText != null) return AppColors.danger;
    if (_focusNode.hasFocus) return AppColors.primary;
    return AppColors.border;
  }

  Color get _fillColor =>
      widget.enabled ? AppColors.surface : AppColors.disabled;

  @override
  Widget build(BuildContext context) {
    final multiline = widget.maxLines > 1;

    final field = TextField(
      controller: widget.controller,
      focusNode: _focusNode,
      enabled: widget.enabled,
      obscureText: widget.obscureText,
      maxLines: widget.maxLines,
      maxLength: widget.maxLength,
      onChanged: widget.onChanged,
      style: TextStyle(
        fontSize: 13,
        height: 1.4,
        color: widget.enabled ? AppColors.text : AppColors.subtle,
      ),
      buildCounter: (context,
              {required currentLength, required isFocused, maxLength}) =>
          null,
      decoration: InputDecoration(
        isDense: true,
        hintText: widget.hint,
        hintStyle: const TextStyle(fontSize: 13, height: 1.4, color: AppColors.subtle),
        contentPadding: multiline
            ? EdgeInsets.zero
            : const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
        border: InputBorder.none,
        enabledBorder: InputBorder.none,
        focusedBorder: InputBorder.none,
        disabledBorder: InputBorder.none,
      ),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          constraints: multiline
              ? const BoxConstraints(minHeight: 96)
              : const BoxConstraints.tightFor(height: 40),
          padding: multiline
              ? const EdgeInsets.fromLTRB(12, 10, 12, 10)
              : null,
          decoration: BoxDecoration(
            color: _fillColor,
            borderRadius: BorderRadius.circular(AppRadius.control),
            border: Border.all(
              color: _borderColor,
              width: _focusNode.hasFocus && widget.enabled ? 1.5 : 1,
            ),
          ),
          child: multiline
              ? Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    field,
                    if (widget.showCounter) ...[
                      const SizedBox(height: 8),
                      _Counter(
                        controller: widget.controller,
                        maxLength: widget.maxLength,
                        enabled: widget.enabled,
                      ),
                    ],
                  ],
                )
              : field,
        ),
        if (widget.errorText != null || widget.helperText != null) ...[
          const SizedBox(height: 6),
          Text(
            widget.errorText ?? widget.helperText!,
            style: TextStyle(
              fontSize: 12,
              height: 1.5,
              color: widget.errorText != null
                  ? AppColors.danger
                  : AppColors.subtle,
            ),
          ),
        ],
      ],
    );
  }
}

class _Counter extends StatelessWidget {
  const _Counter({
    required this.controller,
    required this.maxLength,
    required this.enabled,
  });

  final TextEditingController? controller;
  final int? maxLength;
  final bool enabled;

  @override
  Widget build(BuildContext context) {
    const style = TextStyle(fontSize: 12, height: 1.5, color: AppColors.subtle);

    if (controller == null) {
      return Text('最多 ${maxLength ?? 0} 字 · 当前 0 / ${maxLength ?? 0}',
          style: style);
    }

    return ValueListenableBuilder<TextEditingValue>(
      valueListenable: controller!,
      builder: (context, value, _) {
        return Text(
          '最多 ${maxLength ?? 0} 字 · 当前 ${value.text.runes.length} / ${maxLength ?? 0}',
          style: style,
        );
      },
    );
  }
}
