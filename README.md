# Kelime Quiz

Android tabanlı, kelime ezberleme ve tekrar odaklı bir uygulama. Proje Java, Room, Glide ve Material bileşenleriyle geliştirilmiştir.

## Genel Bakış

Uygulama kullanıcı bazlı çalışır. Her kullanıcının kelime havuzu, öğrenme ilerlemesi ve quiz geçmişi birbirinden bağımsız tutulur.

## Özellikler

- Kullanıcı kayıt, giriş ve şifre sıfırlama akışı
- Kullanıcıya özel kelime havuzu
- Kelime ekleme, silme ve detay görüntüleme
- Görsel ve örnek cümle desteği
- Quiz / tekrar modülü
- Tekrar zamanı gelen kelimeleri önce soran SRS mantığı
- Doğru cevapta seviyeyi artıran, yanlış cevapta sıfırlayan ilerleme sistemi
- Öğrenilmiş kelimeleri quiz havuzundan çıkarma
- Profil ekranından açık / koyu tema seçimi
- Profil ekranından quiz soru sayısı ayarı
- Ana ekranda quiz özeti ve hızlı başlatma

## Quiz Mantığı

Quiz sistemi kullanıcı bazlı ilerler:

1. Doğru cevap kelime seviyesini artırır.
2. Yanlış cevap seviyeyi sıfırlar.
3. Seviye 1'den 6'ya kadar tekrar aralıkları sırasıyla 1 gün, 1 hafta, 1 ay, 3 ay, 6 ay ve 1 yıldır.
4. 6. doğru tekrar tamamlandığında kelime öğrenilmiş kabul edilir.
5. Öğrenilmiş kelimeler normal quiz havuzuna tekrar girmez.

## Teknoloji Yığını

- Java 17
- Android SDK 34
- Room
- Material Design
- Glide
- JUnit

## Proje Yapısı

- `app/src/main/java/com/samil/kelimequiz/data` veri katmanı
- `app/src/main/java/com/samil/kelimequiz/domain` iş kuralları ve modeller
- `app/src/main/java/com/samil/kelimequiz/ui` ekranlar
- `app/src/main/res` layout, drawable ve renk kaynakları

## Çalıştırma

Proje kök dizininden:

```bash
gradlew.bat assembleDebug
```

Uygulamayı test etmek için:

```bash
gradlew.bat testDebugUnitTest lintDebug
```

## Notlar

- Uygulama giriş yapılmadan korumalı ekranları açmaz.
- Quiz soru limiti profil ekranından değiştirilebilir.
- Tema değişimi profil ekranından yapılır ve uygulama genelinde uygulanır.
- `lint` temiz geçecek şekilde tutulmuştur.
