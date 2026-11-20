import 'package:flutter_test/flutter_test.dart';
import 'package:material_ui/material_ui.dart';

import 'package:siji_note/theme/app_theme.dart';
import 'package:siji_note/ui/components/components.dart';

void main() {
  testWidgets('基础组件可渲染且无溢出', (WidgetTester tester) async {
    tester.view.physicalSize = const Size(1000, 1400);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.reset);

    await tester.pumpWidget(
      MaterialApp(
        theme: AppTheme.light,
        home: Scaffold(
          body: SingleChildScrollView(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                for (final variant in SijiButtonVariant.values)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: SijiButton(
                      label: variant.name,
                      variant: variant,
                      onPressed: () {},
                    ),
                  ),
                SijiButton(label: '正在创建…', loading: true, onPressed: () {}),
                const SizedBox(height: 8),
                const SijiButton(label: '暂不可用', onPressed: null),
                const SizedBox(height: 16),
                const SijiTextField(hint: '默认 · 输入笔记本名称'),
                const SizedBox(height: 12),
                const SijiTextField(
                  hint: '错误',
                  errorText: '名称已存在，请换一个名称。',
                ),
                const SizedBox(height: 12),
                const SijiTextField(hint: '用户 ID · 注册后不可修改', enabled: false),
                const SizedBox(height: 12),
                const SijiTextField(hint: '密码', obscureText: true),
                const SizedBox(height: 16),
                Row(
                  children: [
                    SijiCheckbox(value: true, onChanged: (_) {}),
                    const SizedBox(width: 20),
                    SijiCheckbox(value: false, onChanged: (_) {}),
                    const SizedBox(width: 20),
                    SijiSwitch(value: true, onChanged: (_) {}),
                    const SizedBox(width: 12),
                    SijiSwitch(value: false, onChanged: (_) {}),
                  ],
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: 220,
                  child: SijiSegmentedControl(
                    segments: const ['编辑', '预览'],
                    selectedIndex: 0,
                    onChanged: (_) {},
                  ),
                ),
                const SizedBox(height: 16),
                const SijiTag(label: '引导库'),
                const SizedBox(height: 8),
                const SijiStatusPill(
                  status: SijiSaveStatus.saved,
                  label: '已保存 · 刚刚',
                ),
              ],
            ),
          ),
        ),
      ),
    );

    expect(tester.takeException(), isNull);
    expect(find.text('编辑'), findsOneWidget);
  });
}
