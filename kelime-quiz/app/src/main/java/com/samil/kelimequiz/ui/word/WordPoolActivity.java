package com.samil.kelimequiz.ui.word;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.domain.model.WordDetails;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.main.WordCardAdapter;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

import java.util.List;

public class WordPoolActivity extends AppCompatActivity implements WordCardAdapter.WordActionListener {
    private WordCardAdapter wordAdapter;
    private TextView tvEmptyState;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_pool);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClose();
            return;
        }

        tvEmptyState = findViewById(R.id.tvEmptyState);
        RecyclerView rvWords = findViewById(R.id.rvWords);
        wordAdapter = new WordCardAdapter(this);
        rvWords.setAdapter(wordAdapter);

        NavigationHelper.bindTopBar(this, false);
        NavigationHelper.bindBottomBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            loadWords();
        }
    }

    private void loadWords() {
        int userId = sessionManager.getUserId();
        tvEmptyState.setText("Kelime havuzu hazırlanıyor...");
        AppExecutors.io().execute(() -> {
            AppContainer.from(this).wordRepository.addInitialSeedWords(userId);
            List<WordEntity> words = AppContainer.from(this).wordRepository.listWords(userId);
            runOnUiThread(() -> {
                wordAdapter.setWords(words);
                tvEmptyState.setText(words.isEmpty() ? "Henüz kelime eklenmedi." : "Kelime kartlarına dokunarak detayları görebilirsin.");
            });
        });
    }

    @Override
    public void onDetailRequested(WordEntity word) {
        int userId = sessionManager.getUserId();
        AppExecutors.io().execute(() -> {
            WordDetails details = AppContainer.from(this).wordRepository.getWordDetails(userId, word.wordId);
            runOnUiThread(() -> showWordDialog(details));
        });
    }

    @Override
    public void onDeleteRequested(WordEntity word) {
        new AlertDialog.Builder(this)
                .setTitle("Kelimeyi sil")
                .setMessage(word.engWord + " kelimesini silmek istiyor musun?")
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton("Sil", (dialog, which) -> deleteWord(word.wordId))
                .show();
    }

    private void showWordDialog(WordDetails details) {
        WordDetailBottomSheet bottomSheet = new WordDetailBottomSheet(details);
        bottomSheet.show(getSupportFragmentManager(), "WordDetail");
    }

    private String buildSamplesText(WordDetails details) {
        if (details.getSampleTexts().isEmpty()) {
            return "Örnek cümle yok.";
        }

        StringBuilder builder = new StringBuilder();
        for (String sample : details.getSampleTexts()) {
            builder.append("- ").append(sample).append("\n");
        }
        return builder.toString().trim();
    }

    private void bindWordImage(ImageView imageView, String picturePath) {
        if (picturePath == null || picturePath.trim().isEmpty()) {
            imageView.setVisibility(View.GONE);
            return;
        }

        Glide.with(this).load(picturePath).into(imageView);
        imageView.setVisibility(View.VISIBLE);
    }

    private void deleteWord(int wordId) {
        int userId = sessionManager.getUserId();
        AppExecutors.io().execute(() -> {
            AppContainer.from(this).wordRepository.deleteWord(userId, wordId);
            runOnUiThread(this::loadWords);
        });
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
