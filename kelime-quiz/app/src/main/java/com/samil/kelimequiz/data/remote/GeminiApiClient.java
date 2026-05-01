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

public class GeminiApiClient {
    private static final String TEXT_URL = "https://text.pollinations.ai/";
    private static final String IMAGE_URL = "https://image.pollinations.ai/prompt/";

    public static String generateStory(List<String> words) throws Exception {
        String wordList = String.join(", ", words);
        String prompt = "Su 5 Ingilizce kelimeyi kullanarak kisa bir Turkce hikaye yaz (3-4 cumle). "
                + "Ingilizce kelimeleri hikaye icinde aynen koru, cevirme. "
                + "Sadece hikayeyi yaz, baska bir sey yazma. "
                + "Kelimeler: " + wordList;

        String encoded = URLEncoder.encode(prompt, "UTF-8");
        URL url = new URL(TEXT_URL + encoded);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        conn.disconnect();
        return sb.toString().trim();
    }

    public static Bitmap generateImage(String storyDescription) throws Exception {
        String prompt = "colorful children book illustration for story: " + storyDescription;
        String encoded = URLEncoder.encode(prompt, "UTF-8");
        URL url = new URL(IMAGE_URL + encoded + "?width=512&height=512&nologo=true");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(90000);
        conn.setInstanceFollowRedirects(true);

        InputStream is = conn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        conn.disconnect();

        if (bitmap == null) {
            throw new RuntimeException("Görsel oluşturulamadı.");
        }
        return bitmap;
    }
}
