# Kelime Quiz

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Android SDK 34](https://img.shields.io/badge/Android%20SDK-34-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Room Database](https://img.shields.io/badge/Room-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

Kelime Quiz, İngilizce kelime öğrenmeyi kullanıcı bazlı kelime havuzu, sınav, aralıklı tekrar, raporlama, Wordle ve LLM destekli hikaye üretimi ile birleştiren Android dönem projesidir. Projede amaç sadece kelime listelemek değil; kullanıcının kelimelerle tekrar tekrar karşılaşmasını, yanlış bildiği kelimelerin sürecinin sıfırlanmasını ve öğrenme durumunun kişiye özel takip edilmesini sağlamaktır.

## Proje Amacı

Uygulama İngilizce kelime ezberleme sürecini daha düzenli hale getirmek için geliştirildi. Her kullanıcı kendi hesabıyla giriş yapar, kendi kelime havuzunu kullanır ve quiz sonuçları sadece o kullanıcıya ait olarak saklanır. Böylece aynı kelime bir kullanıcı için öğrenilmiş sayılırken başka bir kullanıcı için hâlâ öğrenilmemiş olabilir.

Projenin ana hedefleri:

- Kullanıcı kayıt, giriş ve şifre sıfırlama akışını sağlamak.
- Kullanıcıya özel kelime havuzu oluşturmak.
- Kelimeleri görsel, kategori ve örnek cümleyle desteklemek.
- Quiz modülünde aralıklı tekrar mantığı kullanmak.
- Doğru ve yanlış cevaplara göre öğrenme seviyesini güncellemek.
- Kategori bazlı başarı raporu üretmek.
- Wordle ve LLM modülüyle öğrenmeyi daha etkileşimli hale getirmek.

## Geliştirme Akışı

Proje story mantığıyla ilerletildi. Commit geçmişi de bu sırayı gösterecek şekilde temizlendi; gereksiz README silme/güncelleme ve conflict commitleri kaldırıldı.

| Aşama | Yapılan İş |
| --- | --- |
| Proje dokümanları | Gereksinimler, clean code notları ve proje dökümanları düzenlendi. |
| Story 1 | Kullanıcı kayıt, giriş ve şifre sıfırlama ekranları eklendi. |
| Story 2 | Kelime ekleme, kelime havuzu, kelime detayları ve görsel destekli kelime yönetimi eklendi. |
| Tasarım | Açık/koyu tema, modern kart yapıları, alt menü, profil ve ekran düzenleri iyileştirildi. |
| Story 3-4 | Quiz, SRS tekrar sistemi, kullanıcı bazlı ilerleme ve raporlama eklendi. |
| Kelime havuzu | Kelime veritabanı 300 kelimeye çıkarıldı, kategoriler düzenlendi, görsel yükleme hızlandırıldı. |
| Story 6-7 | Wordle, ipucu sistemi, LLM destekli hikaye ve görsel üretimi eklendi. |

## Özellikler

- Kullanıcı kayıt olma
- Kullanıcı giriş yapma
- Şifre sıfırlama
- Giriş yapılmadan korumalı ekranlara erişimi engelleme
- Kullanıcıya özel kelime havuzu
- Kelime ekleme, silme ve detay görüntüleme
- Kelimeler için kategori, Türkçe karşılık, örnek cümle ve görsel desteği
- İlk giriş sonrası seed kelimelerin kullanıcı veritabanına aktarılması
- 300 kelimelik başlangıç havuzu
- 12 kategori ve her kategoride 25 kelime
- Quiz başlatma
- Quiz soru sayısını profil ayarlarından artırma/azaltma
- Doğru cevapta görsel geri bildirim
- Yanlış cevapta doğru seçeneği gösterme
- Öğrenilmiş kelimeleri normal quiz havuzundan çıkarma
- Kategori bazlı sınav başarı raporu
- Açık/koyu tema seçimi
- Wordle mini oyunu
- LLM destekli hikaye ve görsel üretimi

## Kullanılan Teknolojiler

| Teknoloji | Kullanım Nedeni |
| --- | --- |
| Java 17 | Android tarafında okunabilir, nesne yönelimli ve proje seviyesine uygun geliştirme için kullanıldı. |
| Android SDK 34 | Güncel Android API desteği için kullanıldı. |
| Room | SQLite işlemlerini DAO, Entity ve Database yapısıyla daha güvenli yönetmek için kullanıldı. |
| Material Components | Buton, kart ve form bileşenlerinde modern Android arayüzü için kullanıldı. |
| Glide | Kelime ve quiz görsellerini daha verimli yüklemek için kullanıldı. |
| JUnit | Yardımcı sınıfların test edilebilmesi için kullanıldı. |
| Pollinations tabanlı LLM endpointleri | Word Chain / LLM modülünde hikaye ve görsel üretimi için kullanıldı. |

## Mimari Yapı

Proje katmanlı yapıya yakın tutuldu. Amaç ekran kodlarının doğrudan veritabanı mantığıyla şişmesini engellemek ve iş kurallarını daha okunabilir parçalara ayırmaktır.

```text
kelime-quiz/
  app/src/main/java/com/samil/kelimequiz/
    data/
      local/        Room database, DAO ve entity sınıfları
      remote/       LLM metin ve görsel üretim istemcisi
      repository/   Veri erişimi ve iş akışı koordinasyonu
    domain/
      model/        Ekranlara taşınan iş modelleri
      service/      SRS tekrar tarihi hesaplama servisi
    ui/
      auth/         Giriş, kayıt, şifre sıfırlama
      main/         Ana ekran ve kelime kartları
      profile/      Profil, ayarlar ve raporlama
      quiz/         Quiz ekranı
      word/         Kelime ekleme ve kelime havuzu
      wordchain/    LLM modülü
      wordle/       Wordle mini oyunu
    util/           Session, tema, navigation, executor yardımcıları
```

## Veritabanı Yapısı

Room üzerinde temel olarak şu yapılar kullanıldı:

- `UserEntity`: Kullanıcı bilgileri ve şifre hash verisi.
- `WordEntity`: Kullanıcıya ait kelime, Türkçe karşılık, kategori ve görsel yolu.
- `WordSampleEntity`: Kelimeye bağlı örnek cümleler.
- `QuizProgressEntity`: Kullanıcı ve kelime bazlı öğrenme seviyesi, tekrar tarihi ve öğrenilmiş durumu.

Bu yapı sayesinde kelime havuzu ve quiz ilerlemesi kullanıcı bazlı tutulur. Bir kullanıcının doğru bildiği kelime başka bir kullanıcının ilerlemesini etkilemez.

## Quiz ve SRS Algoritması

Quiz modülü aralıklı tekrar prensibine göre çalışır. Bir kelimenin öğrenilmiş sayılması için kullanıcı aynı kelimeyi farklı tekrar zamanlarında 6 kez doğru bilmelidir.

Tekrar aralıkları:

| Seviye | Doğru Cevap Sonrası Tekrar |
| --- | --- |
| 1 | 1 gün sonra |
| 2 | 1 hafta sonra |
| 3 | 1 ay sonra |
| 4 | 3 ay sonra |
| 5 | 6 ay sonra |
| 6 | 1 yıl sonrası, kelime öğrenilmiş kabul edilir |

Temel kurallar:

- Doğru cevapta kelimenin seviyesi 1 artar.
- Yanlış cevapta kelimenin seviyesi sıfırlanır.
- Yanlış bilinen kelime öğrenilmiş sayılmaz.
- Öğrenilmiş kelime normal quiz havuzuna tekrar girmez.
- Quiz başlatıldığında önce tekrar zamanı gelen kelimeler seçilir.
- Eksik soru kalırsa daha önce kullanıcıya sorulmamış yeni kelimeler rastgele eklenir.

Bu algoritma `SrsScheduler`, `QuizRepository`, `QuizProgressDao` ve `QuizActivity` üzerinden uygulanır.

## Raporlama Mantığı

Raporlama öğrenilmiş kelimelere göre değil, sınav sonucuna göre çalışacak şekilde düzenlendi. Kullanıcı bir kelimeyi son quizde doğru bildiyse kategori başarısında doğru sayılır. Daha sonraki quizde aynı kelimeyi yanlış bilirse rapordaki durum da yanlış/başarısız yönde güncellenir.

Bu tercih özellikle proje mantığı için önemlidir; çünkü sadece öğrenilmiş kelimeye göre rapor yapılsaydı 6 tekrar ve uzun zaman aralıkları nedeniyle raporun anlamlı hale gelmesi çok uzun sürecekti.

## Kelime Havuzu

Başlangıç kelime havuzu `seed_words_100.json` dosyasında tutulur. Dosya adı eski kalsa da içerik güncel olarak 300 kelimedir.

Kategoriler:

- Hayvanlar
- Meslekler
- Sporlar
- Yiyecekler
- Ev ve Eşyalar
- Okul
- Ulaşım
- Doğa
- Sıfatlar
- Fiiller
- Sağlık ve Vücut
- Günlük Yaşam

Her kategoride 25 kelime vardır. Görseller OpenMoji CDN üzerinden `72x72` boyutunda alınır. Daha önce kullanılan büyük görseller yavaş açıldığı için küçük boyutlu görsellere geçildi ve Glide tarafında `thumbnail`, `override` ve `dontAnimate` ayarları kullanıldı.

## Wordle Algoritması

Wordle modülünde kullanıcı günlük kelimeyi tahmin eder. Kelimeler kullanıcının kelime havuzundan seçilir ve 4-6 harfli kelimeler tercih edilir.

Renk mantığı:

- Yeşil: Harf doğru yerde.
- Sarı: Harf kelimede var ama yanlış yerde.
- Gri: Harf kelimede yok.

Kullanıcıya 5 tahmin hakkı verilir. Klavye üzerinde harf durumları renklerle gösterilir ve ipucu hakkı sınırlandırılmıştır.

## LLM Modülü

LLM modülü önce kullanıcının kelime havuzundan rastgele 5 İngilizce kelime seçer. Bu kelimelerle kısa bir Türkçe hikaye üretilir; İngilizce kelimeler hikaye içinde çevrilmeden korunur. Ardından hikayeye uygun bir görsel oluşturulur.

Kod tarafında bu iş `LlmApiClient` sınıfı üzerinden yapılır. Sınıf adı bilinçli olarak servis sağlayıcı isminden bağımsız tutuldu. Böylece ileride kullanılan LLM servisi değişirse ekran ve domain tarafında isim karmaşası oluşmaz.

## Tasarım Kararları

- Giriş yapılmadan bottom menu gösterilmez.
- Kayıt ve şifre sıfırlama ekranlarında kullanıcı korumalı ekranlara geçemez.
- Quiz ana sayfadan başlatılır; bottom menüde ayrı quiz sekmesi tutulmaz.
- Profil ekranında kelime havuzu, raporlama ve ayarlar birlikte konumlandırıldı.
- Tema değişimi açık/koyu seçenekleriyle yapılır.
- Ekranlarda ortak top bar ve bottom nav yapıları kullanılır.
- Cevap doğru/yanlış durumlarında seçenek arka planı renklenir.

## Clean Code Yaklaşımı

Projede aşağıdaki kurallara dikkat edildi:

- Veritabanı işlemleri DAO üzerinden yapılır.
- Ekranlar doğrudan SQL veya karmaşık iş kuralı taşımaz.
- Kullanıcı oturumu `SessionManager` ile yönetilir.
- Tema işlemleri `ThemeManager` ile ayrıştırılır.
- Quiz ayarları `QuizSettingsManager` ile tutulur.
- Ağ ve veritabanı işlemleri ana thread dışında çalıştırılır.
- Sık kullanılan navigation davranışları `NavigationHelper` içine alınır.
- Şifreler düz metin değil hash ve salt yapısıyla saklanır.

## Kurulum ve Çalıştırma

Android Studio ile:

1. Projeyi açın.
2. `kelime-quiz` klasörünü Android proje kökü olarak seçin.
3. Gradle sync işlemini bekleyin.
4. Uygulamayı emulator veya gerçek cihazda çalıştırın.

Terminal ile:

```powershell
cd kelime-quiz
.\gradlew.bat assembleDebug
```

Test ve lint:

```powershell
cd kelime-quiz
.\gradlew.bat testDebugUnitTest lintDebug
```

Tam doğrulama:

```powershell
cd kelime-quiz
.\gradlew.bat clean assembleDebug testDebugUnitTest lintDebug
```

## Branch ve Commit Düzeni

Ana branch `main` branchidir. Geçmişteki çalışma branchleri korunmuştur:

- `story-2-kelime-havuzu`
- `tasarim`
- `story-3-quiz`
- `story-6-7-duzenlemeler`

Main geçmişi proje akışını gösterecek şekilde düzenlenmiştir. Gereksiz README silme/güncelleme ve conflict commitleri kaldırılmış, anlamlı geliştirme commitleri korunmuştur.

## Notlar

- `Scrum-Trello-Table*.png` dosyaları localde untracked durumdaysa proje commitlerine dahil edilmemiştir.
- LLM modülü internet bağlantısı gerektirir.
- Quiz ve raporlama verileri kullanıcı bazlıdır.
- Uygulama local Room veritabanı kullandığı için cihaz/emulator verisi silinirse kullanıcı verileri de sıfırlanır.
