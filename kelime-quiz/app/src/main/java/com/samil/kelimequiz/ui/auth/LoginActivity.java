package com.samil.kelimequiz.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.domain.model.AuthResult;
import com.samil.kelimequiz.ui.main.MainActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextView tvStatusMessage;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openMainAndClose();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnGoRegister = findViewById(R.id.btnGoRegister);
        MaterialButton btnForgotPassword = findViewById(R.id.btnForgotPassword);

        AppExecutors.io().execute(() -> AppContainer.from(this));
        btnLogin.setOnClickListener(v -> login());
        btnGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        btnForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void login() {
        String username = getInput(etUsername);
        String password = getInput(etPassword);
        showStatus("Kontrol ediliyor...");
        btnLogin.setEnabled(false);
        AppExecutors.io().execute(() -> {
            AuthResult result = AppContainer.from(this).authRepository.login(username, password);
            runOnUiThread(() -> handleAuthResult(result));
        });
    }

    private void handleAuthResult(AuthResult result) {
        btnLogin.setEnabled(true);
        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        sessionManager.saveUserId(result.getUserId());
        showStatus("Giriş başarılı.");
        openMainAndClose();
    }

    private void showStatus(String message) {
        tvStatusMessage.setText(message);
    }

    private String getInput(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }

    private void openMainAndClose() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
