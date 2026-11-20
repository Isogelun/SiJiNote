import 'package:flutter_test/flutter_test.dart';
import 'package:material_ui/material_ui.dart';

import 'package:siji_note/data/memory_note_repository.dart';
import 'package:siji_note/data/note_seed.dart';
import 'package:siji_note/features/editor/note_editor_view.dart';
import 'package:siji_note/features/home/widgets/library_home_view.dart';
import 'package:siji_note/features/home/widgets/library_panel.dart';
import 'package:siji_note/features/home/widgets/nav_rail.dart';
import 'package:siji_note/main.dart';

Widget _buildApp() {
  return SiJiNoteApp(
    repository: MemoryNoteRepository(initial: buildSeedNotes()),
  );
}

Future<void> _pumpApp(WidgetTester tester) async {
  await tester.pumpWidget(_buildApp());
  await tester.pumpAndSettle();
}

void main() {
  testWidgets('主界面从仓储读取知识库与笔记', (WidgetTester tester) async {
    tester.view.physicalSize = const Size(1440, 900);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.reset);
    await _pumpApp(tester);

    expect(find.text('我的第一个知识库'), findsWidgets);
    expect(find.text('上手指南'), findsWidgets);
    expect(find.text('欢迎使用思迹笔记'), findsWidgets);
    expect(find.text('上手指南 · 3 篇笔记'), findsOneWidget);
  });

  testWidgets('目录切换、笔记选择、文件夹折叠与编辑页入口', (tester) async {
    tester.view.physicalSize = const Size(1440, 900);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.reset);
    await _pumpApp(tester);

    var panel = tester.widget<LibraryPanel>(find.byType(LibraryPanel));
    expect(panel.selectedNoteIndex, isNull);
    expect(panel.selectedSection, 0);

    await tester.tap(find.text('未分类'));
    await tester.pumpAndSettle();
    expect(find.text('未分类 · 0 篇笔记'), findsOneWidget);
    expect(find.text('这里还没有笔记'), findsOneWidget);

    await tester.tap(find.text('上手指南').first);
    await tester.pumpAndSettle();
    expect(find.text('这里还没有笔记'), findsNothing);

    final folder = find.descendant(
      of: find.byType(LibraryHomeView),
      matching: find.text('上手指南'),
    );
    final contentNote = find.descendant(
      of: find.byType(LibraryHomeView),
      matching: find.text('Markdown 速查'),
    );
    await tester.tap(folder);
    await tester.pumpAndSettle();
    expect(contentNote, findsNothing);
    await tester.tap(folder);
    await tester.pumpAndSettle();
    expect(contentNote, findsOneWidget);

    await tester.tap(find.text('Markdown 速查').first);
    await tester.pumpAndSettle();
    expect(find.byType(NoteEditorView), findsOneWidget);

    panel = tester.widget<LibraryPanel>(find.byType(LibraryPanel));
    expect(panel.selectedNoteIndex, 1);

    await tester.tap(find.byTooltip('返回'));
    await tester.pumpAndSettle();
    expect(find.byType(LibraryHomeView), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('搜索快捷键不挤占输入区', (tester) async {
    await _pumpApp(tester);
    final search = find.byType(TextField);
    final field = tester.widget<TextField>(search);
    final fieldRect = tester.getRect(search);
    final shortcutRect = tester.getRect(
      find.byWidget(field.decoration!.suffixIcon!),
    );
    expect(fieldRect.size, const Size(240, 32));
    expect(shortcutRect.width, lessThanOrEqualTo(52));
    expect(shortcutRect.right, lessThanOrEqualTo(fieldRect.right));
    await tester.enterText(search, 'Markdown');
    await tester.pump();
    expect(find.text('Markdown'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('存储项禁用并保留即将上线提示', (tester) async {
    await _pumpApp(tester);
    final rail = tester.widget<NavRail>(find.byType(NavRail));
    final storage = rail.destinations.singleWhere((item) => item.label == '存储');
    expect(storage.enabled, isFalse);
    expect(storage.disabledHint, '即将上线');
    expect(find.text('5'), findsOneWidget);
    await tester.tap(find.text('存储'));
    await tester.pumpAndSettle();
    expect(tester.widget<NavRail>(find.byType(NavRail)).selectedIndex, 0);
    expect(tester.takeException(), isNull);
  });

  for (final size in const [
    Size(1440, 900),
    Size(1100, 700),
    Size(1044, 700),
    Size(1024, 700),
    Size(1023, 700),
    Size(900, 700),
  ]) {
    testWidgets('${size.width.toInt()} 宽度下不溢出', (WidgetTester tester) async {
      tester.view.physicalSize = size;
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.reset);

      await _pumpApp(tester);

      expect(tester.takeException(), isNull);
      expect(
        find.byType(LibraryPanel),
        size.width >= 1024 ? findsOneWidget : findsNothing,
      );
    });
  }
}
