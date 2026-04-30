package com.samil.kelimequiz.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.WordEntity;

import com.samil.kelimequiz.data.local.entity.WordWithLevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordCardAdapter extends RecyclerView.Adapter<WordCardAdapter.WordViewHolder> {
    public interface WordActionListener {
        void onDetailRequested(WordEntity word);
        void onDeleteRequested(WordEntity word);
    }

    private final List<WordWithLevel> words = new ArrayList<>();
    private final Set<Integer> revealedWordIds = new HashSet<>();
    private final WordActionListener actionListener;

    public WordCardAdapter(WordActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setWords(List<WordWithLevel> newWords) {
        List<WordWithLevel> oldWords = new ArrayList<>(words);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new WordDiffCallback(oldWords, newWords));
        words.clear();
        words.addAll(newWords);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_card, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordWithLevel wordWithLevel = words.get(position);
        holder.bind(wordWithLevel, revealedWordIds.contains(wordWithLevel.word.wordId), actionListener);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEnglishWord;
        private final TextView tvTurkishWord;
        private final ImageView ivLevelStatus;
        private final ImageButton btnToggleMeaning;
        private final MaterialButton btnDetail;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglishWord = itemView.findViewById(R.id.tvEnglishWord);
            tvTurkishWord = itemView.findViewById(R.id.tvTurkishWord);
            ivLevelStatus = itemView.findViewById(R.id.ivLevelStatus);
            btnToggleMeaning = itemView.findViewById(R.id.btnToggleMeaning);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }

        public void bind(WordWithLevel wordWithLevel, boolean isRevealed, WordActionListener listener) {
            WordEntity word = wordWithLevel.word;
            tvEnglishWord.setText(word.engWord);
            tvTurkishWord.setText(word.trWord);
            tvTurkishWord.setVisibility(isRevealed ? View.VISIBLE : View.GONE);
            
            btnToggleMeaning.setImageResource(isRevealed ? R.drawable.ic_eye : R.drawable.ic_eye_off);
            
            updateLevelStatus(wordWithLevel.level);

            btnToggleMeaning.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                if (revealedWordIds.contains(word.wordId)) {
                    revealedWordIds.remove(word.wordId);
                } else {
                    revealedWordIds.add(word.wordId);
                }
                notifyItemChanged(position);
            });

            btnDetail.setOnClickListener(v -> listener.onDetailRequested(word));
            
            itemView.setOnLongClickListener(v -> {
                listener.onDeleteRequested(word);
                return true;
            });
        }

        private void updateLevelStatus(int level) {
            int iconRes;
            String explanation;

            if (level == 0) {
                iconRes = R.drawable.ic_close_red;
                explanation = "Kelime öğrenilmedi (Seviye 0)";
            } else if (level >= 6) {
                iconRes = R.drawable.ic_check_green;
                explanation = "Kelime öğrenildi (Seviye " + level + ")";
            } else {
                iconRes = R.drawable.ic_hourglass_yellow;
                explanation = "Kelime öğreniliyor (Seviye " + level + ")";
            }

            ivLevelStatus.setImageResource(iconRes);
            ivLevelStatus.setOnClickListener(v -> 
                Snackbar.make(v, explanation, Snackbar.LENGTH_SHORT).show()
            );
        }
    }

    private static class WordDiffCallback extends DiffUtil.Callback {
        private final List<WordWithLevel> oldWords;
        private final List<WordWithLevel> newWords;

        WordDiffCallback(List<WordWithLevel> oldWords, List<WordWithLevel> newWords) {
            this.oldWords = oldWords;
            this.newWords = newWords;
        }

        @Override
        public int getOldListSize() {
            return oldWords.size();
        }

        @Override
        public int getNewListSize() {
            return newWords.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldWords.get(oldItemPosition).word.wordId == newWords.get(newItemPosition).word.wordId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            WordWithLevel oldItem = oldWords.get(oldItemPosition);
            WordWithLevel newItem = newWords.get(newItemPosition);
            return oldItem.word.engWord.equals(newItem.word.engWord)
                    && oldItem.word.trWord.equals(newItem.word.trWord)
                    && oldItem.level == newItem.level;
        }
    }
}
