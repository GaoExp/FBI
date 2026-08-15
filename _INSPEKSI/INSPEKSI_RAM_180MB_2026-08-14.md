# Laporan Inspeksi: Penyebab Penggunaan RAM ±180 MB (FBI & FTxT)

**Tanggal inspeksi:** 2026-08-14
**Status:** Analisa statis dari source code (FBI `exp.ftxt.fbi` v1.0.0 & FTxT working tree). Tanpa akses perangkat/`dumpsys`.

---

## 1. Ringkasan Eksekutif

- **Tidak ditemukan satu "biang besar" tunggal** (seperti bitmap 50 MB, WebView, software layer, atau leak jelas) yang bisa menjelaskan 180 MB.
- Gejala "FBI (2 modul, tanpa PNG) ≈ FTxT (7 modul, dengan PNG) ≈ 180–200 MB" menunjukkan biang utamanya **bukan jumlah modul / bukan PNG**, melainkan hal-hal yang **sama-sama dimiliki kedua aplikasi**:
  1. Baseline proses Android modern (ART + UI + RenderThread/GPU) yang sudah ±80–120 MB begitu aplikasi jalan.
  2. **Heap Java yang membengkak karena alokasi periodik terus-menerus dan tidak pernah dikembalikan ke OS** (ART jarang mengecilkan heap setelah naik).
  3. Setiap modul overlay = 1 window surface + 1 handler + siklus polling — di FTxT ada 7 sekaligus.
- Kesimpulan besar: **optimasi RAM yang sudah dilakukan (PNG dihapus, notifikasi di-cache, WakeLock hemat, animasi dihentikan saat hidden) benar dan berharga, tetapi tidak menyentuh biang dominan**, sehingga angka 180 MB tidak banyak berubah.

---

## 2. Fakta yang Diverifikasi dari Source Code

### 2.1 FBI sudah bersih dari aset besar
- Semua PNG background & launcher sudah dihapus, diganti shape gradient + adaptive icon vector (CHANGELOG v1.0.0).
- `find res -type f | grep -v xml` di FBI → **tidak ada PNG sama sekali**.

### 2.2 FTxT MASIH memakai PNG besar (diterapkan via wrapper drawable)
Layout masih memuat 4 PNG per tema. Dimensi (dibaca dari header PNG):

| File (tema terang) | Dimensi | Estimasi bitmap ARGB_8888 |
|---|---|---|
| `bg_alt_light2.png` | 1024×1024 | ±4,2 MB |
| `bg_main_light2.png` | 541×792 | ±1,7 MB |
| `appbar_light.png` | 1000×395 | ±1,6 MB |
| `drawbar_light.png` | 1000×395 | ±1,6 MB |
| **Total tema terang** | | **±9,1 MB** |

Tema gelap (`bg_alt`, `bg_main_dark`, `appbar_dark`, `drawbar_dark`) juga ±9,1 MB.
Wrapper yang memakai: `drawable/main_bg.xml`, `toolbar_bg.xml`, `drawer_bg.xml`, `drawer_header_bg.xml` + versi `-night`.

**Konsekuensi:** selisih bitmap FBI vs FTxT hanya ±9 MB. Kalau selisih RAM nyata hanya 0–20 MB, berarti PNG **bukan** penjelas utama 180 MB — tetapi tetap layak dibersihkan (FTxT).

### 2.3 Pola alokasi periodik (berulang) — biang pertumbuhan heap
Semua ini membuat objek baru berulang kali; ART tidak mengembalikan memori ke OS setelah heap naik.

1. **BatteryStatsModule.updateDisplay()** (`FBI + FTxT`)
   - Setiap interval (default 5 detik): `registerReceiver(null, ACTION_BATTERY_CHANGED)` → `StringBuilder` → `SpannableString` → **`new ForegroundColorSpan` per karakter label** → `setText`.
   - Sumber kode: `BatteryStatsModule.java:268–297, 316–389`.

