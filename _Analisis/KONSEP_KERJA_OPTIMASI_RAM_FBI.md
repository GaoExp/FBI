# Konsep Kerja: Optimasi RAM FBI (Sesi Recoverable)

**Tanggal mulai:** 2026-08-14
**Ruang lingkup:** FBI saja (`/storage/internal_new/project/FBI`). **FTxT DI-SKIP** (urusan agent lain).
**Mode:** Perubahan kecil & terfokus — TIDAK ada refactor besar, TIDAK mengubah arsitektur.
**Keputusan:** Interval default **TIDAK diubah** (menjaga perilaku yang terlihat user). Hanya skip-update bila nilai tidak berubah.

## Tujuan
Mengurangi alokasi periodik (garbage churn) yang membuat heap Java membengkak & tidak turun → menurunkan RAM stabil (±180 MB) tanpa mengubah fungsionalitas.

## Catatan Kritis (agar tidak hilang bila sesi terputus)
- JANGAN commit/tag/push (AGENTS.md).
- JANGAN build.
- Catat perubahan di CHANGELOG entry versi berjalan `[1.0.0]` (bukan entry baru).
- Setiap langkah SELESAI → tandai `[x]` di file ini.
- Pola kunci: **cache nilai terakhir → skip render/receiver bila sama** (sama seperti `cachedIconBitmap` di NotificationHelper).
- Saat `updateColor`/`updateLabelColor`/`updateSeparatorColor` dipanggil, HARUS memaksa re-render (reset cache), agar perubahan warna tetap terlihat.

## Langkah-Langkah

1. [x] Buat file konsep ini di `_INSPEKSI/`.
2. [x] **BatteryStatsModule** — cache teks terakhir (`lastRenderedText`); `updateDisplay()` return bila teks sama. Reset cache di `updateColor/updateLabelColor/updateSeparatorColor`.
   - File: `app/src/main/java/exp/ftxt/features/battery_stats/BatteryStatsModule.java`
   - Aman: warna teks sudah diset sebelumnya; `setTextSize`, `applyBackground` tidak perlu re-render.
3. [x] **BatteryBarModule** — cache status terakhir (`lastPercent/lastCharging/lastLow`); `updateDisplay()` return bila sama → skip `registerReceiver`+`invalidate` saat nilai tidak berubah. (ganti `registerReceiver(null,...)` per tick dengan `BroadcastReceiver` permanen yang terdaftar saat `start()`)
   - File: `app/src/main/java/exp/ftxt/features/battery_bar/BatteryBarModule.java`
   - Aman: perubahan warna via `applyAppearance()` → `setBarConfig()` sudah `invalidate()` sendiri.
4. [x] **NotificationHelper** — di loop 10 detik, skip `nm.notify()` bila `temp + toggleIcon` sama dengan siklus sebelumnya (`lastNotifiedKey`).
   - File: `app/src/main/java/exp/ftxt/core/NotificationHelper.java`
   - `updateNotification()` (dipanggil dari aksi lain) TETAP selalu update — tidak di-guard.
5. [x] **Hindari `restartModule` (removeView+addView) → update in-place.** Perluasan: bukan hanya `BatteryBarPositionController`, tapi semua pemanggil `restartModule` di project (temuan grep):
   - `FloatingService`: tambah helper statis `updateBatteryBarInPlace()` (panggil `applyAppearance()`+`reloadLayout()`+`updatePosition()`, guard `isRunning()`) dan `updateBatteryStatsInPlace()` (panggil `refreshDisplay()`).
   - `BatteryStatsModule`: tambah `public void refreshDisplay()` = reset `lastRenderedText` + `updateDisplay()` + `updatePosition()`.
   - `BatteryBarPositionController.syncToService()` → panggil helper (hapus import tak terpakai).
   - `BatteryBarPanelController.restart()` → panggil helper (menggantikan ~15 pemanggilan restart pada perubahan setting bar).
   - `BatteryPanelController.onOrderChanged()` & `showIntervalPopup()` → panggil helper battery stats (interval sebenarnya otomatis terpakai karena tickRunnable membaca config tiap siklus).
   - Pola sudah terbukti: `BatteryPositionController.syncToService()` sudah memakai update in-place.
   - Catatan: `BatteryBarView.setBarConfig`/setter animasi sudah lengkap `invalidate()` + kelola animator sendiri → update in-place aman tanpa restart.
6. [x] Verifikasi kebenaran statis (baca ulang file yang diubah; pastikan tidak ada referensi rusak).
7. [x] Catat di CHANGELOG.md entry `[1.0.0]` (section 🔧 Optimasi & Penyesuaian).
8. [x] Update file konsep: semua langkah `[x]`, tutup sesi.

## Status Terakhir
- **SELESAI** — semua langkah tuntas. Semua pemanggil `restartModule` diganti update in-place; tidak ada pemanggilan `restartModule` tersisa (hanya definisi di FloatingService).
- Belum build/commit/tag/push (menunggu perintah user).
