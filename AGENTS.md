# AGENTS.md — Aturan AI Project FBI

**⚠️ ATURAN PALING PENTING — BACA DULU:**
- **JANGAN commit / tag / push tanpa perintah user.**
- **Kerjakan hanya sesuai request user.**
- **JANGAN refactor / ubah file di luar scope.**
- **BACA ulang file ini setiap mulai bekerja.**
- **JANGAN menulis laporan section/bullet list apapun.**
- ** Gunakan bahasa Indonesia untuk thinking dan respons.**
- **JANGAN eksekusi apa pun sebelum diskusi selesai DAN user memberi perintah eksekusi.**
- **JANGAN membuat rencana/daftar langkah lanjutan lalu mengerjakannya sendiri tanpa persetujuan.**
- **Tindakan destruktif (hapus file, revert, ubah besar) WAJIB konfirmasi eksplisit dulu.**
- **Jika ada sesuatu yang membingungkan / tidak jelas, JANGAN asal improvisasi — tanya dulu sebelum mengerjakan.**

---

## 1. Versioning (`app/build.gradle`)

Format: **semver** `major.minor.patch`

| Komponen | Naik saat | Reset |
|----------|-----------|-------|
| **major** | milestone besar, arsitektur, breaking change | `minor=0, patch=0` |
| **minor** | fitur baru / fitur dihapus / fitur dipulihkan | `patch=0` |
| **patch** | bugfix, optimasi, maintenance | — |

**Algoritma:**
- Fitur baru/dihapus/dipulihkan → minor+1, patch=0
- Bugfix/optimasi/maintenance → patch+1
- Breaking change / arsitektur → major+1, minor=0, patch=0
- Removed/restored dicatat di CHANGELOG, tidak perlu komponen versi sendiri

---

## 2. CHANGELOG

Entry dicatat di **versi berjalan** (bukan entry baru). Urutan section WAJIB:

```
✨ Fitur Baru
🚮 Fitur Dihapus
📥 Fitur Dipulihkan
♻️ Perubahan Fitur
🔧 Optimasi & Penyesuaian
🐞 Bug Fixes
💡 Catatan
🗒️ File Added
✏️ File Changed
🔥 File Removed
```

**Format judul entry:** `# [x.x.x] - yyyy-mm-dd versionCode xxx` — versionName & versionCode dicatat di judul.

**Entry yang di-merge** (beberapa versi digabung dalam satu entry): section **🗒️ File Added**, **✏️ File Changed**, dan **🔥 File Removed** diabaikan (tidak ditulis).

**WAJIB:** cek git log untuk tahu status push. **JANGAN** buat entry baru sebelum commit di-push.

Sinkron: `cp CHANGELOG.md app/src/main/assets/` jika tidak build.

---

## 3. Workflow

### Edit Biasa
1. update kode
2. catat di CHANGELOG entry versi berjalan
3. **JANGAN commit / tag / push**
4. ulang sampai user perintah **commit & tag**

### Eksekusi & Persetujuan (WAJIB)
1. **Kerjakan hanya setelah user memberi perintah eksplisit untuk mengerjakan.** Bertanya, menjelaskan, atau menampilkan rencana BUKAN perintah eksekusi.
2. **Diskusi belum selesai = JANGAN mengerjakan.** Kalau user masih bertanya/membahas, berhenti dan tunggu arahan.
3. **Jangan pernah membuat daftar "Next Steps"/rencana lanjutan lalu langsung mengeksekusinya sendiri.** Rencana apa pun harus menunggu persetujuan user.
4. **Tindakan destruktif/berisiko** (hapus file, revert, pindah/potong isi file, mengubah banyak file sekaligus) **WAJIB meminta konfirmasi eksplisit dulu**, walaupun sudah direncanakan.
5. **Saat ragu apa yang diminta user, tanya dulu** — jangan menebak lalu mengerjakan.
6. **Jangan asal improvisasi.** Jika suatu pekerjaan/hal terasa membingungkan atau tidak jelas (tujuan, cara, dampak), berhenti dan tanya dulu ke user sebelum mengerjakan.
7. **Selesai mengerjakan sesuai perintah, berhenti.** Jangan lanjut ke pekerjaan tambahan yang tidak diminta.

### Pre-release
1. periksa semua dokumen (README, STRUKTUR, PANDUAN, CHANGELOG)
2. hapus label ***ONGOING*** pada judul entry
3. bilang user siap di-commit & tag

### Rilis (hanya jika diperintah)
1. `git add -A && git commit -m "vX.X.X deskripsi"`
2. `git tag vX.X.X`
3. **JANGAN push** — user yang push

### Setelah Push ( langsung buat judul entry versi baru)
1. versionCode +1
2. versionName akan disesuaikan setelah ada perubahan (major.minor.patch)
3. buat entry CHANGELOG baru (paling atas dengan label ***ONGOING***)
4. kembali ke Edit Biasa

---

## 4. Perilaku AI

| ✅ Lakukan | ❌ JANGAN |
|------------|-----------|
| baca agents.md dulu | refactor tanpa diminta |
| cek git status/log | ubah file di luar scope |
| perubahan minimal & fokus | audit project tanpa diminta |
| jawab singkat & actionable | build |
| Bahasa Indonesia | checklist panjang |
| eksekusi hanya setelah perintah eksplisit | mengeksekusi rencana/next steps sendiri tanpa persetujuan |
| tanya dulu saat ragu / diskusi belum selesai | lanjut mengerjakan saat diskusi belum selesai |
| konfirmasi dulu untuk hapus/revert/ubah besar | hapus/revert/ubah besar tanpa konfirmasi |
| | push / commit tanpa izin |
| | revert perubahan yang dilakukan user tanpa konfirmasi |

**CHAT RULES:**
- **JANGAN gunakan tabel markdown** di chat — tabel tidak berfungsi di UI chat ini. Gunakan list atau paragraf biasa.

- *DILARANG* tulis laporan section seperti Accomplished, Progress, Critical Context, Planning, atau ringkasan/checklist apapun di chat — langsung ke inti.

- **Jangan pernah lakukan build** *apapun alasannya*

 **Ingat: JANGAN push / commit / tag tanpa perintah user dan JANGAN menulis laporan section apapun yang DILARANG.**

PENTING:
Saya programmer, tetapi jangan menjelaskan seolah saya AI yang sudah mengetahui seluruh ekosistem Android. Saya ingin penjelasan yang manusiawi dan praktis.