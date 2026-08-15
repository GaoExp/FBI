# 📋 Analisis: Penggunaan RAM Aplikasi FBI

**Tanggal:** 2026-08-14
**Tujuan:** Mengidentifikasi penyebab penggunaan memori (RAM) yang besar pada aplikasi FBI dan memberikan rekomendasi optimasi.

---

## 🎯 Ringkasan

Penggunaan RAM yang besar pada FBI bersumber dari beberapa hal yang saling menumpuk:

1. Bitmap PNG beresolusi ekstrem di `res/drawable/` (kontributor terbesar).
2. Alokasi objek & bitmap berulang pada `NotificationHelper.startIconCycling()` setiap 10 detik.
3. Pembacaan baterai via `registerReceiver` berulang di setiap tick update.
4. WakeLock PARTIAL 24/7 yang menjaga proses tidak pernah idle.
5. Animasi infinite pada `BatteryBarView` saat status charging/low aktif.
6. Referensi static `onPositionUpdate` (minor).

---

## 🔍 Temuan Detail

### 1. Bitmap PNG beresolusi ekstrem

Semua PNG disimpan di folder `res/drawable/` **tanpa density qualifier** (dianggap mdpi), sehingga saat di-decode menjadi bitmap berformat ARGB_8888 (4 byte/piksel) **pada ukuran asli tanpa penskalaan**.

Dimensi & estimasi memori (ARGB_8888):

- `bg_alt_light2.png` / `bg_alt.png` — 1024×1024 → ±4,2 MB (dipakai background drawer yang lebarnya hanya ±280dp → pemborosan paling besar)
- `ic_launcher_bg.png` — 1024×1024 → ±4,2 MB
- `ic_launcher_foreground.png` — 1024×1024 → ±4,2 MB (adaptive icon seharusnya cukup ±432×432 px)
- `ic_launcher_foreground_alt.png` — 1024×1024 → ±4,2 MB
- `bg_main_light2.png` / `bg_main_dark.png` — 541×792 → ±1,7 MB
- `appbar_light.png` / `appbar_dark.png` — 1000×395 → ±1,6 MB
- `drawbar_light.png` / `drawbar_dark.png` — 1000×395 → ±1,6 MB

Layout utama memakai 4 gambar sekaligus pada satu waktu:

- `activity_main.xml` → `main_bg` (latar utama) + `toolbar_bg` (toolbar)
- `drawer_content.xml` → `drawer_bg` (drawer) + `drawer_header_bg` (header)

Total estimasi bitmap yang hidup bersamaan: **±13–25 MB** hanya untuk aset gambar.

### 2. Notifikasi foreground service (alokasi berulang setiap 10 detik)

`NotificationHelper.startIconCycling()` berjalan terus selama service hidup dan setiap 10 detik melakukan:

- `Bitmap.createBitmap(192, 192, ARGB_8888)` (±150 KB) + `Canvas`, `Paint`, `Typeface` baru
- `RemoteViews` baru + `Intent` + `PendingIntent`
- `registerReceiver(null, ACTION_BATTERY_CHANGED)`

Akibat: objek sampah terus-menerus → GC (garbage collection) sering → memori naik-turun dan RSS/native memory membengkak. Ini biang utama RAM yang "bergerak".

### 3. Pembacaan baterai berulang di setiap tick

- `BatteryStatsModule.readBatterySnapshot()` (BatteryStatsModule.java:319):
  - `new IntentFilter(...)` + `registerReceiver(null, ...)` setiap tick (interval default 5 detik)
  - `SpannableString` + `ForegroundColorSpan` per karakter setiap update
  - fallback `readSysfs()` → buka file I/O `/sys/class/power_supply/*` setiap tick jika voltase/arus nol
- `BatteryBarModule` memanggil `registerReceiver` **2× per tick** (`getBatteryPercent()` + `isCharging()`) dengan interval default 1 detik

Semua ini menghasilkan alokasi sampah terus-menerus → GC intensif.

### 4. WakeLock PARTIAL 24/7

