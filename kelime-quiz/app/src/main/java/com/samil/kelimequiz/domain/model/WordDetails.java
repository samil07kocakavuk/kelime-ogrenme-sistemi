package com.samil.kelimequiz.domain.model;

import java.util.List;

public class WordDetails {
    private final int wordId;
    private final String engWord;
    private final String trWord;
    private final String picturePath;
    private final List<String> sampleTexts;

    public WordDetails(int wordId, String engWord, String trWord, String picturePath, List<String> sampleTexts) {
        this.wordId = wordId;
        this.engWord = engWord;
        this.trWord = trWord;
        this.picturePath = picturePath;
        this.sampleTexts = sampleTexts;
    }

    public int getWordId() {
        return wordId;
    }

    public String getEngWord() {
        return engWord;
    }

    public String getTrWord() {
        return trWord;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public List<String> getSampleTexts() {
        return sampleTexts;
    }
}
