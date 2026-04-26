package com.samil.kelimequiz.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.domain.model.QuizSummary;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.quiz.QuizActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

public class MainActivity extends AppCompatActivity {
    private MaterialButton btnStartQuiz;
    private int userId;
    private int totalWordCount;
    private boolean wordCountLoaded;

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

        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        userId = sessionManager.getUserId();
        btnStartQuiz.setEnabled(false);
        btnStartQuiz.setOnClickListener(v -> openQuizIfWordsExist());
        loadWordCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (btnStartQuiz != null) {
            loadWordCount();
        }
    }

    private void loadWordCount() {
        wordCountLoaded = false;
        btnStartQuiz.setEnabled(false);
        AppExecutors.io().execute(() -> {
            QuizSummary summary = AppContainer.from(this).quizRepository.getSummary(userId);
            runOnUiThread(() -> showWordCount(summary));
        });
    }

    private void showWordCount(QuizSummary summary) {
        wordCountLoaded = true;
        totalWordCount = summary.getTotalWords();
        btnStartQuiz.setText(totalWordCount == 0 ? R.string.add_word_first_action : R.string.start_quiz_action);
        btnStartQuiz.setEnabled(true);
    }

    private void openQuizIfWordsExist() {
        if (!wordCountLoaded) {
            Toast.makeText(this, R.string.quiz_summary_wait, Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalWordCount == 0) {
            Toast.makeText(this, R.string.quiz_requires_words, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, QuizActivity.class));
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
