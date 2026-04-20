# Proje Özeti

## Proje Adı

Kelime Öğrenme Sistemi

## Kısa Tanım

Bu proje, 6 tekrar prensibine dayalı bir kelime öğrenme uygulamasıdır. Kullanıcı sisteme kelime ekler, bu kelimeler tekrar zamanına göre sorulur ve doğru ya da yanlış cevaplara göre öğrenme ilerlemesi güncellenir.

## Hedef

- Kalıcı öğrenmeyi desteklemek
- Düzenli tekrar mantığı kurmak
- Kullanıcı ilerlemesini ölçmek
- Temiz kod ve sürdürülebilir yapı ile geliştirme yapmak

## Beklenen Ana Özellikler

- Kullanıcı kayıt ve giriş sistemi
- Güvenli parola saklama
- Kelime ekleme
- Örnek cümle ekleme
- Görsel yolu saklama
- Quiz ve tekrar planlama
- Ayarlar sayfası
- Başarı raporu

## Teknik Yön

- Mobil hedef: Android Java
- Veritabanı: Room / SQLite
- Öğrenme mantığı: 6 aşamalı tekrar sistemi
- Güvenlik önerisi: `PBKDF2WithHmacSHA256`
