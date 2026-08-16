# 📋 Konsep: Battery Monitor (arah AccuBattery)

**Tanggal:** 2026-08-15
**Status:** PERENCANAAN (belum dikerjakan)
**Tujuan:** Acuan pengembangan tab **Monitor** pada panel Battery Info — placeholder "Monitor baterai sedang dikerjakan" yang sudah ada di `batTabMonitor` akan diisi dengan pemantauan baterai berciri AccuBattery: estimasi kapasitas/kesehatan baterai secara statistik tanpa akses kernel.

---

## 🎯 Kenapa Meniru AccuBattery?

Aplikasi biasa (non-root) **tidak bisa** membaca `cycle_count` dan kapasitas mentah dari chip baterai (`/sys/class/power_supply/` dibatasi di Android modern). AccuBattery mengatasi ini dengan cara statistik:

- Mengukur arus masuk dan perubahan level persen saat sesi pengisian.
- Dari selisih waktu dan muatan yang bertambah, memperkirakan kapasitas efektif baterai.
- Mengulang beberapa sesi, lalu mengambil nilai yang konsisten sebagai estimasi kapasitas.
- Kesehatan = kapasitas efektif / kapasitas desain (dari kolom `BatteryManager.EXTRA_BATTERY_LEVEL` dan perhitungan internal).

Ini pendekatan yang bisa ditiru penuh di FBI tanpa root.

---

## 🛠️ Data yang Dipakai

### Sumber data (semua API resmi)

1. Broadcast sticky `ACTION_BATTERY_CHANGED` — persen level, status pengisian, plugged (USB/AC/wireless), suhu, voltase, health, teknologi. Dibaca tanpa registrasi berulang (cukup `registerReceiver(null, ...)` untuk mengambil status terakhir).
2. Estimasi muatan efektif saat pengisian: `EXTRA_BATTERY_LEVEL` (persen) + `EXTRA_SCALE` (kapasitas 100%).
3. Waktu: `SystemClock.elapsedRealtime()` untuk durasi sesi.

### Yang TIDAK bisa dan tidak dipakai

- `cycle_count` dari kernel — tidak bisa dibaca non-root.
- Kapasitas persis chip — tidak tersedia.
- Estimasi kesehatan dari data tersebut akurat secara statistik, bukan nilai kernel.

---

## 📐 Fitur Tab Monitor (desain awal)

### 1. Sesi pengisian otomatis

- Deteksi saat status berubah ke `CHARGING` → mulai catat waktu mulai + level awal + voltase awal.
- Setiap tick, hitung delta level dan delta waktu → kecepatan isi (persen/menit) dan daya (volt × arus estimasi).
- Saat status berubah ke `FULL` atau kabel dicabut → tutup sesi, simpan ke riwayat.

### 2. Estimasi kapasitas efektif & kesehatan (inti AccuBattery)

- Untuk tiap sesi pengisian: kapasitas_efektif = delta_level / durasi × faktor kalibrasi internal.
- Simpan beberapa sesi, ambil nilai stabil (misal median/rata-rata sesi yang konsisten).
- Kesehatan = kapasitas_efektif / kapasitas_desain × 100%.
- Tampilkan: kapasitas desain (default), kapasitas efektif hasil ukur, dan persen kesehatan.

### 3. Estimasi waktu

- Waktu sampai penuh (jika sedang mengisi): (100 − level) / kecepatan_isi.
- Waktu bertahan (jika tidak mengisi): perhitungan dari riwayat pemakaian level per jam.

### 4. Logging & statistik

- Riwayat sesi pengisian (waktu mulai, durasi, level awal/akhir, kecepatan, daya).
- Statistik per hari: total waktu isi, total pemakaian.
- Bisa disimpan ke prefs `ftxt_prefs` (seperti pola yang ada) dan/atau diekspor ke Download (pola `MemoryMonitor.getHistory()`).

### 5. Tampilan placeholder → konten

- Ganti teks "Monitor baterai sedang dikerjakan" di `batTabMonitor` dengan layout berisi nilai real-time, riwayat, dan estimasi.

---

## 🔧 Pola Implementasi (mengikuti Memory Info)

### 1. Kelas pembaca statis `BatteryMonitor` (pola `MemoryMonitor`)

- Polling per detik dari sticky broadcast (tanpa registrasi berulang).
- Mengelola sesi pengisian, hitung estimasi kapasitas/kesehatan, simpan riwayat.
- Method `start()/stop()/getLastValues()/getHistory()`.

### 2. Controller

- `BatteryPanelController` atau controller baru mengisi tab Monitor: tombol Mulai/Hentikan (manual) dan switch Pemantauan Latar Belakang (bisa meniru `MemoryPanelController`), tombol salin ke clipboard, dan tombol export riwayat.

### 3. File yang Kemungkinan Disentuh

- `app/src/main/java/exp/ftxt/features/battery_stats/` → kelas `BatteryMonitor` baru (+ mungkin `BatteryMonitorConfig`).
- `app/src/main/java/exp/ftxt/ui/BatteryPanelController.java` → isi tab Monitor.
- `app/src/main/res/layout/panel_battery.xml` → layout tab Monitor.
- `FloatingService` / `BootReceiver` / `MainActivity` → hanya bila background monitor baterai diterapkan.
- Prefs `ftxt_prefs` untuk menyimpan riwayat & kapasitas kalibrasi.

---

## ⚠️ Catatan / Keputusan yang Perlu Dikonfirmasi Sebelum Eksekusi

1. **Kapasitas desain** — dari mana nilai awal? Ada daftar lookup per model perangkat (AccuBattery memakai database), atau dimulai dari estimasi otomatis sesi pertama lalu dikalibrasi? Untuk rilis awal, estimasi otomatis lebih masuk akal.
2. **Mode pemakaian** — cukup manual di tab Monitor, atau sekalian background monitor seperti Memory Info?
3. **Ruang cakupan fitur awal** — mulai dari sesi + estimasi kesehatan + waktu sampai penuh saja, atau langsung lengkap dengan statistik harian dan export?
4. **Arus (mA) untuk daya/watt** — tidak tersedia via API resmi pada banyak perangkat; jika ingin daya real-time perlu baca `/sys` yang rawan tidak ada. Opsi aman: hitung dari perubahan level, bukan arus.
5. **Dokumen ini hanya konsep** — kode belum diubah. Eksekusi menunggu keputusan dan persetujuan user.

---

## 📌 Status

- [ ] Menunggu keputusan di atas
- [ ] Implementasi `BatteryMonitor`
- [ ] Implementasi UI tab Monitor
- [ ] Update CHANGELOG (versi berjalan)