`WakeLockManager` mengunci CPU terus (diperbarui setiap 4 menit) selama overlay aktif. Proses tidak pernah idle sehingga memori tidak bisa dipadatkan oleh sistem.

### 5. Animasi infinite `BatteryBarView`

Saat status charging (shine/wave) atau low (fade/wave) aktif, `ValueAnimator.INFINITE` menjalankan `invalidate()` ±60 fps. Render thread & hardware buffer terpakai penuh → beban memori render + CPU.

### 6. Referensi static `onPositionUpdate` (minor)

`BatteryStatsModule.onPositionUpdate` / `BatteryBarModule.onPositionUpdate` adalah static `Runnable` yang menyimpan referensi controller. Sudah di-reset di `cleanup()` (dipanggil dari `onDestroyView`), risiko kecil. Catatan: `cleanup()` juga memanggil `FloatingService.setOrientationSuffixForModule(...)` yang memicu `ensureBatteryStatsModule()` → bisa membuat modul baru jika sebelumnya null.

---

## ✅ Rekomendasi (urut prioritas)

1. **Perkecil/resample PNG** — target:
   - background UI & launcher icon: maksimal 512×512 px (ideal 432×432 px untuk adaptive icon)
   - atau ganti background UI dengan gradient/vector drawable
   Ini saja bisa memangkas ±10–20 MB.
   - **STATUS: SUDAH DITERAPKAN (2026-08-14)** — semua PNG dihapus, diganti shape gradient (background UI) + adaptive icon vector (launcher). Hemat ±13–25 MB bitmap.

2. **Perbaiki `startIconCycling()`** — buat bitmap & RemoteViews sekali lalu reuse, hapus bitmap lama (`recycle()`), atau ganti ikon dinamis dengan update interval lebih jarang.
   - **STATUS: SUDAH DITERAPKAN (2026-08-14)** — bitmap ikon suhu di-cache (dibuat ulang hanya saat nilai berubah), RemoteViews + onClick PendingIntent dibuat sekali via `ensureCachedViews()`, IntentFilter sticky baterai di-cache.

3. **Hemat pembacaan baterai** — gunakan satu sticky broadcast + cache hasil, jangan `registerReceiver` per tick; baca `readSysfs` hanya sesekali (bukan tiap tick).
   - **STATUS: SUDAH DITERAPKAN (2026-08-14)** — `IntentFilter` ACTION_BATTERY_CHANGED di-cache static di kedua modul; BatteryBarModule menggabung 2 `registerReceiver` per tick (percent + charging) menjadi 1 pembacaan status (`readBatteryStatus`).

4. **WakeLock lebih hemat** — ganti `PARTIAL_WAKE_LOCK` berdurasi pendek berulang atau pertimbangkan kebutuhan CPU aktif; beri jeda saat layar mati.
   - **STATUS: SUDAH DITERAPKAN (2026-08-14)** — WakeLock hanya dipegang saat layar menyala (`PowerManager.isInteractive()`); FloatingService mendengar `ACTION_SCREEN_ON`/`ACTION_SCREEN_OFF` untuk melepas saat layar mati dan mengakuisisi lagi saat nyala.

5. **Animasi** — pastikan animasi wave/shine/fade hanya aktif saat benar-benar dibutuhkan; batasi frame rate bila memungkinkan.
   - **STATUS: SUDAH DITERAPKAN (2026-08-14)** — animator infinite di BatteryBarView dihentikan saat overlay disembunyikan (`onVisibilityChanged`) dan dimulai lagi saat ditampilkan.

6. **Bersihkan static callback** — jangan picu pembuatan modul dari `cleanup()`.

---

## ⚠️ Catatan

- Angka estimasi memori dihitung sebagai `panjang × lebar × 4 byte` (ARGB_8888).
- Folder `app/src/main/assets/` belum ada, jadi sinkronisasi CHANGELOG tidak diperlukan.
- Pengukuran nyata (dumpsys meminfo / Android Studio Profiler) disarankan di perangkat untuk validasi.
