package com.samil.kelimequiz.ui.word;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.ImageStorage;
import com.samil.kelimequiz.util.SessionManager;

public class AddWordActivity extends AppCompatActivity {
    private TextInputEditText etEngWord;
    private TextInputEditText etTrWord;
    private TextInputEditText etSamples;
    private TextView tvStatusMessage;
    private TextView tvSelectedImage;
    private ImageView ivSelectedImage;
    private MaterialButton btnSaveWord;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::onImagePicked
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        etEngWord = findViewById(R.id.etEngWord);
        etTrWord = findViewById(R.id.etTrWord);
        etSamples = findViewById(R.id.etSamples);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        tvSelectedImage = findViewById(R.id.tvSelectedImage);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSaveWord = findViewById(R.id.btnSaveWord);
        MaterialButton btnChooseImage = findViewById(R.id.btnChooseImage);

        ivSelectedImage.setVisibility(View.GONE);
        btnChooseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSaveWord.setOnClickListener(v -> saveWord());
    }

    private void saveWord() {
        int userId = new SessionManager(this).getUserId();
        if (userId <= 0) {
            showStatus("Oturum bulunamadı.");
            return;
        }

        String engWord = getInput(etEngWord);
        String trWord = getInput(etTrWord);
        String samples = getInput(etSamples);

        showStatus("Kelime kaydediliyor...");
        btnSaveWord.setEnabled(false);
        AppExecutors.io().execute(() -> {
            try {
                String picturePath = selectedImageUri == null ? null : ImageStorage.copyToAppStorage(this, selectedImageUri);
                AppContainer.from(this).wordRepository.addWord(userId, engWord, trWord, picturePath, samples);
                runOnUiThread(() -> {
                    showStatus("Kelime kaydedildi.");
                    finish();
                });
            } catch (RuntimeException e) {
                runOnUiThread(() -> {
                    btnSaveWord.setEnabled(true);
                    showStatus(e.getMessage());
                });
            }
        });
    }

    private void onImagePicked(Uri uri) {
        selectedImageUri = uri;
        if (uri == null) {
            tvSelectedImage.setText("Henüz görsel seçilmedi.");
            ivSelectedImage.setImageDrawable(null);
            ivSelectedImage.setVisibility(View.GONE);
            return;
        }
        tvSelectedImage.setText("Görsel seçildi.");
        ivSelectedImage.setImageURI(uri);
        ivSelectedImage.setVisibility(View.VISIBLE);
    }

    private String getInput(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }

    private void showStatus(String message) {
        tvStatusMessage.setText(message);
    }
}
