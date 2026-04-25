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
import com.samil.kelimequiz.util.SessionManager;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileInfo;

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

        NavigationHelper.bindTopBar(this, false);
        NavigationHelper.bindBottomBar(this);
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

    private void showUser(UserEntity user) {
        if (user == null) {
            tvProfileInfo.setText("Kullanıcı bilgisi bulunamadı.");
            return;
        }
        tvProfileInfo.setText("Kullanıcı adı: " + user.username);
    }

    @Override
    public void onBackPressed() {
        NavigationHelper.redirectToMain(this);
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