2. **BatteryBarModule** (`FBI + FTxT`, interval default 1 detik)
   - `updateDisplay()` → `readBatteryStatus()` → `context.registerReceiver(null, ...)` → `updateStatus()` → `invalidate()`.
   - FBI sudah menggabung 2 `registerReceiver` jadi 1 & me-cache `IntentFilter` — bagus, tapi **masih memanggil registerReceiver per tick**.

3. **NotificationHelper.startIconCycling()** — setiap 10 detik
   - FBI: bitmap ikon sudah di-cache + RemoteViews di-cache, tetapi masih membangun `Notification` + `Icon` baru per siklus.
   - FTxT: **masih versi lama** — tiap 10 detik membuat `Bitmap.createBitmap(192×192)` + `Canvas` + `RemoteViews` + 3 `PendingIntent` + `IntentFilter` baru **tanpa `recycle()`**. Ini biang alokasi sampah yang paling jelas di FTxT.

4. **FTxT FpsModule** — `Choreographer.postFrameCallback` per frame (60 fps) selama berjalan, plus `SpannableString` per update. Penyedot CPU/GPU/RenderThread.

5. **FTxT NetworkModule** — per tick: `TrafficStats.getTotalRxBytes()` + `formatSpeed` + `SpannableString`.

6. **FTxT ClockModule** — `setText` per detik.

7. **WakeLockManager** — FTxT: PARTIAL 24/7 (diperbarui 4 menit); FBI: sudah hanya saat layar menyala. WakeLock sendiri tidak memakan RAM, tapi menjaga proses tidak pernah idle sehingga memori tidak bisa dipadatkan sistem (berkontribusi pada angka yang tampak "stuck").

8. **Churn window** — `BatteryBarPositionController.syncToService()` → `FloatingService.restartModule()` = `stop()` (removeView) + `start()` (addView) setiap kali ada perubahan di panel. Window surface dibuat & dibuang berulang.

### 2.4 Tidak ditemukan (artinya bukan biang)
- Tidak ada `setLayerType` / `LAYER_TYPE_SOFTWARE` / `hardwareAccelerated=false`.
- Tidak ada WebView, TextureView, SurfaceView.
- Tidak ada `new Thread` / ExecutorService / Timer (semua `Handler.postDelayed`).
- Tidak ada bitmap persisten besar: satu-satunya bitmap tetap FBI = ikon notifikasi 192×192 (±147 KB) + `TriangleColorPickerView` ±1 MB saat dialog dibuka (dibuat ulang saat hue berubah, tanpa `recycle()` — sampah, bukan penumpukan permanen).
- Tidak ada `android:largeHeap` (bagus — largeHeap justru memperburuk).
- Fragment panel di-`hide()` bukan `remove()` → kedua panel lengkap tetap hidup (2 fragment saja, kecil).

---

## 3. Penjelasan Kenapa Angkanya "Cukup Sama" FBI vs FTxT

| Komponen | Estimasi PSS | FBI | FTxT |
|---|---|---|---|
| ART/dex/native libs (libhwui, skia, libc, android_runtime) | ±30–50 MB | sama | sama |
| Java heap inti UI (Activity, Material, Drawer, RecyclerView, 2 fragment hidup, Service) | ±30–60 MB | sama | sama |
| RenderThread + GPU context + surface tiap window overlay | ±20–50 MB | 2 surface | 7 surface |
| PNG bitmap (tema aktif) | 0 MB | ±9 MB |
| Alokasi periodik → heap membengkak & tidak turun | ±20–60 MB | sama (pola sama) | sama + lebih parah |
| **Kisaran total** | | **±150–200 MB** | **±180–220 MB** |

Karena dua komponen terbesar (baseline + perilaku heap) **identik**, kedua aplikasi tampil "kurang lebih sama besar" meski jumlah modul berbeda drastis. Angka 180–200 MB adalah **kondisi stabil PSS/RSS** yang umum untuk aplikasi modern dengan foreground service + overlay + hardware acceleration — bukan anomali yang bisa dijelaskan satu baris kode.

---

## 4. Rekomendasi (urut dampak nyata)

