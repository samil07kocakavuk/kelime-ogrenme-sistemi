package com.samil.kelimequiz.data.repository;

import com.samil.kelimequiz.data.local.dao.QuizProgressDao;
import com.samil.kelimequiz.data.local.dao.WordDao;
import com.samil.kelimequiz.data.local.entity.QuizProgressEntity;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.domain.model.QuizAnswerResult;
import com.samil.kelimequiz.domain.model.QuizQuestion;
import com.samil.kelimequiz.domain.model.QuizSummary;
import com.samil.kelimequiz.domain.service.SrsScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class QuizRepository {
    private static final int OPTION_COUNT = 4;

    private final WordDao wordDao;
    private final QuizProgressDao quizProgressDao;
    private final SrsScheduler srsScheduler;

    public QuizRepository(WordDao wordDao, QuizProgressDao quizProgressDao) {
        this.wordDao = wordDao;
        this.quizProgressDao = quizProgressDao;
        this.srsScheduler = new SrsScheduler();
    }

    public List<QuizQuestion> startQuiz(int userId, int questionLimit) {
        List<WordEntity> selectedWords = selectQuizWords(userId, questionLimit);
        List<QuizQuestion> questions = new ArrayList<>();
        for (WordEntity word : selectedWords) {
            questions.add(toQuestion(userId, word));
        }
        return questions;
    }

    private List<WordEntity> selectQuizWords(int userId, int questionLimit) {
        long now = System.currentTimeMillis();
        List<WordEntity> selectedWords = new ArrayList<>(quizProgressDao.listDueWords(userId, now, questionLimit));
        int remainingLimit = questionLimit - selectedWords.size();
        if (remainingLimit > 0) {
            selectedWords.addAll(quizProgressDao.listNewWords(userId, remainingLimit));
        }
        return selectedWords;
    }

    public QuizAnswerResult answerQuestion(int userId, int wordId, String selectedAnswer) {
        long now = System.currentTimeMillis();
        WordEntity word = wordDao.findByUserAndId(userId, wordId);
        if (word == null) {
            throw new IllegalArgumentException("Kelime bulunamadı.");
        }

        QuizProgressEntity progress = findOrCreateProgress(userId, wordId);
        boolean correct = word.trWord.equals(selectedAnswer);
        updateProgressAfterAnswer(progress, correct, now);
        saveProgress(progress);
        return new QuizAnswerResult(correct, progress.level, progress.learned, progress.nextReviewAt);
    }

    public QuizSummary getSummary(int userId) {
        return new QuizSummary(
                wordDao.countByUser(userId),
                quizProgressDao.countActiveWords(userId),
                quizProgressDao.countLearnedWords(userId)
        );
    }

    private QuizQuestion toQuestion(int userId, WordEntity word) {
        Set<String> uniqueOptions = new LinkedHashSet<>();
        uniqueOptions.add(word.trWord);
        uniqueOptions.addAll(wordDao.listRandomTranslationsExcluding(userId, word.wordId, OPTION_COUNT - 1));

        List<String> options = new ArrayList<>(uniqueOptions);
        Collections.shuffle(options);
        return new QuizQuestion(word.wordId, word.engWord, word.trWord, options);
    }

    private void updateProgressAfterAnswer(QuizProgressEntity progress, boolean correct, long now) {
        if (correct) {
            progress.level = Math.min(progress.level + 1, SrsScheduler.MAX_LEVEL);
            progress.learned = srsScheduler.isLearned(progress.level);
            progress.nextReviewAt = progress.learned ? 0 : srsScheduler.calculateNextReviewAt(progress.level, now);
        } else {
            progress.level = 0;
            progress.learned = false;
            progress.nextReviewAt = now;
        }
        progress.updatedAt = now;
    }

    private QuizProgressEntity findOrCreateProgress(int userId, int wordId) {
        QuizProgressEntity progress = quizProgressDao.findByUserAndWord(userId, wordId);
        if (progress != null) {
            return progress;
        }

        QuizProgressEntity newProgress = new QuizProgressEntity();
        newProgress.userId = userId;
        newProgress.wordId = wordId;
        newProgress.level = 0;
        newProgress.nextReviewAt = 0;
        newProgress.learned = false;
        newProgress.updatedAt = System.currentTimeMillis();
        return newProgress;
    }

    private void saveProgress(QuizProgressEntity progress) {
        if (progress.progressId == 0) {
            progress.progressId = (int) quizProgressDao.insert(progress);
        } else {
            quizProgressDao.update(progress);
        }
    }

}
