package com.samil.kelimequiz.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.WordEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordCardAdapter extends ArrayAdapter<WordEntity> {
    public interface WordActionListener {
        void onDetailRequested(WordEntity word);
    }

    private final LayoutInflater inflater;
    private final Set<Integer> revealedWordIds = new HashSet<>();
    private final WordActionListener actionListener;

    public WordCardAdapter(Context context, List<WordEntity> words, WordActionListener actionListener) {
        super(context, 0, words);
        this.inflater = LayoutInflater.from(context);
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null ? convertView : inflater.inflate(R.layout.item_word_card, parent, false);
        WordEntity word = getItem(position);
        if (word == null) {
            return view;
        }

        TextView tvEnglishWord = view.findViewById(R.id.tvEnglishWord);
        TextView tvTurkishWord = view.findViewById(R.id.tvTurkishWord);
        ImageButton btnToggleMeaning = view.findViewById(R.id.btnToggleMeaning);
        MaterialButton btnDetail = view.findViewById(R.id.btnDetail);

        boolean isRevealed = revealedWordIds.contains(word.wordId);
        tvEnglishWord.setText(word.engWord);
        tvTurkishWord.setText(word.trWord);
        tvTurkishWord.setVisibility(isRevealed ? View.VISIBLE : View.GONE);
        btnToggleMeaning.setImageResource(isRevealed ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        btnToggleMeaning.setContentDescription(isRevealed ? "Çeviriyi gizle" : "Çeviriyi göster");

        btnToggleMeaning.setOnClickListener(v -> {
            toggleMeaning(word.wordId);
            notifyDataSetChanged();
        });
        btnDetail.setOnClickListener(v -> actionListener.onDetailRequested(word));
        return view;
    }

    private void toggleMeaning(int wordId) {
        if (revealedWordIds.contains(wordId)) {
            revealedWordIds.remove(wordId);
            return;
        }
        revealedWordIds.add(wordId);
    }
}
