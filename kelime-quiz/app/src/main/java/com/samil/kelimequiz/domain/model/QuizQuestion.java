package com.samil.kelimequiz.domain.model;

import java.util.List;

public class QuizQuestion {
    private final int wordId;
    private final String questionText;
    private final String correctAnswer;
    private final String picturePath;
    private final List<String> options;

    public QuizQuestion(int wordId, String questionText, String correctAnswer, String picturePath, List<String> options) {
        this.wordId = wordId;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.picturePath = picturePath;
        this.options = options;
    }

    public int getWordId() {
        return wordId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public List<String> getOptions() {
        return options;
    }
}
