# Panduan Penggunaan FBI

---

## Daftar Isi

- [Persyaratan](#persyaratan)
- [Memulai](#memulai)
- [Navigasi](#navigasi)
- [Toolbar](#toolbar)
- [Fitur Overlay](#fitur-overlay)
- [Pengaturan](#pengaturan)
- [Izin](#izin)

---

## Persyaratan

- Android 8.0 (API 26) atau lebih baru
- Izin overlay aplikasi lain (SYSTEM_ALERT_WINDOW)

---

## Memulai

1. Install APK FBI
2. Saat pertama dibuka, aplikasi akan meminta izin overlay, notifikasi, dan optimasi baterai
3. Berikan izin yang diminta
4. Aktifkan salah satu modul overlay (Battery Info atau Battery Strip) dari panelnya
5. Aplikasi akan otomatis menjalankan overlay yang aktif
6. Pilih modul overlay dari navigation drawer

---

## Navigasi

Aplikasi menggunakan **Navigation Drawer** (sidebar) yang bisa dibuka dengan:
- Tap ikon hamburger (☰) di kiri toolbar
- Swipe dari tepi kiri layar

Drawer terdiri dari menu berikut (urutan default; item panel bisa di-reorder dengan long-press lalu drag):

| Menu | Fungsi |
|------|--------|
| Battery Info | Panel dengan 3 tab: Monitor (placeholder), Overlay (Battery Info), Battery Strip |
| Info Memori | Panel dengan 2 tab: Monitor (pemakaian memori realtime) dan Overlay (konfigurasi) |
| Kill Service | Hentikan semua layanan overlay + tutup app |
| Keluar | Tutup UI aplikasi, overlay tetap berjalan |

> **Catatan:** Battery Strip tidak lagi menjadi item drawer tersendiri — konfigurasinya ada di tab **Battery Strip** di dalam panel **Battery Info**. Konfigurasi & izin aplikasi diakses melalui ikon gear (⚙️) di toolbar, bukan dari drawer.

---

## Toolbar

Ikon di pojok kanan toolbar:

| Ikon | Fungsi |
|------|--------|
| Gear ⚙️ | Buka popup: Muat Preset & Konfigurasi (Pengaturan) |
| Orientasi ↔ | Toggle layar Potret/Lanskap |
| Bulan/Matahari 🌙☀️ | Toggle tema gelap/terang |

Header toolbar menampilkan navigasi drawer dan judul modul yang aktif.

> **Muat Preset** menyesuaikan tab yang sedang aktif di panel: di panel Battery Info, tab Overlay memuat preset Battery Info dan tab Battery Strip memuat preset Battery Strip; tab Monitor menampilkan pemberitahuan (belum ada preset).

---

## Fitur Overlay

Setiap panel overlay memiliki pengaturan yang dikelompokkan dalam **section collapsible**:
- **▾ Tampilan Overlay** — Urutan baris info (chip drag dua zona Aktif/Nonaktif), sembunyikan label, update interval, ukuran teks, dan warna
- **▾ Posisi** — Kontrol posisi (slider X/Y, D-Pad, area aman)
- **▾ Shadow** — Konfigurasi shadow (toggle, warna, blur, offset)
- **▾ Background** — Konfigurasi background (toggle, warna, padding, offset, margin, radius)

Klik header section untuk membuka/tutup grup.

### Battery Info
Panel Battery Info memakai **bottom navigation 3 tab**: **Monitor | Overlay | Battery Strip**.
- **Tab Monitor** — Placeholder "Monitor baterai sedang dikerjakan" (fitur monitor baterai direncanakan, belum tersedia).
- **Tab Overlay** — Seluruh konfigurasi overlay Battery Info (isi dari sub-bagian "Battery Info" di bawah).
- **Tab Battery Strip** — Seluruh konfigurasi Battery Strip (isi dari sub-bagian "Battery Strip" di bawah).

#### Battery Info (tab Overlay)
- Menampilkan suhu (°C), persen (%), tegangan (V), arus (mA), dan daya (W) baterai dalam **satu modul kesatuan** dengan satu posisi
- **Tampilan Overlay** — Atur urutan tampilan baris info dengan menyeret chip antar zona **Aktif** dan **Nonaktif**; chip di zona Nonaktif disembunyikan dari overlay. Section ini juga berisi Sembunyikan Label, Update interval, Ukuran Teks, dan preview warna
- **Warna Label Terpisah** — Tombol "Label" untuk warna satuan terpisah dari nilai angka
- **Warna Pemisah** — Baris info dipisahkan tanda `|` (misal `37.4°C | 87% | 4.1V | +120mA | 0.5W`) yang warnanya bisa diatur sendiri
- Opsi **Sembunyikan Label** untuk tampilan nilai saja
- Data dibaca dengan fallback dari sticky broadcast (ACTION_BATTERY_CHANGED), BatteryManager, dan sysfs
- Konfigurasi ukuran, warna, shadow, background, kontrol posisi
- Interval update bisa diatur (default 5d)

### Battery Strip
Berikut adalah konten tab **Battery Strip** di dalam panel **Battery Info**:
- Menampilkan bar baterai sebagai strip di layar (level mengikuti persentase baterai)
- **Mode Cepat** — Bar menempel penuh di salah satu sisi layar (Atas/Bawah/Kiri/Kanan). Posisi & panjang otomatis mengikuti sisi yang dipilih. Kontrol posisi manual nonaktif dalam mode ini. **Area Aman selalu terkunci aktif** (checkbox tercentang, disabled) dan Kunci Posisi (touch passthrough) tidak tersedia.
- **Mode Manual** — Matikan "Mode Cepat" untuk mengatur panjang bar (0–100%) dan posisi bebas (slider X/Y, D-Pad) per orientasi layar. **Area Aman otomatis terkunci aktif** dalam mode ini.
- **Warna Level** — Pemilih skema warna otomatis berdasarkan level baterai: **Tanpa Skema** (warna tetap pilihan user), **Klasik 3-warna** (hijau >20%, kuning ≤20%, merah ≤10%), **Hue Gradien** (0–20% hue `1°` S70%, 21–50% hue `2°→100°` S70%, 51–100% hue `102°→260°` dengan saturasi naik `71%→100%`). Saat skema dipilih, langsung aktif.
- **Baterai Rendah** — Atur ambang low (default 40%); saat level di bawah ambang bar berubah ke warna Low dan berkedip (kecepatan fade bisa diatur). **Warna Low hanya berfungsi saat skema Tanpa Skema** — jika skema Klasik 3-warna atau Hue Gradien aktif, pemilih Warna Low diburamkan & nonaktif.
- **Strip Kosong** — Tampilkan sisa strip di belakang bar dengan warna terpisah.
- **Charging** — Bar menampilkan animasi shine saat perangkat di-charge. Efek ini bisa disesuaikan di section **Animasi Pengisian Daya**: **Animasi Shine** (on/off, default nonaktif), **Kecepatan Shine**, dan **Lebar Band**. Section ini juga berisi **Animasi Wave saat charging** (gelombang mengalir sepanjang bar): **Animasi Wave** (on/off, default nonaktif), **Kecepatan Wave**, dan **Intensitas Wave**.
- **Baterai Rendah** — Saat level di bawah ambang low, bar menampilkan **animasi Wave** (kedutan gelombang): pola gelombang sinus yang menjalar sepanjang bar, berjalan bersamaan dengan animasi fade. Sesuaikan di section **Animasi Baterai Rendah**: **Animasi Wave** (on/off, default nonaktif), **Kecepatan Wave**, **Intensitas Wave**. Section ini juga berisi Warna Low, Ambang Low, **Animasi Fade** (on/off, default nonaktif), dan Kecepatan Fade.
- Pengaturan lain: ketebalan, radius sudut, orientasi horizontal/vertikal, invert, strip kosong.

### Info Memori
Panel Info Memori memakai **bottom navigation 2 tab**: **Monitor | Overlay**.
- **Tab Monitor** — Dashboard pemantauan memori proses per detik dalam **3 kartu**: **Proses FBI** (14 nilai: Java Heap, Native Heap, Graphics, Other PSS, Total Proses, Private Dirty/Clean, Shared Dirty, Swapped, Code, Stack, System, Private/System Other — 2 kolom), **Runtime Java** (Heap Terpakai, Heap Bebas, Heap Maksimum), dan **RAM Sistem** (Total, Terpakai + persentase + progress bar, Tersedia, Cached). Pemantauan dimulai lewat tombol **Mulai Pemantauan** (nilai tampil 0.0 MB sebelum berjalan) dan dihentikan dengan tombol **Hentikan Pemantauan**. Switch **Pemantauan Latar Belakang** membuat polling terus berjalan di background meski app ditutup (berhenti saat Kill Service). Tombol **Salin Ke Clipboard** menyalin seluruh nilai monitor saat ini, dan **Simpan Snapshot** menulis riwayat 20 snapshot terakhir ke file teks `FBI_memori_*.txt` di folder Download.
- **Tab Overlay** — Konfigurasi overlay Info Memori: toggle aktif + kunci posisi, section Tampilan Overlay (pilih item Java/Native/Graphics/Total yang tampil, sembunyikan label, ukuran teks, warna nilai/label/pemisah), Posisi (slider X/Y, D-Pad, koordinat, safe area), Shadow, Background — pola sama dengan Battery Info. Overlay hanya bisa diaktifkan saat **Pemantauan Latar Belakang** menyala; jika switch mati, tab Overlay tidak bisa diakses dan overlay ikut dinonaktifkan.

Data memori dibaca via `Debug.getMemoryInfo()`; warna label default CYAN; posisi default `0.6` agar tidak bertumpuk dengan battery (`0.8`).

### Color Picker
Color Wheel dan Hue/Saturation/Value/Alpha slider tersedia saat mengetuk preview warna:

- **Color Wheel** — Full disk dengan crosshair, sentuh untuk pilih warna
- **Slider H/S/V** — Hue (0–360°), Saturation (0–100%), Brightness (0–100%), masing-masing dengan gradient background dinamis
- **Slider Alpha** — Opacity (0–255) dengan checkerboard transparansi
- **Two-way sync** — Wheel dan slider H/S/V saling sinkron

**Informasi & kontrol:**
- Nama warna otomatis, HEX 8 digit (#AARRGGBB)
- Edit HEX manual via ikon pensil
- Long-press nilai warna untuk salin ke clipboard
- Grid Saved Colors (simpan/load/hapus, maks 16 warna)

### Background & Shadow
- Background: warna (dengan alpha), ukuran/padding (0–80px), offset X/Y, margin (0–30px), radius rounded corner (0–50px)
- Shadow: warna (dengan alpha), blur radius (0–50px), offset X/Y
- Background dan Shadow adalah fitur terpisah, bisa diatur independen
- Klik label slider untuk edit nilai manual via dialog

### Preset System
Akses preset dari **icon gear → "Muat Preset"**. Dialog preset terbuka dengan:
- **Search bar** — Cari preset berdasarkan nama atau tag
- **Header**: tombol **Tandai** — Aktifkan mode pilih, checkbox muncul di tiap item
- **Header**: tombol **Tandai Semua** — Centang/hapus centang semua item
- **Bottom bar (normal)**: **Simpan** — Simpan seluruh konfigurasi modul/tab yang aktif (di panel Battery Info: tab Overlay menyimpan preset Battery Info, tab Battery Strip menyimpan preset Battery Strip); **Impor** — Impor dari file
- **Bottom bar (Tandai)**: **Hapus**, **Favorit**, **Bagikan**, **Ekspor** — Aksi batch untuk item terpilih
- **Tap item**: menu dengan **Gunakan Preset** di urutan pertama
- **Long-press item**: **Drag & drop** — Tukar posisi preset dalam daftar

Metadata: tags, favorite, timestamp, thumbnail warna. Version history hingga 10 versi. Preset baru diletakkan di urutan teratas.

### Kontrol Posisi
- **Slider X/Y** — Posisi horizontal dan vertikal (persentase 0.0–1.0)
- **D-Pad** — Tombol arah ↑↓←→ dengan tahan untuk repeat
- **Safe Area** — Batasi agar overlay tidak masuk area notch/cutout
- **Touch Passthrough** — Kunci posisi agar sentuhan tembus ke aplikasi belakang
- Posisi tersimpan otomatis per orientasi layar

---

## Pengaturan

Diakses dari **icon gear → "Konfigurasi"**:

- **Izin Overlay** — Kelola izin tampilan di atas aplikasi lain
- **Izin Notifikasi** — Kelola izin notifikasi kontrol
- **Optimasi Baterai** — Kelola izin nonaktifkan optimasi baterai
- **Ikon Aplikasi** — Ganti ikon launcher antara Default dan Alternatif (tanpa uninstall)

> Monitoring memori realtime + simpan snapshot yang dulu ada di halaman Konfigurasi kini dipindah ke tab **Monitor** di panel **Info Memori**.

---

## Izin

| Izin | Fungsi |
|------|--------|
| `SYSTEM_ALERT_WINDOW` | Menampilkan overlay di atas aplikasi lain |
| `POST_NOTIFICATIONS` (13+) | Notifikasi kontrol foreground service |
| `FOREGROUND_SERVICE` | Menjalankan service overlay di latar depan |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Service overlay khusus |
| `WAKE_LOCK` | Mencegah CPU tidur saat overlay aktif |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Mencegah overlay dihentikan sistem |
| `PACKAGE_USAGE_STATS` | Akses statistik penggunaan aplikasi |
| `RECEIVE_BOOT_COMPLETED` | Auto-start overlay saat boot |

Izin diminta otomatis saat pertama aplikasi dibuka. Kelola izin bisa dilakukan di **Konfigurasi**.

---
