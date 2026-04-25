package com.samil.kelimequiz.ui.word;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.domain.model.WordDetails;

public class WordDetailBottomSheet extends BottomSheetDialogFragment {
    private final WordDetails details;

    public WordDetailBottomSheet(WordDetails details) {
        this.details = details;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_word_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvEngWord = view.findViewById(R.id.tvDialogTitle); // XML'e bu ID'yi ekleyeceğiz veya setTitle kullanacağız
        ImageView ivWordImage = view.findViewById(R.id.ivWordImage);
        TextView tvMeaning = view.findViewById(R.id.tvMeaning);
        TextView tvSamples = view.findViewById(R.id.tvSamples);

        tvMeaning.setText(details.getTrWord());
        tvSamples.setText(buildSamplesText(details));
        
        // Başlık için bir TextView eklememiz iyi olur
        if (tvEngWord != null) {
            tvEngWord.setText(details.getEngWord());
        }

        if (details.getPicturePath() != null && !details.getPicturePath().isEmpty()) {
            Glide.with(this).load(details.getPicturePath()).into(ivWordImage);
            ivWordImage.setVisibility(View.VISIBLE);
        } else {
            ivWordImage.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
    }

    private String buildSamplesText(WordDetails details) {
        if (details.getSampleTexts().isEmpty()) {
            return "Örnek cümle yok.";
        }

        StringBuilder builder = new StringBuilder();
        for (String sample : details.getSampleTexts()) {
            builder.append("• ").append(sample).append("\n");
        }
        return builder.toString().trim();
    }
}
