package com.samil.kelimequiz.domain.model;

public class CategoryReport {
    private final String category;
    private final int totalWords;
    private final int correctWords;

    public CategoryReport(String category, int totalWords, int correctWords) {
        this.category = category;
        this.totalWords = totalWords;
        this.correctWords = correctWords;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public int getCorrectWords() {
        return correctWords;
    }

    public int getSuccessPercent() {
        if (totalWords == 0) {
            return 0;
        }
        return (correctWords * 100) / totalWords;
    }
}