### 4.1 FBI & FTxT (prioritas tertinggi) — hentikan alokasi periodik
1. **Cache nilai terakhir & skip update bila tidak berubah**
   - Di `BatteryStatsModule` & `BatteryBarModule`: simpan teks terakhir; jika hasil `updateDisplay()` identik → jangan `setText`/`invalidate`. Suhu/persen jarang berubah antar tick; ini menghentikan sebagian besar `SpannableString` + `registerReceiver` + render.
   - Ini pola yang sama dengan `cachedIconBitmap` yang sudah dipakai `NotificationHelper`.
2. **Jangan `registerReceiver(null, ...)` per tick** — daftarkan sekali di service (`registerReceiver(receiver, filter)`), simpan `Intent` extras terakhir, baca dari variabel cache. Estimasi: mengurangi puluhan ribu alokasi per jam.
3. **Naikkan interval default** — Battery Info 5 d → 10 d; Battery Bar 1 d → 2–3 d (kombinasi dengan #1 membuat dampak hampir tak terasa di layar).
4. **NotificationHelper**: hentikan `nm.notify()` tiap 10 detik — cukup perbarui saat nilai benar-benar berubah (sudah cache bitmap; tinggal skip notify bila `cachedIconText` sama) atau ganti interval menjadi 60 d.
5. **Hindari `restartModule` (removeView+addView)** di panel Battery Bar — gunakan update in-place (`applyAppearance`/`reloadLayout` sudah tersedia). Window surface tidak dibuat/dibuang berulang.

### 4.2 Khusus FTxT (backport dari FBI)
6. Hapus PNG besar → shape gradient + adaptive icon vector (hemat ±9 MB/tema, dan ruang APK).
7. Backport `NotificationHelper` yang di-cache (FBI v1.0.0).
8. Backport WakeLock "hanya saat layar menyala" (`isInteractive()` + receiver SCREEN_ON/OFF).
9. Backport hentikan animasi `BatteryBarView` saat `GONE` (`onVisibilityChanged`).
10. Batasi `FpsModule`: jangan `postFrameCallback` terus — cukup update 1×/detik (hitung FPS di window waktu, bukan per frame).

### 4.3 Verifikasi (tanpa PC)
- Bandingkan sebelum/sesudah lewat **Developer Options → Running Services / Recent Apps**, setelah app berjalan 5+ menit.
- Untuk breakdown pasti, bila nanti ada PC/termux: `adb shell dumpsys meminfo exp.ftxt.fbi` dan amati sektor **Java Heap**, **Native Heap**, **Graphics**, **GL mtrack** — itu yang menentukan di mana sisanya berada.
- Target realistis setelah optimasi #1–#5: ±100–130 MB (bukan 50 MB — baseline Android modern memang di situ).

---

## 5. Keterbatasan Analisa

- Murni statis (membaca source code). Tidak ada data `dumpsys` / `meminfo` / Profiler.
- Angka RAM yang dilihat user kemungkinan adalah **PSS/RSS total proses** (Recents / Running Services) yang sudah mencakup native, GPU, dan shared memory — sebagian tidak terlihat dari kode aplikasi.
- Jika setelah optimasi #1–#5 angka masih tetap 180 MB, langkah berikutnya wajib pengukuran perangkat untuk membedah komponen mana yang menonjol.

---

## 6. File yang Ditinjau (kunci)

- FBI: `core/FloatingService`, `core/NotificationHelper`, `core/WakeLockManager`, `features/battery_stats/BatteryStatsModule`, `features/battery_bar/BatteryBarModule`, `features/battery_bar/BatteryBarView`, `shared/ui/ShadowTextView`, `shared/ui/OverlayShadow`, `shared/ui/OverlayDragHandler`, `ui/BatteryPositionController`, `ui/BatteryBarPositionController`, `ui/PanelManager`, `MainActivity`, `AndroidManifest.xml`.
- FTxT: file yang sama + `features/fps_display/FpsModule`, `features/network_stats/NetworkModule`, `features/clock_module/ClockModule`, `features/floating_text/TextModule`, `shared/preset/PresetManager`, seluruh aset `res/drawable*`.
