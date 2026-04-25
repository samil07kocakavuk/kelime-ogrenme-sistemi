package com.samil.kelimequiz.ui.auth;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.domain.model.AuthResult;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etPasswordRepeat;
    private TextInputLayout tilPassword;
    private TextInputLayout tilPasswordRepeat;
    private TextView tvStatusMessage;
    private MaterialButton btnRegister;
    private boolean passwordVisible;
    private boolean passwordRepeatVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordRepeat = findViewById(R.id.etPasswordRepeat);
        tilPassword = findViewById(R.id.tilPassword);
        tilPasswordRepeat = findViewById(R.id.tilPasswordRepeat);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        btnRegister = findViewById(R.id.btnRegister);

        NavigationHelper.bindTopBar(this);
        NavigationHelper.bindBottomBar(this);
        AppExecutors.io().execute(() -> AppContainer.from(this));
        bindPasswordToggles();
        btnRegister.setOnClickListener(v -> register());
    }

    private void bindPasswordToggles() {
        setPasswordVisible(false);
        setPasswordRepeatVisible(false);
        tilPassword.setEndIconOnClickListener(v -> setPasswordVisible(!passwordVisible));
        tilPasswordRepeat.setEndIconOnClickListener(v -> setPasswordRepeatVisible(!passwordRepeatVisible));
    }

    private void setPasswordVisible(boolean visible) {
        passwordVisible = visible;
        etPassword.setTransformationMethod(visible
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance());
        tilPassword.setEndIconDrawable(visible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        etPassword.setSelection(etPassword.length());
    }

    private void setPasswordRepeatVisible(boolean visible) {
        passwordRepeatVisible = visible;
        etPasswordRepeat.setTransformationMethod(visible
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance());
        tilPasswordRepeat.setEndIconDrawable(visible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        etPasswordRepeat.setSelection(etPasswordRepeat.length());
    }

    private void register() {
        String username = getInput(etUsername);
        String password = getInput(etPassword);
        String passwordRepeat = getInput(etPasswordRepeat);
        if (!password.equals(passwordRepeat)) {
            showStatus("Şifreler eşleşmiyor.");
            return;
        }

        showStatus("Kayıt oluşturuluyor...");
        btnRegister.setEnabled(false);
        AppExecutors.io().execute(() -> {
            AuthResult result = AppContainer.from(this).authRepository.register(username, password);
            runOnUiThread(() -> handleRegisterResult(result));
        });
    }

    private void handleRegisterResult(AuthResult result) {
        btnRegister.setEnabled(true);
        if (!result.isSuccess()) {
            showStatus(result.getMessage());
            return;
        }

        showStatus("Kayıt başarılı.");
        finish();
    }

    private void showStatus(String message) {
        tvStatusMessage.setText(message);
    }

    private String getInput(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }
}
