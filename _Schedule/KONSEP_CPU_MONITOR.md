# 📋 Konsep: Pembuatan CPU Monitor FBI

**Tanggal:** 2026-08-15
**Status:** PERENCANAAN (belum dikerjakan)
**Tujuan:** Menjadi acuan pengembangan fitur CPU monitor ke depan — memantau pemakaian prosesor di perangkat, mirip pola yang sudah ada pada Info Memori.

---

## 🎯 Sumber Data yang Tersedia

Semua data CPU di Android dibaca dari `/proc` (virtual file system), tanpa root. Tidak ada API Android resmi yang memberi persentase CPU secara langsung, jadi semuanya berbasis hitung selisih antar pembacaan.

### A. Pemakaian CPU Sistem Keseluruhan

Sumber: `/proc/stat`

- Baris pertama `cpu ` → agregat semua inti. Kolomnya: user, nice, system, idle, iowait, irq, softirq, steal, guest, guest_nice (dalam satuan jiffies).
- Baris `cpu0`, `cpu1`, dst → waktu per inti dengan kolom yang sama.
- Persentase dihitung dari selisih dua pembacaan:
  - total = jumlah semua kolom
  - idle = kolom idle
  - usage% = (selisih total − selisih idle) / selisih total × 100

Sumber: `/proc/loadavg`

- Tiga angka pertama = load average 1, 5, 15 menit (rata-rata antrian proses yang berjalan). Tampil sebagai teks mentah tanpa hitung selisih.

### B. Pemakaian CPU Proses FBI

Sumber: `/proc/<pid>/stat`

- Field 14 (`utime`) dan field 15 (`stime`) = waktu CPU proses di user mode dan kernel mode (jiffies).
- `/proc/self/stat` dari dalam proses FBI langsung memberi data proses sendiri.
- Persentase = (selisih utime+stime) / (selisih waktu berjalan) × 100. Waktu berjalan bisa pakai `SystemClock.elapsedRealtime()`.

Sumber: `Debug.threadCpuTimeNanos()`

- Waktu CPU thread yang sedang berjalan di proses sendiri. Kurang relevan untuk persentase proses utuh, lebih untuk profiling per thread.

### C. Frekuensi CPU (tergantung vendor)

Sumber: `/sys/devices/system/cpu/cpu*/cpufreq/`

- `scaling_cur_freq` → frekuensi inti saat ini (kHz).
- `scaling_min_freq` / `scaling_max_freq` → batas frekuensi.
- Folder `cpufreq` tidak selalu ada; nama/isi bisa berbeda antar perangkat. Fitur ini harus tahan gagal baca (fallback ke nilai kosong).

---

## 🛠️ Rencana Pola Implementasi

Meniru pola Info Memori yang sudah ada:

### 1. Kelas pembaca statis (pola `MemoryMonitor`)

- Baca `/proc/stat` dan simpan nilai mentah; pembacaan berikutnya hitung selisih → persentase.
- Baca `/proc/self/stat` → persentase CPU proses FBI.
- Baca `/proc/loadavg` → teks load average.
- Baca frekuensi inti bila tersedia (try/catch, jangan crash).
- Poling via `Handler` per detik (atau interval yang diputuskan).
- History snapshot untuk kebutuhan export (meniru `MemoryMonitor.getHistory()`).

### 2. Penyajian

- Bisa dipakai dua mode mengikuti Info Memori: monitor manual di panel (tombol Mulai/Hentikan) dan background monitor (service). Ini perlu keputusan tersendiri.
- Format tampilan: persentase CPU total, persentase CPU proses, load average, dan frekuensi per inti.

### 3. File yang Kemungkinan Disentuh

- `app/src/main/java/exp/ftxt/features/...` → kelas pembaca CPU baru (misal `CpuMonitor`).
- Controller panel baru atau perluasan controller yang ada.
- Layout panel baru.
- `FloatingService`, `BootReceiver`, `MainActivity`, prefs — hanya bila background monitor CPU diterapkan.

---

## ⚠️ Catatan / Keputusan yang Perlu Dikonfirmasi Sebelum Eksekusi

1. **Mode pemakaian** — cukup monitor manual di panel saja, atau sekalian background monitor seperti Memory Info?
2. **Ruang cakupan** — hanya persentase CPU total + proses FBI, atau ikut load average dan frekuensi per inti?
3. **Lokasi UI** — dibuat modul/panel baru sendiri, atau digabung ke panel yang sudah ada?
4. **Beban** — `Debug.threadCpuTimeNanos()` sebaiknya dihindari untuk monitor umum; `/proc/stat` + `/proc/self/stat` cukup ringan. Baca file tiap detik tidak memberatkan.
5. **Dokumen ini hanya konsep** — kode belum diubah. Eksekusi menunggu keputusan dan persetujuan user.

---

## 📌 Status

- [ ] Menunggu keputusan di atas
- [ ] Implementasi pembaca CPU
- [ ] Implementasi UI
- [ ] Update CHANGELOG (versi berjalan)
