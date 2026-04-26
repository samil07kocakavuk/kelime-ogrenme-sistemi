package com.samil.kelimequiz.ui.word;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.ImageStorage;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

public class AddWordActivity extends AppCompatActivity {
    private static final String[] CATEGORIES = {
            "Hayvanlar",
            "Meslekler",
            "Sporlar",
            "Yiyecekler",
            "Ev ve Eşyalar",
            "Okul",
            "Ulaşım",
            "Doğa",
            "Sıfatlar",
            "Fiiller",
            "Sağlık ve Vücut",
            "Günlük Yaşam"
    };

    private TextInputLayout tilEngWord, tilTrWord;
    private TextInputEditText etEngWord;
    private TextInputEditText etTrWord;
    private TextInputEditText etSamples;
    private TextView tvSelectedImage;
    private ImageView ivSelectedImage;
    private Spinner spCategory;
    private MaterialButton btnSaveWord;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::onImagePicked
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!new SessionManager(this).isLoggedIn()) {
            openLoginAndClose();
            return;
        }

        setContentView(R.layout.activity_add_word);

        tilEngWord = findViewById(R.id.tilEngWord);
        tilTrWord = findViewById(R.id.tilTrWord);
        etEngWord = findViewById(R.id.etEngWord);
        etTrWord = findViewById(R.id.etTrWord);
        etSamples = findViewById(R.id.etSamples);
        tvSelectedImage = findViewById(R.id.tvSelectedImage);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        spCategory = findViewById(R.id.spCategory);
        btnSaveWord = findViewById(R.id.btnSaveWord);
        MaterialButton btnChooseImage = findViewById(R.id.btnChooseImage);

        NavigationHelper.bindTopBar(this);
        NavigationHelper.bindBottomBar(this);
        setupCategorySpinner();
        ivSelectedImage.setVisibility(View.GONE);
        btnChooseImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSaveWord.setOnClickListener(v -> validateAndSave());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void validateAndSave() {
        String engWord = getInput(etEngWord);
        String trWord = getInput(etTrWord);
        boolean isValid = true;

        tilEngWord.setError(null);
        tilTrWord.setError(null);

        if (engWord.isEmpty()) {
            tilEngWord.setError(getString(R.string.english_required_error));
            isValid = false;
        }
        if (trWord.isEmpty()) {
            tilTrWord.setError(getString(R.string.turkish_required_error));
            isValid = false;
        }

        if (isValid) {
            saveWord(engWord, trWord);
        }
    }

    private void saveWord(String engWord, String trWord) {
        int userId = new SessionManager(this).getUserId();
        if (userId <= 0) {
            showStatus(getString(R.string.session_not_found));
            return;
        }

        String samples = getInput(etSamples);
        String category = (String) spCategory.getSelectedItem();

        showStatus(getString(R.string.saving_word));
        btnSaveWord.setEnabled(false);
        AppExecutors.io().execute(() -> {
            try {
                String picturePath = selectedImageUri == null ? null : ImageStorage.copyToAppStorage(this, selectedImageUri);
                AppContainer.from(this).wordRepository.addWord(userId, engWord, trWord, picturePath, samples, category);
                runOnUiThread(() -> {
                    showStatus(getString(R.string.word_saved));
                    finish();
                });
            } catch (RuntimeException e) {
                runOnUiThread(() -> {
                    btnSaveWord.setEnabled(true);
                    showStatus(getString(R.string.error_with_message, e.getMessage()));
                });
            }
        });
    }

    private void onImagePicked(Uri uri) {
        selectedImageUri = uri;
        if (uri == null) {
            tvSelectedImage.setText(R.string.no_image_selected);
            ivSelectedImage.setImageDrawable(null);
            ivSelectedImage.setVisibility(View.GONE);
            return;
        }
        tvSelectedImage.setText(R.string.image_selected);
        Glide.with(this)
                .load(uri)
                .thumbnail(0.25f)
                .override(480, 360)
                .centerCrop()
                .dontAnimate()
                .into(ivSelectedImage);
        ivSelectedImage.setVisibility(View.VISIBLE);
    }

    private String getInput(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private void showStatus(String message) {
        Snackbar.make(btnSaveWord, message, Snackbar.LENGTH_SHORT).show();
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
