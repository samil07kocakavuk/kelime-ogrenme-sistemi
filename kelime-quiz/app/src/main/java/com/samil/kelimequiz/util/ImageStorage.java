package com.samil.kelimequiz.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class ImageStorage {
    private static final String IMAGE_DIR = "word_images";

    private ImageStorage() {
    }

    public static String copyToAppStorage(Context context, Uri sourceUri) {
        try {
            File imageDirectory = new File(context.getFilesDir(), IMAGE_DIR);
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                throw new IllegalStateException("Görsel klasörü oluşturulamadı.");
            }

            File targetFile = new File(imageDirectory, "word_" + System.currentTimeMillis() + ".jpg");
            try (InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
                 OutputStream outputStream = new FileOutputStream(targetFile)) {
                if (inputStream == null) {
                    throw new IllegalStateException("Seçilen görsel okunamadı.");
                }
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return targetFile.getAbsolutePath();
        } catch (Exception e) {
            throw new IllegalStateException("Görsel kaydedilemedi.", e);
        }
    }
}
