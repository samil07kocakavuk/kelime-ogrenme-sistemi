package com.samil.kelimequiz.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.domain.model.QuizAnswerResult;
import com.samil.kelimequiz.domain.model.QuizQuestion;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.QuizSettingsManager;
import com.samil.kelimequiz.util.SessionManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private final List<MaterialButton> optionButtons = new ArrayList<>();
    private final List<QuizQuestion> questions = new ArrayList<>();

    private TextView tvQuizProgress;
    private TextView tvQuestionWord;
    private TextView tvQuizFeedback;
    private TextView tvLevelLabel;
    private ImageView ivQuizWordImage;
    private LinearProgressIndicator lpiWordLevel;
    private MaterialButton btnNextQuestion;
    private MaterialButton btnFinishQuiz;

    private int userId;
    private int currentIndex;
    private int correctCount;
    private boolean answered;
    private String lastSelectedAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClose();
            return;
        }
        userId = sessionManager.getUserId();

        bindViews();
        NavigationHelper.bindTopBar(this);
        loadQuiz();
    }

    private void bindViews() {
        tvQuizProgress = findViewById(R.id.tvQuizProgress);
        tvQuestionWord = findViewById(R.id.tvQuestionWord);
        tvQuizFeedback = findViewById(R.id.tvQuizFeedback);
        tvLevelLabel = findViewById(R.id.tvLevelLabel);
        ivQuizWordImage = findViewById(R.id.ivQuizWordImage);
        lpiWordLevel = findViewById(R.id.lpiWordLevel);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        btnFinishQuiz = findViewById(R.id.btnFinishQuiz);

        optionButtons.add(findViewById(R.id.btnOptionOne));
        optionButtons.add(findViewById(R.id.btnOptionTwo));
        optionButtons.add(findViewById(R.id.btnOptionThree));
        optionButtons.add(findViewById(R.id.btnOptionFour));

        btnNextQuestion.setOnClickListener(v -> showNextQuestion());
        btnFinishQuiz.setOnClickListener(v -> finish());
    }

    private void loadQuiz() {
        int questionLimit = new QuizSettingsManager(this).getQuestionLimit();
        tvQuizFeedback.setText(R.string.quiz_loading);
        AppExecutors.io().execute(() -> {
            List<QuizQuestion> loadedQuestions = AppContainer.from(this).quizRepository.startQuiz(userId, questionLimit);
            runOnUiThread(() -> showQuiz(loadedQuestions));
        });
    }

    private void showQuiz(List<QuizQuestion> loadedQuestions) {
        questions.clear();
        questions.addAll(loadedQuestions);
        currentIndex = 0;
        correctCount = 0;

        if (questions.isEmpty()) {
            showEmptyQuiz();
            return;
        }
        showCurrentQuestion();
    }

    private void showCurrentQuestion() {
        answered = false;
        lastSelectedAnswer = null;
        QuizQuestion question = questions.get(currentIndex);
        tvQuizProgress.setText(getString(R.string.quiz_progress_format, currentIndex + 1, questions.size()));
        tvQuestionWord.setText(question.getQuestionText());
        showQuestionImage(question.getPicturePath());
        updateLevelStatus(question.getLevel());
        tvQuizFeedback.setText(R.string.choose_turkish_answer);
        tvQuizFeedback.setBackgroundResource(R.drawable.bg_feedback_neutral);
        tvQuizFeedback.setTextColor(getColor(R.color.text_secondary));
        btnNextQuestion.setVisibility(View.GONE);
        btnFinishQuiz.setVisibility(View.GONE);

        List<String> options = question.getOptions();
        for (int i = 0; i < optionButtons.size(); i++) {
            MaterialButton button = optionButtons.get(i);
            if (i >= options.size()) {
                button.setVisibility(View.GONE);
                continue;
            }
            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
            button.setText(options.get(i));
            resetOptionStyle(button);
            button.setOnClickListener(v -> submitAnswer(question, ((MaterialButton) v).getText().toString()));
        }
    }

    private void updateLevelStatus(int level) {
        if (tvLevelLabel != null) {
            tvLevelLabel.setText(getString(R.string.level_format_label, level, 6));
        }
        if (lpiWordLevel != null) {
            // Level 0-6 arasını 0-100 arasına mapliyoruz
            int progress = (level * 100) / 6;
            lpiWordLevel.setProgressCompat(progress, true);
        }
    }

    private void submitAnswer(QuizQuestion question, String selectedAnswer) {
        if (answered) {
            return;
        }
        answered = true;
        lastSelectedAnswer = selectedAnswer.trim();
        tvQuizFeedback.setText(R.string.saving_answer);
        tvQuizFeedback.setBackgroundResource(R.drawable.bg_feedback_neutral);
        AppExecutors.io().execute(() -> {
            try {
                QuizAnswerResult result = AppContainer.from(this).quizRepository.answerQuestion(
                        userId,
                        question.getWordId(),
                        selectedAnswer
                );
                runOnUiThread(() -> {
                    updateLevelStatus(result.getLevel());
                    showAnswerResult(result);
                });
            } catch (RuntimeException exception) {
                runOnUiThread(() -> showAnswerError());
            }
        });
    }

    private void showAnswerResult(QuizAnswerResult result) {
        if (result.isCorrect()) {
            correctCount++;
        }
        applyAnswerStyles(result.isCorrect(), questions.get(currentIndex).getCorrectAnswer());
        tvQuizFeedback.setText(buildResultMessage(result));
        tvQuizFeedback.setBackgroundResource(result.isCorrect()
                ? R.drawable.bg_feedback_success
                : R.drawable.bg_feedback_error);
        tvQuizFeedback.setTextColor(getColor(result.isCorrect() ? R.color.success : R.color.error));
        if (currentIndex == questions.size() - 1) {
            btnFinishQuiz.setVisibility(View.VISIBLE);
            btnFinishQuiz.setText(getString(R.string.finish_quiz_score, correctCount, questions.size()));
        } else {
            btnNextQuestion.setVisibility(View.VISIBLE);
        }
    }

    private String buildResultMessage(QuizAnswerResult result) {
        if (!result.isCorrect()) {
            return getString(R.string.wrong_answer_feedback);
        }
        if (result.isLearned()) {
            return getString(R.string.learned_answer_feedback);
        }
        return getString(
                R.string.correct_answer_feedback,
                result.getLevel(),
                DateFormat.getDateInstance(DateFormat.MEDIUM).format(result.getNextReviewAt())
        );
    }

    private void showAnswerError() {
        answered = false;
        tvQuizFeedback.setText(R.string.answer_save_error);
        tvQuizFeedback.setBackgroundResource(R.drawable.bg_feedback_error);
        tvQuizFeedback.setTextColor(getColor(R.color.error));
    }

    private void showQuestionImage(String picturePath) {
        if (picturePath == null || picturePath.trim().isEmpty()) {
            ivQuizWordImage.setVisibility(View.GONE);
            return;
        }

        ivQuizWordImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(picturePath)
                .thumbnail(0.25f)
                .override(216, 216)
                .centerCrop()
                .dontAnimate()
                .into(ivQuizWordImage);
    }

    private void applyAnswerStyles(boolean correct, String correctAnswer) {
        for (MaterialButton button : optionButtons) {
            String option = button.getText().toString();
            if (option.equals(correctAnswer)) {
                applyOptionStyle(button, R.drawable.bg_quiz_option_success, R.color.text_on_primary);
            } else if (!correct && option.equals(lastSelectedAnswer)) {
                applyOptionStyle(button, R.drawable.bg_quiz_option_error, R.color.text_on_primary);
            }
        }
    }

    private void resetOptionStyle(MaterialButton button) {
        button.setBackgroundTintList(null);
        button.setBackgroundResource(R.drawable.bg_quiz_option);
        button.setTextColor(getColor(R.color.quiz_option_text));
    }

    private void applyOptionStyle(MaterialButton button, int backgroundDrawableRes, int textColorRes) {
        button.setBackgroundTintList(null);
        button.setBackgroundResource(backgroundDrawableRes);
        button.setTextColor(getColor(textColorRes));
    }

    private void showNextQuestion() {
        currentIndex++;
        showCurrentQuestion();
    }

    private void showEmptyQuiz() {
        tvQuizProgress.setText(R.string.empty_quiz_progress);
        tvQuestionWord.setText(R.string.empty_quiz_title);
        ivQuizWordImage.setVisibility(View.GONE);
        tvQuizFeedback.setText(R.string.empty_quiz_message);
        setOptionsVisible(false);
        btnNextQuestion.setVisibility(View.GONE);
        btnFinishQuiz.setVisibility(View.VISIBLE);
    }

    private void setOptionsVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        for (MaterialButton button : optionButtons) {
            button.setVisibility(visibility);
        }
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
