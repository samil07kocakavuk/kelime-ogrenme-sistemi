# Kelime Quiz - Android Application

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Language-Java%2017-orange.svg)](https://www.oracle.com/java/)
[![Room](https://img.shields.io/badge/Database-Room-blue.svg)](https://developer.android.com/training/data-storage/room)

Bu dizin, **Kelime Quiz** uygulamasının ana Android projesini içerir. Uygulama, 6 aşamalı tekrar algoritması (SRS) kullanarak kelime öğrenimini kolaylaştırmak amacıyla geliştirilmiştir.

## 📱 Uygulama Hakkında

Kelime Quiz, kullanıcı bazlı çalışan bir sistemdir. Her kullanıcının kendi kelime listesi, öğrenme durumu ve quiz ayarları yerel veritabanında (Room) güvenli bir şekilde saklanır.

## 🛠️ Teknik Özellikler

- **Mimarisi:** MVVM (Data, Domain, UI katmanları)
- **Veritabanı:** Room Persistence Library
- **Kullanıcı Güvenliği:** PBKDF2 Hashing algoritması ile yerel şifre doğrulama.
- **UI Bileşenleri:** Material Design 3 (Material Components for Android).
- **Görsel Yükleme:** Glide ile verimli resim önbelleğe alma ve görüntüleme.
- **Arka Plan İşlemleri:** ExecutorService ve Handler ile asenkron veritabanı işlemleri.

## 🧠 Quiz Algoritması

Sistem, **6 Tekrar Prensibine** göre çalışır. Bir kelimenin "Öğrenilmiş" statüsüne geçmesi için 6 farklı zaman diliminde doğru cevaplanması gerekir:

- 1. Tekrar: 1 Gün sonra
- 2. Tekrar: 1 Hafta sonra
- 3. Tekrar: 1 Ay sonra
- 4. Tekrar: 3 Ay sonra
- 5. Tekrar: 6 Ay sonra
- 6. Tekrar: 1 Yıl sonra

*Not: Herhangi bir aşamada yanlış cevap verilirse, kelimenin seviyesi 0'a düşer.*

## 📂 Paket Yapısı

- `com.samil.kelimequiz.data`: Entity'ler, DAO'lar ve Database sınıfları.
- `com.samil.kelimequiz.domain`: Business logic, yardımcı sınıflar (Helper) ve modeller.
- `com.samil.kelimequiz.ui`: Activity, Fragment ve Adapter sınıfları.

## 🚀 Çalıştırma ve Test

### Derleme (Build)

```bash
./gradlew assembleDebug
```

Derleme sonrası APK dosyasına `app/build/outputs/apk/debug/app-debug.apk` yolundan ulaşabilirsiniz.

### Testler

```bash
./gradlew test        # Unit Testleri çalıştırır
./gradlew lint        # Kod kalitesi kontrolü
```

## 📝 Notlar

- Uygulama ilk açılışta giriş ekranı ile başlar.
- Yeni kelime eklerken cihaz galerisinden resim seçilebilir.
- Quiz soru sayısı ve tema ayarları profil ekranından yönetilebilir.
