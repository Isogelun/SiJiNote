/// 字数口径：中文每字 1，连续英文/数字算 1，不含 Markdown 标记与空白。
int countWords(String markdown) {
  if (markdown.trim().isEmpty) return 0;

  var text = markdown
      // 代码块整体不计入正文
      .replaceAll(RegExp(r'```[\s\S]*?```'), ' ')
      // 行内代码与图片语法
      .replaceAll(RegExp(r'`[^`]*`'), ' ')
      .replaceAll(RegExp(r'!\[[^\]]*\]\([^)]*\)'), ' ')
      // 链接保留文字
      .replaceAllMapped(
        RegExp(r'\[([^\]]*)\]\([^)]*\)'),
        (match) => match.group(1) ?? '',
      )
      // 其余标记字符
      .replaceAll(RegExp(r'[#>*_~\-\[\]()]'), ' ');

  final matches = RegExp(
    r'[\u3400-\u4dbf\u4e00-\u9fff]|[A-Za-z0-9]+',
  ).allMatches(text);
  return matches.length;
}
