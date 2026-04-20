# AI Kullanım Notları

Bu projede yapay zeka destekli geliştirme yapılabilir; ancak çıkan kod doğrudan teslim edilmemelidir.

## Dikkat Edilecek Noktalar

- Kod basit ve sade olmalı
- Gereksiz katman oluşturulmamalı
- Uzun metotlar parçalanmalı
- Aynı iş farklı yerlerde tekrar edilmemeli
- Domain odaklı isimlendirme kullanılmalı
- Açık şifre saklanmamalı

## Kod Kalitesi Öncelikleri

- KISS
- DRY
- Okunabilirlik
- Bakım yapılabilirlik
- Küçük sınıflar ve küçük metotlar

## AI'ye Verilebilecek Kısa Yönlendirme

Bu proje Android Java tabanlı bir kelime öğrenme sistemidir. Kod üretirken KISS, DRY, okunabilirlik ve bakım yapılabilirlik öncelikli olsun. Gereksiz abstraction kurma. Parola saklama için `PBKDF2WithHmacSHA256` kullan. 6 aşamalı tekrar mantığını bozmadan ilerle.
