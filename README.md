# FBI (Floating Battery Indicator)

>**Current Release:** `1.2.0` **Beta**
**Last Updated:** `2026-08-15`

>>FBI adalah aplikasi Android overlay yang menampilkan informasi baterai di atas aplikasi lain dengan fitur kustomisasi lengkap untuk warna, ukuran, posisi, dan kontrol sentuhan.

---

## ✨ Fitur Utama

- **Battery Info Overlay** — Satu modul "Battery Info" berisi baris info Suhu (°C), Persen (%), Tegangan (V), Arus (mA), dan Daya (W) dalam satu overlay dengan satu posisi; urutan baris bisa diatur (▲/▼) dan tiap baris bisa disembunyikan; warna nilai, label & pemisah terpisah; interval update bisa diatur; pembacaan data dengan fallback dari sticky broadcast, BatteryManager, dan sysfs
- **Panel Battery Info 3 Tab** — Panel pengaturan Battery Info memakai bottom navigation **Monitor | Overlay | Battery Strip**: tab Overlay untuk konfigurasi Battery Info, tab Battery Strip untuk konfigurasi Battery Strip, dan tab Monitor (placeholder monitor baterai, dikerjakan nanti)
- **Battery Strip Overlay** — Bar baterai fleksibel di layar: Mode Cepat (snap ke sisi atas/bawah/kiri/kanan) atau Mode Manual (panjang & posisi bebas), orientasi horizontal/vertikal + invert, warna fill + strip kosong, skema warna level (Tanpa Skema / Klasik 3-warna / Hue Gradien), animasi fade + wave (kedutan gelombang) saat low dan shine + wave saat charging (kecepatan, lebar band, intensitas bisa diatur)
- **Memory Info Overlay** — Modul overlay pemakaian memori proses (Java Heap, Native Heap, Graphics, Total Proses/PSS) via `Debug.getMemoryInfo()`, polling tiap detik dengan skip render bila nilai tidak berubah; panel pengaturan memakai bottom navigation **Monitor | Overlay** (Monitor = monitoring realtime + simpan snapshot, Overlay = konfigurasi)
- **Crash Logger** — Saat force close, stack trace otomatis ditulis ke `FBI_crash_*.txt` di folder Download (plus cadangan prefs) agar bug mudah dilaporkan tanpa logcat/adb
- **Color Wheel & Hue Slider** — Dua mode color picker: HSV color wheel dengan crosshair atau slider Hue/Saturation/Brightness/Alpha. Two-way sync, color name auto-detection, HEX edit manual, saved colors
- **Safe Area** — Batasi posisi overlay agar tidak masuk area notch/cutout
- **Touch Passthrough** — Kunci posisi agar sentuhan tembus ke aplikasi belakang (default ON)
- **Position Control Lengkap** — Slider X/Y, D-Pad, preset posisi, orientasi otomatis per mode layar
- **Preset Full-Konfigurasi (v2)** — Simpan/muat seluruh config overlay (posisi, ukuran, warna, shadow, background, touchPassthrough, safeArea, toggle display spesifik) dengan metadata (tags, favorite, thumbnail, version history). UUID-based storage, backward compatible.
- **Selective Preset Apply** — Opsi apply preset: posisi saja, warna saja, background saja, dll. Merge partial config tanpa timpa pengaturan lain.
- **Preset Search & Tagging** — Cari preset berdasarkan nama atau tag; favorite flag untuk quick access.
- **Preset Share via Intent** — Bagikan preset via native Android share intent (file-based, tidak clipboard).
- **Configurable Background** — Warna, ukuran, offset, margin, radius (independen dari shadow)
- **Configurable Shadow** — Warna, blur, offset X/Y per modul
- **Slider Label Edit** — Klik label slider untuk edit nilai via dialog
- **Screen Orientation Toggle** — Ikon orientasi layar di toolbar, toggle Potret/Lanskap sekali ketuk
- **Dark/Light Theme** — Toggle tema (default malam), tersimpan otomatis
- **Collapsible Panel Sections** — Setiap panel overlay dikelompokkan dalam section collapsible: Tampilan Overlay, Posisi, Shadow, Background. Klik header ▾/▸ untuk toggle
- **Overlay Toggle** — Auto-start, permission handling, WakeLock, foreground service
- **Auto-start saat boot** — Overlay otomatis dimulai kembali saat perangkat dinyalakan
- **Ikon Notifikasi Kontrol** — Notifikasi foreground service dengan aksi toggle overlay, kill service, dan buka aplikasi
- **Ikon Aplikasi Default/Alternatif** — Ganti ikon launcher dari Pengaturan tanpa uninstall
- **Android SplashScreen** — SplashScreen API resmi tanpa fake loading

---

## 📚 Dokumentasi Terkait

