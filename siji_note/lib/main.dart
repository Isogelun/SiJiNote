import 'package:material_ui/material_ui.dart';

import 'data/note_repository.dart';
import 'data/sqlite_note_repository.dart';
import 'features/home/home_page.dart';
import 'theme/app_theme.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final repository = await SqliteNoteRepository.open();
  runApp(SiJiNoteApp(repository: repository));
}

class SiJiNoteApp extends StatelessWidget {
  const SiJiNoteApp({super.key, required this.repository});

  final NoteRepository repository;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '思迹笔记',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light,
      home: HomePage(repository: repository),
    );
  }
}
