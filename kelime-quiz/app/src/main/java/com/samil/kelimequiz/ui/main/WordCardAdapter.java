package com.samil.kelimequiz.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
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
        List<WordEntity> oldWords = new ArrayList<>(words);
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
    }

    private static class WordDiffCallback extends DiffUtil.Callback {
        private final List<WordEntity> oldWords;
        private final List<WordEntity> newWords;

        WordDiffCallback(List<WordEntity> oldWords, List<WordEntity> newWords) {
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
            return oldWords.get(oldItemPosition).wordId == newWords.get(newItemPosition).wordId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            WordEntity oldWord = oldWords.get(oldItemPosition);
            WordEntity newWord = newWords.get(newItemPosition);
            return oldWord.engWord.equals(newWord.engWord)
                    && oldWord.trWord.equals(newWord.trWord)
                    && oldWord.createdAt == newWord.createdAt;
        }
    }
}
