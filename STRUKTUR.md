# 📁 Struktur Project FBI

```
FBI/
├── .gitignore                    — Git ignore rules (build, gradle, local)
├── AGENTS.md                     — Pedoman AI agent
├── CHANGELOG.md                  — Riwayat perubahan per release
├── PANDUAN.md                    — Panduan penggunaan lengkap
├── README.md                     — Ringkasan project & fitur
├── STRUKTUR.md                   — Struktur project (file ini)
├── build.gradle                  — Root Gradle
├── gradle.properties             — Gradle: AndroidX, JVM args
├── gradlew / gradlew.bat         — Gradle wrapper scripts
├── settings.gradle               — Settings Gradle (include :app)
├── local.properties              — Local SDK/NDK path
│
├── gradle/wrapper/
│   └── gradle-wrapper.properties — Versi Gradle wrapper
│
└── app/
    ├── build.gradle              — Module: minSdk 26, compileSdk/targetSdk 35, Java 17
    ├── proguard-rules.pro        — Aturan ProGuard (custom rules)
    │
    ├── src/main/
    │   ├── AndroidManifest.xml   — Permission: overlay, notif, baterai, wake lock, boot
    │   │
    │   ├── java/exp/ftxt/
    │   │   ├── core/
    │   │   │   ├── FloatingService.java     — Foreground service: kelola semua overlay via WindowManager
    │   │   │   ├── NotificationHelper.java  — Notifikasi foreground service (custom RemoteViews + ikon dinamis)
    │   │   │   ├── NotificationActionReceiver.java — Handle aksi notifikasi (toggle, kill, open)
    │   │   │   ├── WakeLockManager.java     — Partial wake lock biar CPU tetap aktif
    │   │   │   └── BootReceiver.java        — Auto-start overlay saat boot
    │   │   │
    │   │   ├── features/
    │   │   │   ├── battery_stats/
    │   │   │   │   ├── BatteryStatsConfig.java   — Konfigurasi statis Battery Info overlay (termasuk show V/mA/W & itemOrder)
    │   │   │   │   └── BatteryStatsModule.java   — Suhu °C, persen %, tegangan/arus/daya; baca broadcast + fallback (API 28+, sysfs), render sesuai urutan item
    │   │   │   ├── battery_bar/
    │   │   │   │   ├── BatteryBarConfig.java         — Konfigurasi Battery Strip overlay
    │   │   │   │   ├── BatteryBarView.java           — Custom View bar baterai H/V (empty strip, fade, shine, wave)
    │   │   │   │   └── BatteryBarModule.java         — Bar baterai: mode cepat (snap sisi) & manual, update interval
    │   │   │   └── color_picker/
    │   │   │       └── TriangleColorPickerView.java — Custom View segitiga HSV untuk Color Picker
    │   │   │
    │   │   ├── shared/
    │   │   │   ├── color/
    │   │   │   │   ├── ColorMath.java         — Operasi matematika HSV: gradient, angle, selector posisi
    │   │   │   │   ├── ColorNameResolver.java — Deteksi nama warna dari RGB
    │   │   │   │   └── HSVColorPickerView.java— Custom View: color wheel HSV + crosshair
    │   │   │   ├── preset/
    │   │   │   │   ├── OverlayPreset.java     — Model data preset dengan UUID, metadata, history
    │   │   │   │   ├── PresetManager.java     — CRUD preset: save/load/rename/reorder/search/export/import/share
    │   │   │   │   ├── PresetHandler.java     — Delegate pattern: save/load dialog infrastructure per modul
    │   │   │   │   └── PresetBrowserDialog.java   — DialogFragment browser preset dengan search & filter
    │   │   │   └── ui/
    │   │   │       ├── BackgroundConfig.java      — Model data background (enable, color, padding, offset, margin, radius)
    │   │   │       ├── ShadowConfig.java          — Model data shadow (enable, color, blur, offset)
    │   │   │       ├── ShadowTextView.java        — Custom TextView dengan shadow + background di onDraw()
    │   │   │       ├── OverlayDragHandler.java    — Touch listener untuk drag overlay
    │   │   │       ├── OverlayModule.java         — Interface untuk menyeragamkan method semua modul overlay
    │   │   │       ├── OverlayShadow.java         — Apply elevation-based shadow ke overlay
    │   │   │       ├── ColorPickerDialog.java     — Dialog color picker: wheel, sliders, HEX/ARGB
    │   │   │       ├── DpadController.java        — Kontrol D-Pad dengan repeat untuk fine position
    │   │   │       ├── SliderPositionController.java  — Slider X/Y posisi normalized 0-1000
    │   │   │       ├── SliderLabelEditor.java     — Dialog edit nilai numerik dari label slider
    │   │   │       └── SectionHelper.java         — Utility collapsible section toggle ▸/▾
    │   │   │
    │   │   ├── ui/
    │   │   │   ├── BatteryPanelController.java         — UI panel Battery: toggle °C/%/V/mA/W, urutan info (drag & drop), size, color
    │   │   │   ├── BatteryOrderAdapter.java            — RecyclerView adapter urutan info (checkbox tampil + drag handle ≡) untuk Battery Info
    │   │   │   ├── BatteryPositionController.java      — Kontrol posisi Battery Info
    │   │   │   ├── BatteryBarPanelController.java          — UI panel Battery Strip: quick/manual mode, warna, shadow
    │   │   │   ├── BatteryBarPositionController.java       — Kontrol posisi Battery Strip
    │   │   │   ├── BasePanelFragment.java                  — Abstract base Fragment untuk semua panel
    │   │   │   ├── PanelManager.java                       — Kelola show/hide Fragment panel
    │   │   │   └── fragment/
    │   │   │       ├── BatteryPanelFragment.java           — Fragment Battery Info
    │   │   │       └── BatteryBarPanelFragment.java        — Fragment Battery Strip
    │   │   │
    │   │   ├── utils/
    │   │   │   └── PermissionHelper.java    — Helper izin: overlay, notifikasi, optimasi baterai
    │   │   │
    │   │   ├── MainActivity.java           — Activity utama: toolbar, nav drawer, panel system, theme toggle
    │   │   └── SettingsActivity.java        — Konfigurasi: izin overlay, notifikasi, baterai, ikon aplikasi
    │   │
    │   └── res/
    │       ├── anim/
    │       │   ├── settings_popup_enter.xml — Animasi masuk popup settings
    │       │   └── settings_popup_exit.xml  — Animasi keluar popup settings
    │       ├── drawable/
    │       │   ├── ic_arrow_down.xml        — Ikon panah bawah untuk D-Pad
    │       │   ├── ic_arrow_left.xml        — Ikon panah kiri untuk D-Pad
    │       │   ├── ic_arrow_right.xml       — Ikon panah kanan untuk D-Pad
    │       │   ├── ic_arrow_up.xml          — Ikon panah atas untuk D-Pad
    │       │   ├── ic_close.xml             — Ikon close/X untuk Kill Service
    │       │   ├── ic_edit.xml              — Ikon pensil untuk edit HEX/nilai
    │       │   ├── ic_exit.xml              — Ikon exit/keluar untuk tombol Keluar
    │       │   ├── ic_launcher_bg.xml       — Background adaptive icon (gradient biru)
    │       │   ├── ic_launcher_bg_alt.xml   — Background adaptive icon alternatif (gradient hijau)
    │       │   ├── ic_launcher_foreground.xml — Foreground adaptive icon (vector baterai)
    │       │   ├── ic_launcher_foreground_alt.xml — Foreground ikon alternatif (vector baterai)
    │       │   ├── ic_notification_invisible.xml — Ikon mata tertutup untuk toggle hide
    │       │   ├── ic_notification_open.xml — Ikon buka aplikasi untuk notifikasi
    │       │   ├── ic_notification_toggle.xml — Ikon toggle untuk notifikasi
    │       │   ├── ic_notification_visible.xml — Ikon mata terbuka untuk toggle show
    │       │   ├── ic_screen_rotation.xml   — Ikon orientasi layar (toolbar)
    │       │   ├── ic_settings.xml          — Ikon gear untuk settings
    │       │   ├── ic_star_filled.xml       — Ikon bintang solid (favorit)
    │       │   ├── ic_star_outline.xml      — Ikon bintang outline (non-favorit)
    │       │   ├── ic_sun.xml               — Ikon matahari untuk tema terang
    │       │   ├── ic_theme.xml             — Ikon tema gelap/terang
    │       │   ├── seekbar_thumb.xml        — Thumb slider lingkaran 12×12dp
    │       │   ├── vertical_divider.xml     — Divider vertikal untuk bottom bar preset
    │       │   ├── drawer_bg.xml            — Drawable wrapper drawer bg terang
    │       │   ├── drawer_header_bg.xml     — Drawable wrapper header drawer terang
    │       │   ├── toolbar_bg.xml           — Drawable wrapper toolbar bg terang
    │       │   ├── main_bg.xml              — Drawable wrapper main bg terang
    │       │   ├── appbar_light.png         — Background toolbar tema terang
    │       │   ├── bg_alt_light2.png        — Background drawer tema terang (varian 2)
    │       │   ├── bg_main_light2.png       — Background layar utama tema terang (varian 2)
    │       │   └── drawbar_light.png        — Background header drawer tema terang
    │       ├── drawable-night/
    │       │   ├── drawer_bg.xml            — Drawable wrapper drawer bg gelap (flip 180°)
    │       │   ├── drawer_header_bg.xml     — Drawable wrapper header drawer gelap
    │       │   ├── toolbar_bg.xml           — Drawable wrapper toolbar bg gelap
    │       │   └── main_bg.xml              — Drawable wrapper main bg gelap
    │       ├── layout/
    │       │   ├── activity_main.xml            — Layout utama dengan DrawerLayout + CoordinatorLayout
    │       │   ├── activity_settings.xml        — Layout halaman Konfigurasi izin
    │       │   ├── dialog_color_picker.xml      — Dialog color picker gabungan wheel + sliders
    │       │   ├── dialog_preset_browser.xml    — Dialog browser preset dengan search & list
    │       │   ├── drawer_content.xml           — Konten navigation drawer: RecyclerView item list
    │       │   ├── item_battery_order.xml      — Item baris urutan info Battery Info (checkbox tampil + label + drag handle ≡)
    │       │   ├── nav_header.xml               — Header navigation drawer: logo + versi
    │       │   ├── notification_custom.xml      — Custom notification layout (RemoteViews + ImageButton)
    │       │   ├── panel_battery.xml            — Panel konfigurasi Battery Info (Tampilan Overlay, posisi, shadow, bg)
    │       │   ├── panel_battery_bar.xml        — Panel konfigurasi Battery Strip
    │       │   └── preset_browser_item.xml      — Item layout untuk daftar preset
    │       ├── menu/
    │       │   └── main_menu.xml     — Menu toolbar: theme, orientation, settings
    │       ├── mipmap-anydpi-v26/
    │       │   ├── ic_launcher.xml   — Adaptive icon launcher (default)
    │       │   └── ic_launcher_alt.xml   — Adaptive icon launcher (alternatif)
    │       ├── values/
    │       │   ├── colors.xml        — Warna: primary, accent, drawer, bg
    │       │   ├── strings.xml       — Semua string UI Bahasa Indonesia
    │       │   ├── styles.xml        — Style: AppTheme, popup animation, popup menu
    │       │   ├── themes.xml        — Theme: SplashScreen
    │       │   └── ids.xml           — ID tetap `R.id.nav*` untuk drawer
    │       ├── values-night/
    │       │   └── colors.xml        — Warna mode gelap: drawer bg, drawer header
    │       └── values-v31/
    │           └── themes.xml        — SplashScreen theme untuk API 31+
    │
    ├── src/test/java/exp/ftxt/
    │   └── ExampleUnitTest.java      — Contoh unit test (JVM)
    │
    └── src/androidTest/java/exp/ftxt/
        └── ExampleInstrumentedTest.java  — Contoh instrumented test (Android)
```

## Statistik Project

| Kategori | Jumlah |
|----------|-------:|
| Java source | 40 |
| Java test | 2 |
| Layout XML | 10 |
| Drawable XML | 31 |
| Drawable PNG | 0 |
| Values XML | 7 |
| Mipmap XML | 2 |
| Menu XML | 1 |
| Anim XML | 2 |
| XML lainnya (Manifest) | 1 |
| Assets (md) | 0 |
| Root dokumen | 6 |
| Root konfigurasi | 5 |
| Gradle & wrapper | 4 |
| CI/CD | 0 |
| **Total file** | **~111** |
| **Total direktori** | **~43** |
