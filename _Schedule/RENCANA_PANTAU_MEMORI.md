# 📋 Rencana: Perluasan Pemantauan Info Memori

**Tanggal:** 2026-08-15
**Status:** PERENCANAAN (belum dikerjakan)
**Tujuan:** Menambah nilai yang dipantau di tab Monitor Info Memori FBI, dari 4 nilai (Java, Native, Graphics, Total) menjadi cakupan lebih luas mencakup proses, runtime, sistem, dan proses lain.

---

## 🎯 Lingkup Nilai yang Ditambahkan

Semua nilai tetap berhubungan dengan memori (RAM), bukan CPU. Dibagi 4 kelompok.

### A. Detail Proses FBI (dari `Debug.getMemoryInfo`, sudah tersedia di `MemoryMonitor`)

1. **Other PSS** — `info.otherPss` (KB). Sisa PSS di luar Java/Native/Graphics (buffer, library, cache internal). Pelengkap Total PSS.
2. **Total Private Dirty** — `info.getTotalPrivateDirty()` (KB). RAM murni milik FBI yang sudah diubah, tidak bisa dilepas sistem.
3. **Total Private Clean** — `info.getTotalPrivateClean()` (KB). RAM milik FBI yang masih asli dari file, bisa dilepas sistem saat penuh.
4. **Total Shared Dirty** — `info.getTotalSharedDirty()` (KB). RAM dipakai bersama proses lain yang sudah diubah.
5. **Total Swapped** — `info.getTotalSwapped()` (KB). Data FBI yang dipindah ke penyimpanan saat RAM penuh.
6. **Breakdown Kategori** — `info.getMemoryStat(...)` (format `"<angka> kB"`):
   - Code — `summary.code`, kode program di RAM.
   - Stack — `summary.stack`, tumpukan thread.
   - System — `summary.system`, memori internal sistem proses.
   - Private Other — `summary.private-other`.
   - System Other — `summary.system-other`.

### B. Runtime Java (dari `Runtime.getRuntime()`, tanpa file)

7. **Heap Terpakai** — `totalMemory() - freeMemory()`.
8. **Heap Bebas** — `freeMemory()`.
9. **Batas Heap Maksimum** — `maxMemory()`.

Catatan: beda dari PSS — ini dilihat dari sisi pengelola memori ART, bukan dari sisi sistem.

### C. RAM Sistem (perangkat, bukan FBI)

10. **Total RAM** — `ActivityManager.MemoryInfo.totalMem` (aktivitas `getSystemService(ACTIVITY_SERVICE)`).
11. **RAM Tersedia** — `ActivityManager.MemoryInfo.availMem`.
12. **Cached** — baca `/proc/meminfo` baris `Cached:` (parse KB).

### D. Proses Lain (via `ActivityManager`)

13. **PSS Proses Lain** — `getRunningAppProcesses()` ambil PID, lalu `getProcessMemoryInfo(int[])` → `getTotalPss()` per proses. Tampilkan beberapa proses dengan PSS terbesar.

---

## 🛠️ Rencana Implementasi

### 1. `MemoryMonitor.java`

- Ganti struktur penyimpanan dari `int[4]` menjadi objek hasil yang memuat semua nilai kelompok A + B + C + D.
- `Snapshot` di history diperluas mengikuti objek hasil yang sama.
- `getLastValues()` diganti metode pengambilan objek hasil (misal `getLastSnapshot()` atau `MemoryValues`).
- Poling tetap 1 detik. Catatan beban: baca `/proc/meminfo` + `getProcessMemoryInfo` tiap detik bisa lebih berat dari sekarang — perlu diputuskan apakah proses lain dibaca tiap detik atau tiap beberapa detik (lihat Catatan).

### 2. `MemoryPanelController.java`

- `updateMonitorInfo()` diperluas menyusun teks 4 baris → beberapa bagian:
  - Bagian Proses FBI: Java, Native, Graphics, Other, Total PSS, Private Dirty, Private Clean, Shared Dirty, Swapped, lalu Code/Stack/System.
  - Bagian Runtime Java: Heap terpakai, bebas, maksimum.
  - Bagian Sistem: Total, Tersedia, Cached.
  - Bagian Proses Lain: daftar proses teratas (nama + PSS).
- Format tetap `monospace`, nilai `formatMb(kb)`.
- `copyToClipboard()` otomatis ikut menyalin semua baris yang tampil (tanpa perubahan tambahan).
- `exportMemorySnapshot()` memakai history yang sudah diperluas dari `MemoryMonitor`.

### 3. `panel_memory.xml`

- Tidak perlu perubahan struktur selama teks tetap satu `TextView` `memMonitorText`. Jika ingin rapi, bisa dipisah per bagian (belum diputuskan).

### 4. File yang Disentuh

- `app/src/main/java/exp/ftxt/features/memory_stats/MemoryMonitor.java`
- `app/src/main/java/exp/ftxt/ui/MemoryPanelController.java`
- `app/src/main/res/layout/panel_memory.xml` (jika penataan teks berubah)

---

## ⚠️ Catatan / Keputusan yang Perlu Dikonfirmasi Sebelum Eksekusi

1. **Beban polling proses lain** — `getProcessMemoryInfo` tiap detik berpotensi memberatkan. Opsi: baca proses lain setiap 5 detik saja, atau hanya saat tombol Mulai Pemantauan aktif (background monitor tidak ikut).
2. **Jumlah proses lain yang ditampilkan** — batasi beberapa teratas (misal 3) atau semua.
3. **Nama proses** — tampilkan nama proses atau label aplikasi (label butuh PackageManager, beban tambahan).
4. **Cached dari /proc/meminfo** — perlu izin baca file sistem? `/proc/meminfo` umumnya bisa dibaca tanpa root. Alternatif: hilangkan Cached jika bermasalah, cukup Total & Tersedia dari `ActivityManager.MemoryInfo`.
5. **Dokumen ini hanya perencanaan** — kode belum diubah. Eksekusi menunggu persetujuan user.

---

## 📌 Status

- [ ] Menunggu persetujuan keputusan di atas
- [ ] Implementasi MemoryMonitor
- [ ] Implementasi MemoryPanelController
- [ ] Update CHANGELOG (versi berjalan)
