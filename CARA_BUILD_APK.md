# CARA BUILD APK TANPA PC (GitHub Actions — GRATIS)

Butuh ~5 menit, cuma modal HP + akun GitHub gratis.

## Langkah 1: Buat akun & repo GitHub
1. Buka https://github.com → daftar akun gratis (pakai email).
2. Klik **+** (kanan atas) → **New repository**.
3. Nama repo: `kontrol-ortu` → pilih **Public** → **Create repository**.

## Langkah 2: Upload file
1. Di halaman repo, klik **uploading an existing file** (link kecil di bawah Quick setup).
2. Extract file `KontrolOrtu_SourceCode.zip` yang saya kirim.
3. **Upload SEMUA isi folder** `KontrolOrtu/` (settings.gradle.kts, build.gradle.kts, app/, .github/, dll) — JANGAN upload foldernya, tapi ISINYA.
4. Scroll ke bawah → **Commit changes**.

## Langkah 3: Tunggu build otomatis
1. Klik tab **Actions** (di atas repo).
2. Akan ada workflow "Build APK" berjalan (oranye berputar).
3. Tunggu ~3–4 menit sampai jadi centang hijau.

## Langkah 4: Download APK
1. Klik workflow yang sudah hijau → scroll ke bawah ke bagian **Artifacts**.
2. Klik **KontrolOrtu-APK** → download file zip-nya.
3. Extract zip → dapat file `app-debug.apk`.
4. Kirim ke HP anak → install (aktifkan "Sumber tidak dikenal").

## Jika gagal
- Klik workflow yang merah → lihat log error → screenshot kirim ke saya, nanti saya perbaiki.
- Pastikan semua file terupload (termasuk folder `.github/workflows/build.yml`).
