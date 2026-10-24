# KONTROL ORTU — APK Parental Control Buatan Sendiri

Aplikasi Android (Kotlin) untuk mengontrol HP anak dari jarak jauh **via SMS**.
Tanpa perlu internet, tanpa perlu anak klik apa pun.

## Fitur
1. **Kunci Layar** — kirim SMS kode `ORTU_LOCK123` → HP anak langsung terkunci (harus masuk PIN).
2. **Senter Kelap-Kelip 2 Menit** — kirim SMS kode `ORTU_LAMPU123` → senter berkedip selama 2 menit.

> Ganti kode rahasia di dalam aplikasi setelah diinstal.

## Cara Build APK
1. Install **Android Studio** (gratis): https://developer.android.com/studio
2. Buka Android Studio → **Open** → pilih folder `KontrolOrtu` ini.
3. Tunggu sampai Gradle selesai sync (download otomatis).
4. Menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. APK jadi ada di: `app/build/outputs/apk/debug/app-debug.apk`
6. Kirim file APK itu ke HP anak, install (aktifkan "Sumber tidak dikenal").

## Setup di HP Anak (lakukan sekali)
1. Install APK, buka aplikasinya.
2. Ganti **Kode Lock** dan **Kode Lampu** dengan kode rahasiamu → SIMPAN.
3. Tekan **AKTIFKAN DEVICE ADMIN** → izinkan.
4. Izinkan **SMS** dan **Kamera** saat diminta.
5. (Opsional) Sembunyikan ikon aplikasi dari launcher agar tidak di-uninstall sembarangan.

## Cara Pakai dari HP Orang Tua
- Buka aplikasi SMS biasa.
- Kirim ke nomor HP anak, isi pesan: `KODE_LOCK_KAMU` → layar anak terkunci.
- Atau isi: `KODE_LAMPU_KAMU` → senter berkedip 2 menit.
- SMS perintah otomatis dihapus dari inbox anak (tidak kelihatan).

## Catatan Penting
- Install hanya di perangkat yang **kamu miliki / kelola** (HP anak di bawah umur).
- Jangan disalahgunakan ke orang lain tanpa persetujuan — itu melanggar hukum.
- Di Android 10+, beberapa OEM (Xiaomi, Oppo, Vivo) membatasi SMS receiver di background.
  Aktifkan **Autostart** dan matikan **Battery Optimization** untuk aplikasi ini di pengaturan HP anak.
- Agar tidak bisa di-uninstall: aplikasi ini sudah Device Admin, jadi sebelum uninstall
  harus nonaktifkan Device Admin dulu (Settings → Security → Device admin apps).

## Struktur File
```
KontrolOrtu/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/orangtua/kontrol/
        │   ├── MainActivity.kt       # UI + setup kode + test
        │   ├── SmsReceiver.kt        # Terima SMS & jalankan perintah
        │   └── AdminReceiver.kt      # Device Admin untuk kunci layar
        └── res/
            ├── layout/activity_main.xml
            ├── values/strings.xml
            ├── values/themes.xml
            └── xml/device_admin.xml
```
