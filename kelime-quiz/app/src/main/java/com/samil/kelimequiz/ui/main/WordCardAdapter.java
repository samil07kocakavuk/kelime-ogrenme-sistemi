package com.samil.kelimequiz.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.WordEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordCardAdapter extends RecyclerView.Adapter<WordCardAdapter.WordViewHolder> {
    public interface WordActionListener {
        void onDetailRequested(WordEntity word);
        void onDeleteRequested(WordEntity word);
    }

    private final List<WordEntity> words = new ArrayList<>();
    private final Set<Integer> revealedWordIds = new HashSet<>();
    private final WordActionListener actionListener;

    public WordCardAdapter(WordActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setWords(List<WordEntity> newWords) {
        words.clear();
        words.addAll(newWords);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_card, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordEntity word = words.get(position);
        holder.bind(word, revealedWordIds.contains(word.wordId), actionListener);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEnglishWord;
        private final TextView tvTurkishWord;
        private final ImageButton btnToggleMeaning;
        private final MaterialButton btnDetail;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglishWord = itemView.findViewById(R.id.tvEnglishWord);
            tvTurkishWord = itemView.findViewById(R.id.tvTurkishWord);
            btnToggleMeaning = itemView.findViewById(R.id.btnToggleMeaning);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }

        public void bind(WordEntity word, boolean isRevealed, WordActionListener listener) {
            tvEnglishWord.setText(word.engWord);
            tvTurkishWord.setText(word.trWord);
            tvTurkishWord.setVisibility(isRevealed ? View.VISIBLE : View.GONE);
            
            btnToggleMeaning.setImageResource(isRevealed ? R.drawable.ic_eye : R.drawable.ic_eye_off);
            
            btnToggleMeaning.setOnClickListener(v -> {
                if (revealedWordIds.contains(word.wordId)) {
                    revealedWordIds.remove(word.wordId);
                } else {
                    revealedWordIds.add(word.wordId);
                }
                notifyItemChanged(getAdapterPosition());
            });

            btnDetail.setOnClickListener(v -> listener.onDetailRequested(word));
            
            itemView.setOnLongClickListener(v -> {
                listener.onDeleteRequested(word);
                return true;
            });
        }
    }
}
