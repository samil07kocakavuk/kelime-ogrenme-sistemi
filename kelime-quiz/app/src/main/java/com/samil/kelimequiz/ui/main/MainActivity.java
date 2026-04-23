package com.samil.kelimequiz.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.domain.model.WordDetails;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<WordEntity> loadedWords = new ArrayList<>();
    private WordCardAdapter wordAdapter;
    private TextView tvEmptyState;
    private ListView listWords;
    private MaterialButton btnViewWords;
    private MaterialButton btnUpdatePool;
    private MaterialButton btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClose();
            return;
        }

        tvEmptyState = findViewById(R.id.tvEmptyState);
        listWords = findViewById(R.id.listWords);
        wordAdapter = new WordCardAdapter(this, loadedWords, this::showWordDetails);
        listWords.setAdapter(wordAdapter);

        btnViewWords = findViewById(R.id.btnViewWords);
        btnUpdatePool = findViewById(R.id.btnUpdatePool);
        btnLogout = findViewById(R.id.btnLogout);
        NavigationHelper.bindTopBar(this);
        NavigationHelper.bindBottomBar(this);
        btnViewWords.setOnClickListener(v -> toggleWordList());
        btnUpdatePool.setOnClickListener(v -> updatePool());
        btnLogout.setOnClickListener(v -> {
            sessionManager.clear();
            openLoginAndClose();
        });

        listWords.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(loadedWords.get(position));
            return true;
        });

        setWordListVisible(false);
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
        AppExecutors.io().execute(() -> {
            List<WordEntity> words = AppContainer.from(this).wordRepository.listWords(userId);
            runOnUiThread(() -> showWords(words));
        });
    }

    private void showWords(List<WordEntity> words) {
        loadedWords.clear();
        loadedWords.addAll(words);

        wordAdapter.clear();
        wordAdapter.addAll(words);
        wordAdapter.notifyDataSetChanged();
        tvEmptyState.setText(words.isEmpty() ? "Henüz kelime eklenmedi." : "Eklenen kelimeleri butonla görüntüleyebilirsin.");
        if (words.isEmpty()) {
            setWordListVisible(false);
        }
        updatePoolButton(words.size());
    }

    private void updatePool() {
        int userId = sessionManager.getUserId();
        tvEmptyState.setText("Kelime havuzu güncelleniyor...");
        btnUpdatePool.setEnabled(false);
        btnViewWords.setEnabled(false);
        btnLogout.setEnabled(false);
        AppExecutors.io().execute(() -> {
            int importedCount = AppContainer.from(this).wordRepository.addNextSeedBatch(userId);
            int totalSeedCount = AppContainer.from(this).wordRepository.getSeedWordCount();
            List<WordEntity> words = AppContainer.from(this).wordRepository.listWords(userId);
            runOnUiThread(() -> {
                btnUpdatePool.setEnabled(true);
                btnViewWords.setEnabled(true);
                btnLogout.setEnabled(true);
                showWords(words);
                if (importedCount == 0) {
                    tvEmptyState.setText("Tüm " + totalSeedCount + " kelime zaten havuzda.");
                    return;
                }
                tvEmptyState.setText(importedCount + " yeni kelime internetten havuza eklendi.");
            });
        });
    }

    private void toggleWordList() {
        boolean shouldShow = listWords.getVisibility() != View.VISIBLE;
        setWordListVisible(shouldShow);
    }

    private void setWordListVisible(boolean visible) {
        listWords.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnViewWords.setText(visible ? "Eklenen Kelimeleri Gizle" : "Eklenen Kelimeleri Gör");
        if (loadedWords.isEmpty()) {
            tvEmptyState.setText("Henüz kelime eklenmedi.");
        }
    }

    private void updatePoolButton(int currentWordCount) {
        int totalSeedCount = AppContainer.from(this).wordRepository.getSeedWordCount();
        btnUpdatePool.setText(currentWordCount == 0 ? "Havuz Oluştur" : "Havuz Güncelle (20)");
        btnUpdatePool.setEnabled(currentWordCount < totalSeedCount);
    }

    private void showWordDetails(WordEntity word) {
        showWordDetails(word.wordId);
    }

    private void showWordDetails(int wordId) {
        int userId = sessionManager.getUserId();
        AppExecutors.io().execute(() -> {
            WordDetails details = AppContainer.from(this).wordRepository.getWordDetails(userId, wordId);
            runOnUiThread(() -> showWordDialog(details));
        });
    }

    private void showWordDialog(WordDetails details) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_word_details, null, false);
        ImageView ivWordImage = dialogView.findViewById(R.id.ivWordImage);
        TextView tvMeaning = dialogView.findViewById(R.id.tvMeaning);
        TextView tvSamples = dialogView.findViewById(R.id.tvSamples);

        tvMeaning.setText(details.getTrWord());
        tvSamples.setText(buildSamplesText(details));
        bindWordImage(ivWordImage, details.getPicturePath());

        new AlertDialog.Builder(this)
                .setTitle(details.getEngWord())
                .setView(dialogView)
                .setPositiveButton("Tamam", null)
                .show();
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

        Glide.with(this)
                .load(picturePath)
                .into(imageView);
        imageView.setVisibility(View.VISIBLE);
    }

    private void showDeleteDialog(WordEntity word) {
        new AlertDialog.Builder(this)
                .setTitle("Kelimeyi sil")
                .setMessage(word.engWord + " kelimesini silmek istiyor musun?")
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton("Sil", (dialog, which) -> deleteWord(word.wordId))
                .show();
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
