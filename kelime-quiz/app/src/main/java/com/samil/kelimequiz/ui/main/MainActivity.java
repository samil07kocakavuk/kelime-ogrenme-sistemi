package com.samil.kelimequiz.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import com.samil.kelimequiz.domain.model.DayProgress;
import com.samil.kelimequiz.domain.model.QuizSummary;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.quiz.QuizActivity;
import com.samil.kelimequiz.ui.wordchain.WordChainActivity;
import com.samil.kelimequiz.ui.wordle.WordleActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private MaterialButton btnStartQuiz;
    private TextView tvUsernameMain;
    private TextView tvAverageLevelValue;
    private TextView tvGreeting;
    private TextView tvStreakValue;
    private TextView tvSuccessRateValue;
    private TextView tvWordCountValue;
    private TextView tvDailyWord;
    private TextView tvDailyWordMeaning;
    private LinearProgressIndicator lpiAverageLevel;
    private LinearLayout layoutBarChart;
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

        userId = sessionManager.getUserId();

        // Stale session kontrolü (DB reset sonrası)
        AppExecutors.io().execute(() -> {
            AppContainer container = AppContainer.from(this);
            UserEntity user = container.authRepository.findUserById(userId);
            if (user == null) {
                runOnUiThread(() -> {
                    sessionManager.clear();
                    openLoginAndClose();
                });
                return;
            }

            runOnUiThread(() -> {
                NavigationHelper.bindBottomBar(this);
                btnStartQuiz = findViewById(R.id.btnStartQuiz);
                tvUsernameMain = findViewById(R.id.tvUsernameMain);
                tvAverageLevelValue = findViewById(R.id.tvAverageLevelValue);
                tvGreeting = findViewById(R.id.tvGreeting);
                tvStreakValue = findViewById(R.id.tvStreakValue);
                tvSuccessRateValue = findViewById(R.id.tvSuccessRateValue);
                tvWordCountValue = findViewById(R.id.tvWordCountValue);
                tvDailyWord = findViewById(R.id.tvDailyWord);
                tvDailyWordMeaning = findViewById(R.id.tvDailyWordMeaning);
                lpiAverageLevel = findViewById(R.id.lpiAverageLevel);
                layoutBarChart = findViewById(R.id.layoutBarChart);
                
                tvUsernameMain.setText(user.username);
                updateGreeting();
                
                btnStartQuiz.setEnabled(false);
                btnStartQuiz.setOnClickListener(v -> openQuizIfWordsExist());

                MaterialButton btnDailyWordle = findViewById(R.id.btnDailyWordle);
                btnDailyWordle.setOnClickListener(v -> startActivity(new Intent(this, WordleActivity.class)));

                MaterialButton btnWordChain = findViewById(R.id.btnWordChain);
                btnWordChain.setOnClickListener(v -> startActivity(new Intent(this, WordChainActivity.class)));

                loadUserData();
            });
        });
    }

    private void updateGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) greeting = "Günaydın,";
        else if (hour >= 12 && hour < 18) greeting = "Tünaydın,";
        else if (hour >= 18 && hour < 22) greeting = "İyi Akşamlar,";
        else greeting = "İyi Geceler,";
        
        if (tvGreeting != null) tvGreeting.setText(greeting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (btnStartQuiz != null) {
            loadUserData();
            updateGreeting();
        }
    }

    private void loadUserData() {
        wordCountLoaded = false;
        btnStartQuiz.setEnabled(false);
        AppExecutors.io().execute(() -> {
            AppContainer container = AppContainer.from(this);
            UserEntity user = container.authRepository.findUserById(userId);
            if (user != null) {
                container.authRepository.checkAndUpdateStreak(user);
                // Güncellenmiş veriyi tekrar çek
                user = container.authRepository.findUserById(userId);
            }
            
            QuizSummary summary = container.quizRepository.getSummary(userId);
            double avgLevel = container.quizRepository.getGlobalAverageLevel(userId);
            int levelOneCount = container.quizRepository.countLevelOneWords(userId);
            double averageSuccessRate = container.quizRepository.getAverageSuccessRate(userId);
            List<DayProgress> weeklyProgress = container.quizRepository.getWeeklyProgress(userId);
            
            final UserEntity finalUser = user;
            
            runOnUiThread(() -> {
                showWordCount(summary);
                updateAverageLevel(avgLevel);
                updateWeeklyProgress(weeklyProgress);
                if (tvStreakValue != null && finalUser != null) {
                    tvStreakValue.setText(finalUser.currentStreak + " Gün Seri");
                }
                if (tvSuccessRateValue != null) tvSuccessRateValue.setText(String.format(Locale.US, "%%%.0f Başarı", averageSuccessRate));
                if (tvWordCountValue != null) tvWordCountValue.setText(levelOneCount + " Kelime");
            });
        });
    }

    private void updateAverageLevel(double avgLevel) {
        if (tvAverageLevelValue != null) {
            tvAverageLevelValue.setText(String.format(Locale.US, "%.1f", avgLevel));
        }
        if (lpiAverageLevel != null) {
            int progress = (int) ((avgLevel * 100) / 6.0);
            lpiAverageLevel.setProgressCompat(progress, true);
        }
    }

    private void updateWeeklyProgress(List<DayProgress> progressList) {
        if (layoutBarChart == null) return;
        layoutBarChart.removeAllViews();

        for (DayProgress dp : progressList) {
            View itemView = getLayoutInflater().inflate(R.layout.item_bar_chart, layoutBarChart, false);
            View viewBar = itemView.findViewById(R.id.viewBar);
            TextView tvDayName = itemView.findViewById(R.id.tvDayName);

            tvDayName.setText(dp.day);

            // Başarı oranına göre bar yüksekliğini ayarla (Max 100dp)
            float density = getResources().getDisplayMetrics().density;
            int maxHeightPx = (int) (100 * density);
            int barHeight = (int) (dp.avgSuccess * maxHeightPx / 100.0);
            
            // Minimum görünürlük için 4dp
            if (dp.avgSuccess > 0 && barHeight < (int)(4 * density)) {
                barHeight = (int)(4 * density);
            }

            ViewGroup.LayoutParams params = viewBar.getLayoutParams();
            params.height = barHeight;
            viewBar.setLayoutParams(params);

            layoutBarChart.addView(itemView);
        }
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
