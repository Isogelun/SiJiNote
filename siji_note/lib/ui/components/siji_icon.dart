import 'dart:math' as math;

import 'package:material_ui/material_ui.dart';

/// 设计稿「思迹UI」里的线性图标，坐标按 20×20 画布记录。
enum SijiIcons {
  logo,
  library,
  star,
  storage,
  trash,
  settings,
  back,
  notebook,
  chevronDown,
  plus,
  folder,
  note,
  search,
}

class SijiIcon extends StatelessWidget {
  const SijiIcon(
    this.icon, {
    super.key,
    this.size = 20,
    required this.color,
    this.strokeWidth = 1.5,
  });

  final SijiIcons icon;
  final double size;
  final Color color;

  /// 以 20×20 画布计的线宽，随 [size] 等比缩放。
  final double strokeWidth;

  @override
  Widget build(BuildContext context) {
    return SizedBox.square(
      dimension: size,
      child: CustomPaint(painter: _SijiIconPainter(icon, color, strokeWidth)),
    );
  }
}

class _SijiIconPainter extends CustomPainter {
  _SijiIconPainter(this.icon, this.color, this.strokeWidth);

  final SijiIcons icon;
  final Color color;
  final double strokeWidth;

  @override
  void paint(Canvas canvas, Size size) {
    canvas.scale(size.width / 20, size.height / 20);
    final paint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round
      ..isAntiAlias = true;

    void line(double x1, double y1, double x2, double y2) =>
        canvas.drawLine(Offset(x1, y1), Offset(x2, y2), paint);

    void poly(List<double> c, {bool close = false}) {
      final path = Path()..moveTo(c[0], c[1]);
      for (var i = 2; i + 1 < c.length; i += 2) {
        path.lineTo(c[i], c[i + 1]);
      }
      if (close) path.close();
      canvas.drawPath(path, paint);
    }

    void rrect(double l, double t, double r, double b, double radius) => canvas
        .drawRRect(RRect.fromLTRBR(l, t, r, b, Radius.circular(radius)), paint);

    void document() {
      poly([
        11.5,
        3,
        6,
        3,
        5,
        4,
        5,
        16,
        6,
        17,
        14,
        17,
        15,
        16,
        15,
        6.5,
        11.5,
        3,
      ], close: true);
      poly([11.5, 3, 11.5, 6.5, 15, 6.5]);
      line(7.8, 10.4, 12.2, 10.4);
      line(7.8, 13.2, 10.6, 13.2);
    }

    switch (icon) {
      case SijiIcons.logo:
      case SijiIcons.note:
        document();
      case SijiIcons.library:
        rrect(3.4, 5.2, 9.5, 15.4, 1.2);
        rrect(10.5, 5.2, 16.6, 15.4, 1.2);
      case SijiIcons.star:
        const cx = 10.0, cy = 10.1, outer = 6.4, inner = 3.1;
        final path = Path();
        for (var i = 0; i < 10; i++) {
          final r = i.isEven ? outer : inner;
          final a = -math.pi / 2 + i * math.pi / 5;
          final p = Offset(cx + r * math.cos(a), cy + r * math.sin(a));
          i == 0 ? path.moveTo(p.dx, p.dy) : path.lineTo(p.dx, p.dy);
        }
        canvas.drawPath(path..close(), paint);
      case SijiIcons.storage:
        canvas.drawOval(Rect.fromLTRB(4.2, 3.3, 15.8, 7.5), paint);
        line(4.2, 5.4, 4.2, 14.6);
        line(15.8, 5.4, 15.8, 14.6);
        canvas.drawArc(
          Rect.fromLTRB(4.2, 7.9, 15.8, 12.1),
          0,
          math.pi,
          false,
          paint,
        );
        canvas.drawArc(
          Rect.fromLTRB(4.2, 12.5, 15.8, 16.7),
          0,
          math.pi,
          false,
          paint,
        );
      case SijiIcons.trash:
        line(3.6, 5.9, 16.4, 5.9);
        poly([8.2, 5.9, 8.2, 3.3, 11.8, 3.3, 11.8, 5.9]);
        poly([5.6, 5.9, 6.3, 16.9, 13.7, 16.9, 14.4, 5.9]);
      case SijiIcons.settings:
        line(4, 7.2, 5.7, 7.2);
        line(9.5, 7.2, 16, 7.2);
        line(4, 12.8, 10.5, 12.8);
        line(14.3, 12.8, 16, 12.8);
        canvas.drawCircle(const Offset(7.6, 7.2), 1.9, paint);
        canvas.drawCircle(const Offset(12.4, 12.8), 1.9, paint);
      case SijiIcons.back:
        poly([12, 5, 7, 10, 12, 15]);
      case SijiIcons.notebook:
        rrect(4, 3, 16, 17, 1.5);
        line(7, 3, 7, 17);
        line(10, 7, 13, 7);
      case SijiIcons.chevronDown:
        poly([6, 8, 10, 12, 14, 8]);
      case SijiIcons.plus:
        line(10, 4, 10, 16);
        line(4, 10, 16, 10);
      case SijiIcons.folder:
        poly([
          3,
          5,
          3.6,
          4,
          7.6,
          4,
          9.2,
          5.8,
          16.4,
          5.8,
          17,
          6.4,
          17,
          15.4,
          16.4,
          16,
          3.6,
          16,
          3,
          15.4,
        ], close: true);
      case SijiIcons.search:
        canvas.drawCircle(const Offset(8.9, 8.9), 5.7, paint);
        line(13.1, 13.1, 16.8, 16.8);
    }
  }

  @override
  bool shouldRepaint(_SijiIconPainter old) =>
      old.icon != icon || old.color != color || old.strokeWidth != strokeWidth;
}
