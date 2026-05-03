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
        String prompt = "Su 5 Ingilizce kelimeyi kullanarak kisa bir Turkce hikaye yaz (3-4 cumle). "
                + "Ingilizce kelimeleri hikaye icinde aynen koru, cevirme. "
                + "Sadece hikayeyi yaz, baska bir sey yazma. "
                + "Kelimeler: " + wordList;

        String encoded = URLEncoder.encode(prompt, "UTF-8");
        URL url = new URL(TEXT_URL + encoded);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder storyBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            storyBuilder.append(line).append("\n");
        }
        reader.close();
        connection.disconnect();
        return storyBuilder.toString().trim();
    }

    public static Bitmap generateImage(String storyDescription) throws Exception {
        String prompt = "colorful children book illustration for story: " + storyDescription;
        String encoded = URLEncoder.encode(prompt, "UTF-8");
        URL url = new URL(IMAGE_URL + encoded + "?width=512&height=512&nologo=true");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(90000);
        connection.setInstanceFollowRedirects(true);

        InputStream inputStream = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        connection.disconnect();

        if (bitmap == null) {
            throw new RuntimeException("Görsel oluşturulamadı.");
        }
        return bitmap;
    }
}
