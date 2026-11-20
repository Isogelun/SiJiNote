import 'package:flutter_test/flutter_test.dart';
import 'package:material_ui/material_ui.dart';

import 'package:siji_note/data/memory_note_repository.dart';
import 'package:siji_note/data/note_seed.dart';
import 'package:siji_note/features/editor/note_editor_view.dart';
import 'package:siji_note/main.dart';

Widget _buildApp() {
  return SiJiNoteApp(
    repository: MemoryNoteRepository(initial: buildSeedNotes()),
  );
}

Future<void> _openNote(WidgetTester tester, String title) async {
  tester.view.physicalSize = const Size(1440, 900);
  tester.view.devicePixelRatio = 1;
  addTearDown(tester.view.reset);

  await tester.pumpWidget(_buildApp());
  await tester.pumpAndSettle();

  await tester.tap(find.text(title).first);
  await tester.pumpAndSettle();
  expect(find.byType(NoteEditorView), findsOneWidget);
}

void main() {
  testWidgets('编辑正文后自动保存', (tester) async {
    await _openNote(tester, '欢迎使用思迹笔记');

    final titleField = tester.widget<TextField>(
      find.byKey(const Key('editor-title')),
    );
    expect(titleField.controller!.text, '欢迎使用思迹笔记');

    await tester.enterText(
      find.byKey(const Key('editor-body')),
      '# 新的一节\n\n保存测试。',
    );
    await tester.pump();
    expect(find.textContaining('未保存'), findsOneWidget);

    await tester.pump(const Duration(milliseconds: 900));
    await tester.pumpAndSettle();

    expect(find.textContaining('已存本地'), findsOneWidget);
    expect(find.textContaining('字'), findsWidgets);
  });

  testWidgets('预览渲染 Markdown 结构', (tester) async {
    await _openNote(tester, 'Markdown 速查');

    await tester.tap(find.text('预览'));
    await tester.pumpAndSettle();

    expect(find.text('常用语法', findRichText: true), findsOneWidget);
    expect(find.textContaining('加粗', findRichText: true), findsWidgets);
    expect(find.byKey(const Key('editor-body')), findsNothing);
  });

  testWidgets('修改标题后返回，列表同步更新', (tester) async {
    await _openNote(tester, '批注怎么用');

    await tester.enterText(
      find.byKey(const Key('editor-title')),
      '改过的标题',
    );
    await tester.pump(const Duration(milliseconds: 900));
    await tester.pumpAndSettle();

    await tester.tap(find.byTooltip('返回'));
    await tester.pumpAndSettle();

    expect(find.byType(NoteEditorView), findsNothing);
    expect(find.text('改过的标题'), findsWidgets);
  });
}