| File | Isi |
|------|-----|
| [PANDUAN.md](PANDUAN.md) | Panduan penggunaan lengkap |
| [CHANGELOG.md](CHANGELOG.md) | Riwayat perubahan lengkap |
| [STRUKTUR.md](STRUKTUR.md) | Struktur project lengkap |

---

## 📝 Lisensi & Klarifikasi

Belum ada lisensi resmi yang ditetapkan untuk project ini.

>Sebagian besar pengembangan dibantu AI, sementara pengembang menangani pengujian, penyesuaian implementasi, revisi, dan debugging sambil ngopi.

>>Silakan gunakan, modifikasi, fork, atau kustomisasi sesuai kebutuhan.

---

## 👨‍💻 Author

<mark> Developed by <u>***GaoZhan.***</u> </mark>

Aplikasi overlay baterai Android FBI dengan fokus pada customization, real-time updates, dan lightweight overlay behavior.

---

## 📧 Support

Laporan bug, issue, atau permintaan fitur:
Silakan buat issue atau hubungi pengembang.

>Respons tidak dijamin cepat, karena project ini berkembang mengikuti eksperimen, suasana hati, waktu luang, dan secangkir kopi.

---

## 💻 Development

### Environment

| Item | Detail |
|------|--------|
| Build System | Gradle + AGP |
| Java | Java 17 (source/target) |
| Min SDK | 26 |
| Target SDK | 35 |
| Compile SDK | 35 |
| Namespace | exp.ftxt |
| Application ID | exp.ftxt.fbi |

### 🔢 Versioning

Project ini menggunakan Semantic Versioning: `major.minor.patch`

| Komponen | Naik saat | Reset |
|----------|-----------|-------|
| **major** | milestone besar, arsitektur, breaking change | `minor=0, patch=0` |
| **minor** | fitur baru / fitur dihapus | `patch=0` |
| **patch** | bugfix, optimasi, maintenance | — |

### Section Changelog

| Section | Deskripsi |
|---------|-----------|
| ✨ Fitur Baru | Fitur baru ditambahkan |
| 🚮 Fitur Dihapus | Fitur dihapus/dinonaktifkan |
| ♻️ Perubahan Fitur | Perubahan fitur existing |
| 🔧 Optimasi & Penyesuaian | Optimasi, refactor, maintenance |
| 🐞 Bug Fixes | Perbaikan bug |
| 💡 Catatan | Informasi tambahan |
| 🗒️ File Added | File baru |
| ✏️ File Changed | File diubah |
| 🔥 File Removed | File dihapus |

Format judul entry: `# [x.x.x] - yyyy-mm-dd versionCode xxx` — versionName & versionCode dicatat di judul, tanpa section 🔢 Version.

Entry yang di-merge (beberapa versi digabung dalam satu entry): section 🗒️ File Added, ✏️ File Changed, dan 🔥 File Removed diabaikan (tidak ditulis).

### Dependencies

| Library | Versi | Fungsi |
|---------|-------|--------|
| AndroidX AppCompat | 1.7.1 | UI compatibility |
| Material Design | 1.12.0 | Material 3 components |
| ConstraintLayout | 2.2.1 | Layout |
| Core SplashScreen | 1.0.1 | SplashScreen API |
| RecyclerView | 1.3.2 | Drag & drop animasi |
| GSON | 2.10.1 | JSON serialization |
| JUnit | 4.13.2 | Testing |
| AndroidX Test JUnit | 1.2.1 | Instrumented testing |
| Espresso Core | 3.6.1 | UI testing |

### Architecture

MVC dengan service-based overlay:

- **Model** — Config classes (BatteryStatsConfig, BatteryBarConfig, MemoryConfig, dll), OverlayPreset, SharedPreferences
- **View** — Activity utama + panel controllers (Battery, Battery Strip, Memory) + overlay modules (ShadowTextView)
- **Service** — FloatingService (foreground service + WindowManager)

### Build

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Permission

Dideklarasikan di AndroidManifest.xml:

- `SYSTEM_ALERT_WINDOW` — Izin overlay aplikasi lain
- `FOREGROUND_SERVICE` — Layanan latar depan
- `FOREGROUND_SERVICE_SPECIAL_USE` — Layanan overlay
- `POST_NOTIFICATIONS` — Notifikasi kontrol (API 33+)
- `WAKE_LOCK` — Jaga CPU tetap aktif
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` — Nonaktifkan optimasi baterai
- `PACKAGE_USAGE_STATS` — Akses statistik penggunaan aplikasi
- `RECEIVE_BOOT_COMPLETED` — Auto-start overlay saat boot

Izin diminta otomatis saat pertama aplikasi dibuka. Pengguna juga bisa mengelola izin melalui menu **Konfigurasi**.

---

## 📁 Struktur Project

Lihat [STRUKTUR.md](STRUKTUR.md) untuk struktur project lengkap beserta statistik.
