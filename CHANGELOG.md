# CHANGELOG

## [1.1.0] - 2026-08-15 versionCode 2 ***ONGOING***

✨ Fitur Baru
- Info Memori di halaman Konfigurasi: menampilkan pemakaian memori proses (Java Heap/Dalvik, Native Heap, Graphics, Total Proses/PSS) yang diperbarui otomatis setiap detik selama halaman terbuka — dibaca via `Debug.getMemoryInfo()`, tanpa izin tambahan.
- Tombol "Simpan Snapshot ke Download": menulis snapshot memori saat itu (dengan waktu) ke file teks `FBI_memori_<timestamp>.txt` di folder Download — via MediaStore untuk API 29+, fallback tulis langsung untuk API 26-28.

🐞 Bug Fixes
- Build gagal (`build_error.txt`): `Debug.MemoryInfo` tidak punya field publik `graphicsPss`/`totalPss` — diganti `getMemoryStat("summary.graphics")` (di-parse) untuk Graphics dan `getTotalPss()` untuk Total Proses.

💡 Catatan
- Fitur eksperimen untuk pemantauan/laporan/diagnosa RAM (tindak lanjut ANALISIS_RAM.md); tidak menyentuh modul overlay.
- Pengujian runtime di perangkat/emulator dilakukan user.

✏️ File Changed
- app/src/main/java/exp/ftxt/SettingsActivity.java
- app/src/main/res/layout/activity_settings.xml
- app/build.gradle
- CHANGELOG.md

## [1.0.0] - 2026-08-14 versionCode 1

✨ Fitur Baru
- Project FBI lahir sebagai aplikasi overlay mandiri berisi modul baterai hasil duplikasi dari FTxT.
- Battery Stats: satu overlay berisi baris info suhu (°C), persen (%), tegangan (V), arus (mA), dan daya (W) dengan pengaturan warna, shadow, background, posisi, touch passthrough, safe area.
- Urutan Info: urutan baris info (Suhu, Persen, Tegangan, Arus, Daya) bisa diatur via drag & drop (seret ikon ≡ pada tiap baris); tiap baris bisa disembunyikan via toggle °C/%/V/mA/W.
- Battery Bar: bar persentase baterai (mode quick menempel di sisi layar / mode bebas) dengan efek fade, shine, wave, chargeWave, dan skema warna Classic/Hue.
- Panel pengaturan sidebar untuk 2 modul baterai (Battery Stats, Battery Bar).
- Sistem preset, color picker (HSV + Triangle), drag & posisi (slider/d-pad), shadow & background config.
- Foreground service (FloatingService) + notifikasi kontrol overlay + auto-start saat boot.
- SettingsActivity (izin overlay, notifikasi, optimasi baterai, ikon aplikasi).

🚮 Fitur Dihapus
- Modul & panel Battery Current — digabung ke dalam Battery Stats (satu posisi, satu set konfigurasi).

♻️ Perubahan Fitur
- Battery Current melebur ke Battery Stats: tegangan/arus/daya menjadi bagian dari modul Battery Stats dengan satu posisi dan satu set ukuran/warna/shadow/background.
- Nama aplikasi diganti menjadi FBI (label aplikasi, judul/channel notifikasi, header sidebar, path ekspor preset).
- Navigasi sidebar jadi 2 modul (Battery Stats, Battery Bar).
- Urutan Info kini memakai drag & drop (RecyclerView + ItemTouchHelper, seret ikon ≡) menggantikan tombol ▲/▼ — urutan disimpan ke `battery_item_order` dan diterapkan saat drop (drag berhenti).
- Nama modul overlay diubah agar perbedaannya lebih mencolok: **Battery Stats → Battery Info**, **Battery Bar → Battery Strip** — diterapkan di sidebar (drawer), judul toolbar, switch panel, dialog preset, dan dokumen. Label sidebar lama di prefs dinormalisasi otomatis berdasarkan id menu.
- Panel Battery Info direstrukturisasi: section 'Tampilan' lama dihapus (chevron tidak lagi diperlukan); seluruh isinya (Sembunyikan Label, Update interval, Ukuran Teks, preview warna) digabung ke section 'Urutan Info' yang dipindah ke posisi teratas dan berganti nama menjadi **'Tampilan Overlay'**.
- Checkbox tampil °C/%/V/mA/W dipindah dari section 'Tampilan' ke dalam tiap baris daftar urutan info — setiap baris kini punya checkbox show/hide di sebelah label (item_battery_order.xml, logika di BatteryOrderAdapter).
- Urutan Info Battery Info diubah menjadi **chip drag dua zona** 'Aktif'/'Nonaktif' (BatteryOrderZonesView): chip diseret untuk mengubah urutan, digeser antar zona untuk menampilkan/menyembunyikan baris — menggantikan daftar drag & drop RecyclerView (item_battery_order.xml + BatteryOrderAdapter yang dihapus).
- `itemOrder` kini ikut tersimpan/termuat saat simpan/muat preset Battery Info (BatteryPositionController).

