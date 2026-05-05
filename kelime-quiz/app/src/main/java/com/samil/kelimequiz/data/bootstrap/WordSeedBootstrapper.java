package com.samil.kelimequiz.data.bootstrap;

import android.content.Context;

import com.samil.kelimequiz.data.repository.WordRepository;
import com.samil.kelimequiz.domain.model.WordLevel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordSeedBootstrapper {
    private final Context context;
    private final WordRepository wordRepository;

    public WordSeedBootstrapper(Context context, WordRepository wordRepository) {
        this.context = context.getApplicationContext();
        this.wordRepository = wordRepository;
    }

    public int ensureSeedWords(int userId) {
        int importedCount = 0;
        for (SeedWord seedWord : loadSeedWords()) {
            boolean inserted = wordRepository.addWord(
                    userId,
                    seedWord.engWord,
                    seedWord.trWord,
                    seedWord.picturePath,
                    seedWord.samplesText,
                    seedWord.category,
                    seedWord.cefrLevel
            );
            if (inserted) {
                importedCount++;
            }
        }
        return importedCount;
    }

    private List<SeedWord> loadSeedWords() {
        try (InputStream inputStream = context.getAssets().open("seed_words_100.json");
             Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            String json = scanner.hasNext() ? scanner.next() : "[]";
            return parseSeedWords(json);
        } catch (Exception e) {
            throw new IllegalStateException("Hazir kelime havuzu yuklenemedi.", e);
        }
    }

    private List<SeedWord> parseSeedWords(String json) throws Exception {
        JSONArray items = new JSONArray(json);
        List<SeedWord> words = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String category = item.optString("category", "Günlük Yaşam");
            words.add(new SeedWord(
                    item.optString("engWord", ""),
                    item.optString("trWord", ""),
                    item.optString("picturePath", null),
                    item.optString("samplesText", ""),
                    category,
                    item.optString("cefrLevel", WordLevel.fromCategory(category).name())
            ));
        }
        return words;
    }

    private static final class SeedWord {
        private final String engWord;
        private final String trWord;
        private final String picturePath;
        private final String samplesText;
        private final String category;
        private final String cefrLevel;

        private SeedWord(String engWord, String trWord, String picturePath, String samplesText, String category, String cefrLevel) {
            this.engWord = engWord;
            this.trWord = trWord;
            this.picturePath = picturePath;
            this.samplesText = samplesText;
            this.category = category;
            this.cefrLevel = cefrLevel;
        }
    }
}
