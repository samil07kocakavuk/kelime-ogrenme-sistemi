package com.samil.kelimequiz.data.remote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LlmApiClient {
    private static final String TEXT_URL = "https://text.pollinations.ai/";
    private static final String IMAGE_URL = "https://image.pollinations.ai/prompt/";

    private LlmApiClient() {
    }

    public static String generateStory(List<String> words) throws Exception {
        String wordList = String.join(", ", words);
        String systemPrompt = "Sen bir yardimci egitim asistanisin. Sana verilen 5 Ingilizce kelimeyi kullanarak kisa bir Turkce hikaye yazmalisin (3-4 cumle). Ingilizce kelimeleri hikaye icinde aynen koru, cevirme. Sadece hikayeyi yaz.";
        String userPrompt = "Kelimeler: " + wordList;

        int maxRetries = 3;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                String encodedSystem = URLEncoder.encode(systemPrompt, "UTF-8").replace("+", "%20");
                String encodedUser = URLEncoder.encode(userPrompt, "UTF-8").replace("+", "%20");
                
                // Pollinations API query parameters for better control
                URL url = new URL(TEXT_URL + encodedUser + "?system=" + encodedSystem + "&model=openai");
                
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Add User-Agent to avoid blocks
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder storyBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        storyBuilder.append(line).append("\n");
                    }
                    reader.close();
                    connection.disconnect();
                    String result = storyBuilder.toString().trim();
                    if (!result.isEmpty()) return result;
                } else {
                    connection.disconnect();
                }
            } catch (Exception e) {
                lastException = e;
                // Wait a bit before retry
                Thread.sleep(1000 * (i + 1));
            }
        }
        throw (lastException != null) ? lastException : new RuntimeException("Hikaye olusturulamadi (API Hatasi)");
    }

    public static Bitmap generateImage(String storyDescription) throws Exception {
        String prompt = "colorful children book illustration, no text, " + storyDescription;
        int maxRetries = 2;
        Exception lastException = null;

        for (int i = 0; i < maxRetries; i++) {
            try {
                String encoded = URLEncoder.encode(prompt, "UTF-8").replace("+", "%20");
                URL url = new URL(IMAGE_URL + encoded + "?width=512&height=512&nologo=true&model=flux");
                
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(45000);
                connection.setInstanceFollowRedirects(true);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    connection.disconnect();
                    if (bitmap != null) return bitmap;
                } else {
                    connection.disconnect();
                }
            } catch (Exception e) {
                lastException = e;
                Thread.sleep(1500);
            }
        }
        throw (lastException != null) ? lastException : new RuntimeException("Gorsel olusturulamadi (API Hatasi)");
    }
}