🔧 Optimasi & Penyesuaian
- Semua PNG background UI (main, toolbar, drawer, header) di drawable & drawable-night diganti shape gradient agar hemat memori bitmap (hemat ±13–25 MB).
- Launcher icon (default & alternatif) diganti adaptive icon berbasis vector + gradient (bentuk baterai) — menghapus bitmap 1024×1024.
- Warna header drawer (nav_header) digelapkan di tema terang (#ECEFF1/#CFD8DC → #9E9E9E/#757575) dan gelap (#37474F/#263238 → #1A1A1A/#0E0E0E) agar kontras dengan background panel drawer.
- Warna appbar (toolbar_bg) digelapkan di tema terang (#FFFFFF/#EFEFEF → #455A64/#37474F) dan gelap (#2A2A2A/#1C1C1C → #212121/#111111) agar kontras dengan panel utama; judul & ikon toolbar (hamburger, menu) diset warna putih agar tetap terbaca.
- Package source dikembalikan dari `f.bat` ke `exp.ftxt` (namespace `exp.ftxt`) — seluruh folder source dipindah `java/f/bat/` → `java/exp/ftxt/` agar kode siap diambil FTxT tanpa perubahan lagi; `applicationId` FBI dibedakan menjadi `exp.ftxt.fbi` agar tidak menimpa FTxT (`exp.ftxt`) saat diinstall berdampingan.
- Chip urutan info di Battery Info (BatteryOrderZonesView) diperbesar: ukuran teks 13sp → 15sp dan padding 10/4/10/4dp → 14/8/14/8dp (chip asli & drag ghost) agar lebih mudah disentuh/di-drag.
- NotificationHelper dioptimasi untuk RAM (rekomendasi ANALISIS_RAM #2): `startIconCycling()` tidak lagi membuat Bitmap/Canvas/RemoteViews/PendingIntent baru setiap 10 detik — bitmap ikon suhu di-cache (dibuat ulang hanya saat nilai berubah), RemoteViews + onClick PendingIntent dibuat sekali via `ensureCachedViews()` (dipakai buildNotification & buildNotificationDynamic), IntentFilter sticky baterai di-cache; alokasi sampah per siklus berkurang drastis.
- WakeLock dioptimasi untuk RAM (rekomendasi ANALISIS_RAM #4): WakeLock PARTIAL hanya dipegang saat layar menyala — `WakeLockManager.acquire()` batal jika layar mati (`PowerManager.isInteractive()`), dan FloatingService mendengar `ACTION_SCREEN_ON`/`ACTION_SCREEN_OFF` untuk melepas WakeLock saat layar mati dan mengakuisisi lagi saat layar nyala (selama modul aktif). Proses bisa idle saat layar mati sehingga memori bisa dipadatkan sistem.
- Pembacaan baterai dihemat (rekomendasi ANALISIS_RAM #3): BatteryBarModule menggabung 2 `registerReceiver` per tick (percent + charging) menjadi 1 pembacaan status (`readBatteryStatus`), dan `IntentFilter` ACTION_BATTERY_CHANGED di-cache static di BatteryBarModule & BatteryStatsModule (tidak dibuat baru tiap pembacaan).
- Animasi BatteryBarView dihemat (rekomendasi ANALISIS_RAM #5): animator infinite (fade/shine/wave/chargeWave) dihentikan saat overlay disembunyikan (`onVisibilityChanged` visibility GONE) dan dimulai lagi saat ditampilkan — tidak ada `invalidate()` ±60fps saat overlay tidak terlihat.
- Alokasi render overlay dihemat (rekomendasi ANALISIS_RAM #1): `updateDisplay()` di BatteryStatsModule & BatteryBarModule kini skip bila nilai tidak berubah — BatteryStats cache teks terakhir (`lastRenderedText`, di-reset saat warna/label/separator berubah), BatteryBar cache status terakhir (percent/charging/low); `SpannableString` + `ForegroundColorSpan` per karakter, `setText`, dan `invalidate()` hanya dibuat saat teks/status benar-benar berubah.
- Pembacaan baterai dihemat lebih jauh: BatteryBarModule mengganti `registerReceiver(null, ...)` per tick dengan satu `BroadcastReceiver` permanen yang didaftarkan di `start()` dan dilepas di `stop()` — nilai baterai di-cache ke field dan tetap diperbarui oleh sticky broadcast ACTION_BATTERY_CHANGED; tick interval tidak lagi memicu alokasi receiver/intent baru.
- Siklus notifikasi 10 detik dihemat (rekomendasi ANALISIS_RAM #2 lanjutan): `startIconCycling()` skip `notify()` bila suhu & ikon toggle tidak berubah dari siklus sebelumnya (`lastNotifiedKey`) — `Notification` baru hanya dibangun saat nilai berubah.
- `FloatingService.restartModule()` (stop+start = removeView+addView) tidak lagi dipanggil oleh panel — diganti update in-place: helper baru `updateBatteryBarInPlace()` (applyAppearance + reloadLayout + updatePosition) dan `updateBatteryStatsInPlace()` (via `refreshDisplay()` baru di BatteryStatsModule). Diterapkan di `BatteryBarPositionController.syncToService`, `BatteryBarPanelController.restart()` (semua pengaturan bar), dan `BatteryPanelController` (urutan info & interval) — overlay tidak di-rebuild saat setting diubah, mengurangi churn window/surface.

🐞 Bug Fixes
- Force close saat membuka Color Picker dialog (dialog_color_picker.xml): tag class `TriangleColorPickerView` tidak konsisten dengan package project — disamakan menjadi `exp.ftxt.features.color_picker.*`.
- Chip urutan info tidak bisa di-drag: NestedScrollView panel meng-intercept gerakan sentuhan (requestDisallowInterceptTouchEvent hanya dipanggil di parent langsung chip). Perbaiki dengan meneruskan requestDisallowIntercept ke seluruh rantai parent saat chip disentuh, dan chip tiruan (drag ghost) kini mengikuti jari — sebelumnya posisinya melompat ke pojok root.
- Force close saat mulai drag chip (`labelFor` → NPE): `dragId` bisa bernilai null karena di-reset di `finishDrag`/`cancelDrag` pada sentuhan beruntun/multi-touch, lalu MOVE berikutnya memicu `startDrag` dengan `dragId` null. Perbaiki dengan menetapkan `dragId` dari `chip.getTag()` saat drag dimulai, guard `applyTarget` jika `dragId` null, dan abaikan ACTION_DOWN (jari kedua) saat sedang dragging.
- Force close drag chip masih terjadi (logcat.txt, `toContainer.addView`): `applyTarget` memindahkan view chip secara fisik (removeView/addView) pada setiap MOVE, dan pada sentuhan beruntun/multi-touch chip stale (sudah dibuang `renderZones`) bisa membuat operasi view ini crash. Perbaiki dengan mengubah `applyTarget` hanya memperbarui data list urutan (activeIds/inactiveIds), tidak menyentuh view hierarchy; hasil tampil setelah drag selesai via `renderZones`. Index chip saat drop dikoreksi (hitung jumlah chip yang dilewati, bukan `i+1`, karena chip yang di-drag di-skip). Ditambah guard defensif: `labelFor` null-safe, `startDrag` batal jika `chip.getParent()` null, `updateDrag` batal jika `dragView` null.

💡 Catatan
- Duplikasi kode dari FTxT working tree, bukan pemindahan — FTxT tidak disentuh.
- Package di-rename `exp.ftxt.*` → `f.bat.*` saat duplikasi awal, lalu dikembalikan ke `exp.ftxt.*` (lihat 🔧). Key prefs dipertahankan (`ftxt_prefs`/`ftxt_presets`); karena `applicationId` kini `exp.ftxt.fbi`, data prefs lama dari build `f.bat` di perangkat tidak terbawa.
- Migrasi prefs penggabungan: `batcur_*` dipindahkan sekali ke `battery_*` via flag `battery_merged_v1` (enabled, show V/mA/W, update interval, posisi). Key `batcur_*` lama tidak dihapus.
- Urutan baris info disimpan di prefs `battery_item_order` (default `temp,pct,volt,cur,power`).
- Modul non-baterai FTxT (floating text, fps, network, clock, dll) tidak disalin.
- SliderLabelEditor ikut disalin (ternyata dipakai panel baterai, tidak hanya FPS/Text seperti perkiraan awal).
- Drawable `ic_edit`, `seekbar_thumb`, `vertical_divider` ikut disalin (dependensi dialog color picker & preset browser).
- Drawable `bg_segment_*` tidak disalin karena sudah dihapus di working tree FTxT dan tidak lagi direferensikan.
- Uji runtime overlay wajib dilakukan di perangkat/emulator oleh user.

🗒️ File Added
- ANALISIS_RAM.md
- app/src/main/res/drawable/ic_launcher_bg.xml, ic_launcher_bg_alt.xml, ic_launcher_foreground.xml, ic_launcher_foreground_alt.xml
- PERENCANAAN.md
- app/src/main/java/exp/ftxt/ (semua isi folder: 40 file Java hasil salin dari exp/ftxt + SliderLabelEditor)
- app/src/main/java/exp/ftxt/ui/BatteryOrderAdapter.java
- app/src/main/res/layout/item_battery_order.xml
- app/src/main/res/ (semua isi folder: layout, menu, drawable, values, anim, mipmap)
- app/src/main/AndroidManifest.xml, app/build.gradle, CHANGELOG.md
- app/src/main/java/exp/ftxt/ui/BatteryOrderZonesView.java

✏️ File Changed
- app/src/main/java/exp/ftxt/features/battery_stats/BatteryStatsConfig.java, BatteryStatsModule.java
- app/src/main/java/exp/ftxt/MainActivity.java
- app/src/main/java/exp/ftxt/core/FloatingService.java, NotificationHelper.java, BootReceiver.java
- app/src/main/java/exp/ftxt/ui/BatteryPanelController.java, BatteryPositionController.java, PanelManager.java
- app/src/main/java/exp/ftxt/shared/preset/OverlayPreset.java
- app/src/main/res/layout/panel_battery.xml
- app/src/main/res/values/ids.xml, strings.xml
- app/src/main/res/layout/panel_battery_bar.xml
- app/src/main/java/exp/ftxt/ui/BatteryBarPositionController.java
- app/src/main/java/exp/ftxt/ui/BatteryOrderZonesView.java
- app/src/main/res/drawable/main_bg.xml, toolbar_bg.xml, drawer_bg.xml, drawer_header_bg.xml
- app/src/main/res/drawable-night/main_bg.xml, toolbar_bg.xml, drawer_bg.xml, drawer_header_bg.xml
- app/src/main/res/mipmap-anydpi-v26/ic_launcher_alt.xml
- README.md, STRUKTUR.md, PANDUAN.md, CHANGELOG.md
- app/src/main/java/exp/ftxt/ui/BatteryOrderZonesView.java
- app/src/main/java/exp/ftxt/core/WakeLockManager.java
- app/src/main/java/exp/ftxt/features/battery_bar/BatteryBarModule.java, BatteryBarView.java
- app/src/main/java/exp/ftxt/ui/BatteryBarPanelController.java

🔥 File Removed
- app/src/main/res/drawable/appbar_light.png, bg_alt_light2.png, bg_main_light2.png, drawbar_light.png
- app/src/main/res/drawable/ic_launcher_bg.png, ic_launcher_foreground.png, ic_launcher_foreground_alt.png
- app/src/main/res/drawable-night/appbar_dark.png, bg_alt.png, bg_main_dark.png, drawbar_dark.png
- app/src/main/java/exp/ftxt/features/battery_current/BatteryCurrentConfig.java
- app/src/main/java/exp/ftxt/features/battery_current/BatteryCurrentModule.java
- app/src/main/java/exp/ftxt/ui/BatteryCurrentPanelController.java
- app/src/main/java/exp/ftxt/ui/BatteryCurrentPositionController.java
- app/src/main/java/exp/ftxt/ui/fragment/BatteryCurrentPanelFragment.java
- app/src/main/res/layout/panel_battery_current.xml
- app/src/main/res/layout/item_battery_order.xml
- app/src/main/java/exp/ftxt/ui/BatteryOrderAdapter.java
