import 'package:markdown/markdown.dart' as md;
import 'package:material_ui/material_ui.dart';

import '../../../theme/app_theme.dart';

/// 只读 Markdown 预览：解析交给 markdown 包，渲染按设计稿的排版走。
class MarkdownPreview extends StatelessWidget {
  const MarkdownPreview({
    super.key,
    required this.source,
    this.padding = const EdgeInsets.fromLTRB(40, 26, 40, 40),
    this.header,
  });

  final String source;
  final EdgeInsets padding;

  /// 渲染在正文之前的固定头部，例如笔记标题与更新时间。
  final Widget? header;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: padding,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          ?header,
          ..._buildBlocks(md.Document().parse(source)),
        ],
      ),
    );
  }
}

const _body = TextStyle(fontSize: 13.5, height: 1.9, color: AppColors.text);
const _mono = TextStyle(
  fontSize: 12.5,
  height: 1.7,
  color: AppColors.text,
  fontFamily: 'Consolas',
  fontFamilyFallback: ['Menlo', 'monospace'],
);
const _quoteBg = Color(0xFFF6F1DE);

List<md.Node> _childrenOf(md.Element element) =>
    element.children ?? const <md.Node>[];

List<Widget> _buildBlocks(List<md.Node> nodes) {
  final widgets = <Widget>[];
  for (final node in nodes) {
    if (node is md.Text) {
      if (node.text.trim().isEmpty) continue;
      widgets.add(Padding(
        padding: const EdgeInsets.only(bottom: 12),
        child: Text(node.text, style: _body),
      ));
      continue;
    }
    if (node is! md.Element) continue;

    switch (node.tag) {
      case 'h1':
        widgets.add(_heading(node, 26, 8, 12, FontWeight.w600));
      case 'h2':
        widgets.add(_heading(node, 20, 16, 8, FontWeight.w600));
      case 'h3':
        widgets.add(_heading(node, 17, 14, 6, FontWeight.w600));
      case 'p':
        widgets.add(Padding(
          padding: const EdgeInsets.only(bottom: 12),
          child: Text.rich(_inline(_childrenOf(node), _body)),
        ));
      case 'blockquote':
        widgets.add(_quote(node));
      case 'pre':
        widgets.add(_codeBlock(node));
      case 'ul':
        widgets.add(_list(node, ordered: false));
      case 'ol':
        widgets.add(_list(node, ordered: true));
      case 'hr':
        widgets.add(const Padding(
          padding: EdgeInsets.symmetric(vertical: 12),
          child: Divider(height: 1),
        ));
      default:
        widgets.addAll(_buildBlocks(_childrenOf(node)));
    }
  }
  return widgets;
}

Widget _heading(md.Element node, double size, double top, double bottom,
    FontWeight weight) {
  return Padding(
    padding: EdgeInsets.only(top: top, bottom: bottom),
    child: Text.rich(
      _inline(_childrenOf(node), _body.copyWith(fontSize: size, fontWeight: weight)),
    ),
  );
}

Widget _quote(md.Element node) {
  return Container(
    margin: const EdgeInsets.only(bottom: 12),
    decoration: BoxDecoration(
      color: _quoteBg,
      borderRadius: BorderRadius.circular(8),
    ),
    clipBehavior: Clip.antiAlias,
    child: IntrinsicHeight(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Container(width: 3, color: AppColors.primary),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(14, 12, 14, 0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: _buildBlocks(_childrenOf(node)),
              ),
            ),
          ),
        ],
      ),
    ),
  );
}

Widget _codeBlock(md.Element node) {
  final code = node.textContent.trimRight();
  return Container(
    width: double.infinity,
    margin: const EdgeInsets.only(bottom: 12),
    padding: const EdgeInsets.all(12),
    decoration: BoxDecoration(
      color: AppColors.bg,
      borderRadius: BorderRadius.circular(8),
      border: Border.all(color: AppColors.line),
    ),
    child: SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Text(code, style: _mono),
    ),
  );
}

Widget _list(md.Element node, {required bool ordered}) {
  final items = _childrenOf(node).whereType<md.Element>().where((e) => e.tag == 'li');

  return Padding(
    padding: const EdgeInsets.only(bottom: 12),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        for (final (index, item) in items.indexed)
          Padding(
            padding: const EdgeInsets.only(bottom: 4),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(
                  width: 20,
                  child: Text(
                    ordered ? '${index + 1}.' : '•',
                    style: _body.copyWith(color: AppColors.muted),
                  ),
                ),
                Expanded(
                  child: Text.rich(_inline(_childrenOf(item), _body)),
                ),
              ],
            ),
          ),
      ],
    ),
  );
}

InlineSpan _inline(List<md.Node> nodes, TextStyle base) {
  final spans = <InlineSpan>[];
  for (final node in nodes) {
    if (node is md.Text) {
      spans.add(TextSpan(text: node.text, style: base));
      continue;
    }
    if (node is! md.Element) continue;

    switch (node.tag) {
      case 'strong':
        spans.add(_inline(_childrenOf(node), base.copyWith(fontWeight: FontWeight.w600)));
      case 'em':
        spans.add(_inline(_childrenOf(node), base.copyWith(fontStyle: FontStyle.italic)));
      case 'del':
        spans.add(_inline(
          _childrenOf(node),
          base.copyWith(decoration: TextDecoration.lineThrough),
        ));
      case 'code':
        spans.add(TextSpan(
          text: node.textContent,
          style: base.copyWith(
            fontFamily: 'Consolas',
            fontFamilyFallback: const ['Menlo', 'monospace'],
            fontSize: (base.fontSize ?? 13.5) - 1,
            backgroundColor: AppColors.disabled,
          ),
        ));
      case 'a':
        spans.add(_inline(
          _childrenOf(node),
          base.copyWith(
            color: AppColors.primary,
            decoration: TextDecoration.underline,
            decorationColor: AppColors.primary,
          ),
        ));
      case 'br':
        spans.add(const TextSpan(text: '\n'));
      default:
        spans.add(_inline(_childrenOf(node), base));
    }
  }
  return TextSpan(style: base, children: spans);
}
