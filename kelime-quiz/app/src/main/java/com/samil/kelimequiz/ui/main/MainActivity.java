package com.samil.kelimequiz.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.domain.model.QuizSummary;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.quiz.QuizActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.QuizSettingsManager;
import com.samil.kelimequiz.util.SessionManager;

public class MainActivity extends AppCompatActivity {
    private TextView tvQuizLimit;
    private TextView tvQuizSummary;
    private MaterialButton btnStartQuiz;
    private int userId;
    private int totalWordCount;
    private boolean quizSummaryLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClose();
            return;
        }

        NavigationHelper.bindTopBar(this, false);
        NavigationHelper.bindBottomBar(this);

        tvQuizLimit = findViewById(R.id.tvQuizLimit);
        tvQuizSummary = findViewById(R.id.tvQuizSummary);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        userId = sessionManager.getUserId();
        btnStartQuiz.setEnabled(false);
        btnStartQuiz.setOnClickListener(v -> openQuizIfWordsExist());
        loadQuizSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tvQuizLimit != null) {
            loadQuizSummary();
        }
    }

    private void loadQuizSummary() {
        quizSummaryLoaded = false;
        btnStartQuiz.setEnabled(false);
        int questionLimit = new QuizSettingsManager(this).getQuestionLimit();
        tvQuizLimit.setText(getResources().getQuantityString(R.plurals.quiz_limit_message, questionLimit, questionLimit));
        tvQuizSummary.setText(R.string.quiz_summary_loading);
        AppExecutors.io().execute(() -> {
            QuizSummary summary = AppContainer.from(this).quizRepository.getSummary(userId);
            runOnUiThread(() -> showQuizSummary(summary));
        });
    }

    private void showQuizSummary(QuizSummary summary) {
        quizSummaryLoaded = true;
        totalWordCount = summary.getTotalWords();
        tvQuizSummary.setText(getString(
                R.string.quiz_summary_short,
                getString(R.string.active_words_label),
                summary.getActiveWords(),
                getString(R.string.learned_words_label),
                summary.getLearnedWords()
        ));
        btnStartQuiz.setText(totalWordCount == 0 ? R.string.add_word_first_action : R.string.start_quiz_action);
        btnStartQuiz.setEnabled(true);
    }

    private void openQuizIfWordsExist() {
        if (!quizSummaryLoaded) {
            tvQuizSummary.setText(R.string.quiz_summary_wait);
            return;
        }
        if (totalWordCount == 0) {
            tvQuizSummary.setText(R.string.quiz_requires_words);
            return;
        }
        startActivity(new Intent(this, QuizActivity.class));
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
