# Clean Code Rules

Bu dosya, projeyi geliştirirken uyulması gereken temiz kod kurallarını toplar. Kurallar, derste verilen yazılım kalitesi notları ve proje değerlendirme beklentileri dikkate alınarak hazırlanmıştır.

## Ana Hedef

Kod şu niteliklere sahip olmalıdır:

- Basit
- Sade
- Okunaklı
- Anlaşılabilir
- Bakımı kolay

Bu projede amaç sadece çalışan bir uygulama çıkarmak değildir. Aynı zamanda kodun gözden geçirilebilir, geliştirilebilir ve refactor edilebilir olması gerekir.

## Temel İlkeler

### KISS

- Gereksiz katman kurma
- Gereksiz abstraction ekleme
- Küçük proje için büyük kurumsal mimari kullanma
- Çözümü en basit anlaşılır haliyle kur

Bu proje için uygun yaklaşım:

- `ui`
- `data`
- `domain`

gibi sade bir paket ayrımıyla ilerlemek ve modül sayısını gerçekten ihtiyaç kadar tutmaktır.

### DRY

- Aynı doğrulama kodunu birden fazla yerde tekrar etme
- Aynı veri dönüştürme işlemini farklı sınıflarda kopyalama
- Aynı hata mesajlarını her ekranda ayrı ayrı yazma

Uygun yaklaşım:

- ortak doğrulama metotları
- küçük yardımcı sınıflar
- tekrar eden sorgu veya algoritmaların tek yerde toplanması

### Okunabilirlik

Kod önce insan için yazılmalıdır.

Uygun yaklaşım:

- açık sınıf isimleri kullanmak
- açık metod isimleri kullanmak
- kısa metodlar yazmak
- aynı blok içinde çok fazla iş yapmamak
- derin iç içe `if` ve `for` bloklarını azaltmak

Kötü örnek isimler:

- `data`
- `obj`
- `temp`
- `manager2`

İyi örnek isimler:

- `PasswordHasher`
- `QuizGenerator`
- `SrsScheduler`
- `WordProgressRepository`

## Yapısal Kurallar

### Küçük Sınıflar ve Metodlar

- Her sınıf tek ana sorumluluk taşımalı
- Her metod tek iş yapmalı
- Uzun metodlar parçalanmalı
- Çok fazla parametre alan metodlardan kaçınılmalı

Pratik hedef:

- metodlar mümkün olduğunca kısa tutulmalı
- bir metod tek akışı anlatmalı
- bir sınıf hem veritabanı hem iş kuralı hem de UI işi yapmamalı

### Global Veri Kullanımını Azalt

- Gereksiz `static` durum tutma
- Her yerden erişilen değişkenler oluşturma
- Modülleri birbirine gizli bağımlılıklarla bağlama

Uygun yaklaşım:

- ihtiyaç duyulan bağımlılığı açıkça ilgili sınıfa ver
- sabitler için ayrı constant alanları kullan

### Magic Number Kullanma

Projede özellikle tekrar sistemi sayısal sabitler içerir. Bunlar doğrudan kod içine dağınık şekilde yazılmamalıdır.

Örnek:

- `STAGE_COUNT = 6`
- `ONE_DAY`
- `ONE_WEEK`
- `ONE_MONTH`
- `THREE_MONTHS`
- `ONE_YEAR`

Bu yaklaşım kodun hem anlaşılmasını hem de değiştirilmesini kolaylaştırır.

## Bu Projede Nasıl Uygulanmalı

### Kullanıcı Modülü

- kayıt, giriş ve şifre doğrulama ayrı sorumluluklarla ele alınmalı
- açık şifre asla saklanmamalı
- parola yönetimi tek bir yardımcı sınıf üzerinden yapılmalı

### Kelime Modülü

- kelime ekleme, düzenleme ve silme tek yerde toplanmalı
- örnek cümle ilişkisi açık tanımlanmalı
- görsel yolu saklama mantığı veri modelinde net olmalı

### Quiz ve SRS Modülü

- soru seçimi ile cevap değerlendirme ayrılmalı
- tekrar zamanı hesaplama tek bir sınıfta toplanmalı
- yanlış cevapta sıfırlama kuralı merkezi olarak uygulanmalı

### Ayarlar ve Raporlama

- ayar okuma ve ayar yazma işlemleri sade tutulmalı
- raporlama kodu UI içinde hesaplanmamalı
- yüzdelik hesaplar ayrı yardımcı metotlarda toplanmalı

## Yorum Yazımı

- Yorumlar gereksiz açıklama olmamalı
- Kodun ne yaptığı açıkça isimlerden anlaşılabiliyorsa yorum eklenmemeli
- Yorum gerekiyorsa neden o yaklaşımın seçildiğini açıklamalı

Kötü yorum örneği:

- `// kullanıcının adını ata`

İyi yorum örneği:

- `// Yanlış cevapta kelime ilerlemesini sıfırlayarak tekrar döngüsünü başlat`

## Refactor Disiplini

Bu projede refactor sonradan yapılacak ayrı bir iş gibi görülmemelidir.

Uygun yaklaşım:

- her story bitiminde tekrar eden kodları temizlemek
- uzun metodları bölmek
- isimlendirmeleri düzeltmek
- gereksiz dosya ve sınıfları silmek

## Hoca Açısından Güçlü Görünecek Noktalar

- commit geçmişinin parça parça ilerlemesi
- story bazlı geliştirme
- gereksiz karmaşıklıktan kaçınılması
- şifre saklamada güvenli yaklaşım kullanılması
- kod kalitesi ve clean code kararlarının dokümante edilmesi

## Sonuç

Bu proje temiz kod açısından güçlü görünmek istiyorsa şu çizgiyi korumalıdır:

- az ama net dosya yapısı
- açık isimlendirme
- küçük metodlar
- düşük tekrar
- düşük karmaşıklık
- sürekli refactor
- güvenli parola yönetimi

Kodun sade görünmesi bu proje için teknik olarak da akademik olarak da artı puandır.
