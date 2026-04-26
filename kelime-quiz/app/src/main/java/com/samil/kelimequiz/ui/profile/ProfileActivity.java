package com.samil.kelimequiz.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.word.WordPoolActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.QuizSettingsManager;
import com.samil.kelimequiz.util.SessionManager;
import com.samil.kelimequiz.util.ThemeManager;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileInfo;
    private TextView tvThemeMode;
    private TextView tvQuizQuestionLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClose();
            return;
        }

        tvProfileInfo = findViewById(R.id.tvProfileInfo);
        MaterialButton btnWordPool = findViewById(R.id.btnWordPool);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnThemeLight = findViewById(R.id.btnThemeLight);
        MaterialButton btnThemeDark = findViewById(R.id.btnThemeDark);
        MaterialButton btnDecreaseQuizLimit = findViewById(R.id.btnDecreaseQuizLimit);
        MaterialButton btnIncreaseQuizLimit = findViewById(R.id.btnIncreaseQuizLimit);
        tvThemeMode = findViewById(R.id.tvThemeMode);
        tvQuizQuestionLimit = findViewById(R.id.tvQuizQuestionLimit);

        NavigationHelper.bindTopBar(this, false);
        NavigationHelper.bindBottomBar(this);
        bindThemeSettings(btnThemeLight, btnThemeDark);
        bindQuizLimitSettings(btnDecreaseQuizLimit, btnIncreaseQuizLimit);
        btnWordPool.setOnClickListener(v -> startActivity(new Intent(this, WordPoolActivity.class)));
        btnLogout.setOnClickListener(v -> {
            sessionManager.clear();
            openLoginAndClose();
        });

        int userId = sessionManager.getUserId();
        AppExecutors.io().execute(() -> {
            UserEntity user = AppContainer.from(this).authRepository.findUserById(userId);
            runOnUiThread(() -> showUser(user));
        });
    }

    private void bindThemeSettings(MaterialButton btnThemeLight, MaterialButton btnThemeDark) {
        showThemeMode();
        btnThemeLight.setOnClickListener(v -> changeTheme(ThemeManager.THEME_LIGHT));
        btnThemeDark.setOnClickListener(v -> changeTheme(ThemeManager.THEME_DARK));
    }

    private void changeTheme(int themeMode) {
        if (ThemeManager.getSavedTheme(this) == themeMode) {
            showThemeMode();
            return;
        }
        ThemeManager.saveAndApplyTheme(this, themeMode);
    }

    private void showThemeMode() {
        boolean darkTheme = ThemeManager.getSavedTheme(this) == ThemeManager.THEME_DARK;
        tvThemeMode.setText(darkTheme ? R.string.theme_mode_dark : R.string.theme_mode_light);
    }

    private void bindQuizLimitSettings(MaterialButton btnDecreaseQuizLimit, MaterialButton btnIncreaseQuizLimit) {
        QuizSettingsManager settingsManager = new QuizSettingsManager(this);
        showQuizQuestionLimit(settingsManager.getQuestionLimit());
        btnDecreaseQuizLimit.setOnClickListener(v -> showQuizQuestionLimit(settingsManager.decreaseQuestionLimit()));
        btnIncreaseQuizLimit.setOnClickListener(v -> showQuizQuestionLimit(settingsManager.increaseQuestionLimit()));
    }

    private void showQuizQuestionLimit(int questionLimit) {
        tvQuizQuestionLimit.setText(String.valueOf(questionLimit));
    }

    private void showUser(UserEntity user) {
        if (user == null) {
            tvProfileInfo.setText(R.string.profile_not_found);
            return;
        }
        tvProfileInfo.setText(getString(R.string.profile_username, user.username));
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
