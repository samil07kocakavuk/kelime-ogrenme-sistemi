package com.samil.kelimequiz.data.repository;

import android.content.Context;

import com.samil.kelimequiz.data.local.dao.WordDao;
import com.samil.kelimequiz.data.local.dao.WordSampleDao;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.data.local.entity.WordSampleEntity;
import com.samil.kelimequiz.domain.model.WordDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordRepository {
    private final Context context;
    private final WordDao wordDao;
    private final WordSampleDao wordSampleDao;

    public WordRepository(Context context, WordDao wordDao, WordSampleDao wordSampleDao) {
        this.context = context.getApplicationContext();
        this.wordDao = wordDao;
        this.wordSampleDao = wordSampleDao;
    }

    public int addInitialSeedWords(int userId) {
        if (!wordDao.listByUser(userId).isEmpty()) {
            return 0;
        }

        int importedCount = 0;
        for (SeedWord seedWord : loadAvailableSeedWords()) {
            addWord(userId, seedWord.engWord, seedWord.trWord, seedWord.picturePath, seedWord.samplesText);
            importedCount++;
        }
        return importedCount;
    }

    public void addWord(int userId, String engWord, String trWord, String picturePath, String samplesText) {
        String cleanEngWord = requireText(engWord, "İngilizce kelime boş bırakılamaz.");
        String cleanTrWord = requireText(trWord, "Türkçe karşılık boş bırakılamaz.");
        if (wordDao.findByUserAndEnglishWord(userId, cleanEngWord) != null) {
            throw new IllegalArgumentException("Bu İngilizce kelime zaten kayıtlı.");
        }

        WordEntity word = new WordEntity();
        word.userId = userId;
        word.engWord = cleanEngWord;
        word.trWord = cleanTrWord;
        word.picturePath = trimToNull(picturePath);
        word.createdAt = System.currentTimeMillis();
        int wordId = (int) wordDao.insert(word);
        insertSamples(wordId, samplesText);
    }

    public List<WordEntity> listWords(int userId) {
        return wordDao.listByUser(userId);
    }

    public WordDetails getWordDetails(int userId, int wordId) {
        WordEntity word = wordDao.findByUserAndId(userId, wordId);
        if (word == null) {
            throw new IllegalArgumentException("Kelime bulunamadı.");
        }

        List<WordSampleEntity> samples = wordSampleDao.listByWordId(wordId);
        List<String> sampleTexts = new ArrayList<>();
        for (WordSampleEntity sample : samples) {
            sampleTexts.add(sample.sampleText);
        }

        return new WordDetails(word.wordId, word.engWord, word.trWord, word.picturePath, sampleTexts);
    }

    public void deleteWord(int userId, int wordId) {
        WordEntity word = wordDao.findByUserAndId(userId, wordId);
        if (word != null) {
            wordDao.delete(word);
        }
    }

    private void insertSamples(int wordId, String samplesText) {
        List<WordSampleEntity> samples = new ArrayList<>();
        for (String line : splitLines(samplesText)) {
            WordSampleEntity sample = new WordSampleEntity();
            sample.wordId = wordId;
            sample.sampleText = line;
            samples.add(sample);
        }
        if (!samples.isEmpty()) {
            wordSampleDao.insertAll(samples);
        }
    }

    private List<String> splitLines(String samplesText) {
        List<String> lines = new ArrayList<>();
        if (samplesText == null || samplesText.trim().isEmpty()) {
            return lines;
        }

        for (String line : samplesText.split("\\R")) {
            String cleanLine = trimToNull(line);
            if (cleanLine != null) {
                lines.add(cleanLine);
            }
        }
        return lines;
    }

    private String requireText(String value, String errorMessage) {
        String cleanValue = trimToNull(value);
        if (cleanValue == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return cleanValue;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<SeedWord> loadAvailableSeedWords() {
        try (InputStream inputStream = context.getAssets().open("seed_words_100.json");
             Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            String json = scanner.hasNext() ? scanner.next() : "[]";
            return parseSeedWords(json);
        } catch (Exception e) {
            throw new IllegalStateException("Hazır kelime havuzu yüklenemedi.", e);
        }
    }

    private List<SeedWord> parseSeedWords(String json) throws Exception {
        JSONArray items = new JSONArray(json);
        List<SeedWord> words = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            words.add(new SeedWord(
                    item.getString("engWord"),
                    item.getString("trWord"),
                    item.getString("picturePath"),
                    item.getString("samplesText")
            ));
        }
        return words;
    }

    private static final class SeedWord {
        private final String engWord;
        private final String trWord;
        private final String picturePath;
        private final String samplesText;

        private SeedWord(String engWord, String trWord, String picturePath, String samplesText) {
            this.engWord = engWord;
            this.trWord = trWord;
            this.picturePath = picturePath;
            this.samplesText = samplesText;
        }
    }
}
