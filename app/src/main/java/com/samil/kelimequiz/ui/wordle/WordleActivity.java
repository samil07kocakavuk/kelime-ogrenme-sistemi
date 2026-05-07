package com.samil.kelimequiz.ui.wordle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.AppDatabase;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordleActivity extends AppCompatActivity {
    private static final int MAX_ATTEMPTS = 5;
    private static final int MIN_WORD_LENGTH = 4;
    private static final int MAX_WORD_LENGTH = 6;
    private static final String PREFS_NAME = "wordle_prefs";

    private LinearLayout llWordleGrid;
    private LinearLayout llKeyboard;
    private LinearLayout llCalendar;
    private TextView tvStatus;
    private TextView tvResult;

    private String targetWord;
    private int currentAttempt;
    private int currentCol;
    private boolean gameOver;
    private int userId;
    private String selectedDate;
    private final List<LinearLayout> gridRows = new ArrayList<>();
    private final Map<String, MaterialButton> keyButtons = new HashMap<>();
    private final Map<String, Integer> letterStates = new HashMap<>(); // 0=absent, 1=misplaced, 2=correct

    private static final String[] KEYBOARD_ROW_1 = {"Q","W","E","R","T","Y","U","I","O","P"};
    private static final String[] KEYBOARD_ROW_2 = {"A","S","D","F","G","H","J","K","L"};
    private static final String[] KEYBOARD_ROW_3 = {"Z","X","C","V","B","N","M"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }
        userId = sessionManager.getUserId();

        bindViews();
        NavigationHelper.bindTopBar(this);
        NavigationHelper.bindBottomBar(this);
        buildCalendar();

        selectedDate = todayString();
        loadGameForDate(selectedDate);
    }

    private void bindViews() {
        llWordleGrid = findViewById(R.id.llWordleGrid);
        llKeyboard = findViewById(R.id.llKeyboard);
        llCalendar = findViewById(R.id.llCalendar);
        tvStatus = findViewById(R.id.tvWordleStatus);
        tvResult = findViewById(R.id.tvWordleResult);
    }

    // ─── TAKVİM ───

    private void buildCalendar() {
        llCalendar.removeAllViews();
        SimpleDateFormat dayFmt = new SimpleDateFormat("EEE\ndd", new Locale("tr"));
        Calendar cal = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            Calendar day = (Calendar) cal.clone();
            day.add(Calendar.DAY_OF_YEAR, -i);
            String dateStr = toDateString(day);
            boolean completed = isDateCompleted(dateStr);

            MaterialButton btn = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(52), dpToPx(52));
            params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            btn.setLayoutParams(params);
            btn.setPadding(0, 0, 0, 0);
            btn.setInsetTop(0);
            btn.setInsetBottom(0);
            btn.setTextSize(11);
            btn.setText(dayFmt.format(day.getTime()));

            if (completed) {
                btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.success)));
                btn.setTextColor(getColor(R.color.white));
            }
            if (dateStr.equals(todayString())) {
                btn.setStrokeWidth(dpToPx(2));
                btn.setStrokeColor(ColorStateList.valueOf(getColor(R.color.primary)));
            }

            btn.setOnClickListener(v -> {
                selectedDate = dateStr;
                loadGameForDate(dateStr);
            });
            llCalendar.addView(btn);
        }
    }

    // ─── OYUN YÜKLEME ───

    private void loadGameForDate(String date) {
        AppExecutors.io().execute(() -> {
            String word = getWordForDate(date);
            runOnUiThread(() -> {
                if (word == null) {
                    showNoWordsState();
                } else {
                    restoreOrStartGame(word, date);
                }
            });
        });
    }

    private void restoreOrStartGame(String word, String date) {
        targetWord = word;
        gameOver = false;
        currentAttempt = 0;
        currentCol = 0;
        letterStates.clear();
        tvResult.setVisibility(View.GONE);

        buildEmptyGrid();
        buildKeyboard();

        List<String> savedGuesses = getSavedGuesses(date);
        for (String guess : savedGuesses) {
            replayGuess(guess);
        }

        if (isDateCompleted(date)) {
            gameOver = true;
            setKeyboardEnabled(false);
            boolean won = savedGuesses.stream().anyMatch(g -> g.equals(targetWord));
            showResult(won);
        }

        tvStatus.setText(getString(R.string.wordle_hint, targetWord.length()));
    }

    private void replayGuess(String guess) {
        int wordLen = targetWord.length();
        LinearLayout row = gridRows.get(currentAttempt);
        for (int i = 0; i < wordLen && i < guess.length(); i++) {
            TextView cell = (TextView) row.getChildAt(i);
            cell.setText(String.valueOf(guess.charAt(i)));
        }
        applyColors(currentAttempt, guess);
        updateKeyboardColors(guess);
        currentAttempt++;
        currentCol = 0;
    }

    // ─── KELİME SEÇİMİ ───

    private String getWordForDate(String date) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = "word_" + date + "_" + userId;
        String saved = prefs.getString(key, null);
        if (saved != null) return saved;

        AppDatabase db = AppDatabase.getInstance(this);
        String word = db.wordDao().getRandomWordForWordle(userId, MIN_WORD_LENGTH, MAX_WORD_LENGTH);
        if (word != null) {
            word = word.toUpperCase(Locale.ENGLISH);
            prefs.edit().putString(key, word).apply();
        }
        return word;
    }

    // ─── STATE ───

    private void saveGuess(String date, String guess) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = "guesses_" + date + "_" + userId;
        String existing = prefs.getString(key, "");
        String updated = existing.isEmpty() ? guess : existing + "|" + guess;
        prefs.edit().putString(key, updated).apply();
    }

    private List<String> getSavedGuesses(String date) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = "guesses_" + date + "_" + userId;
        String raw = prefs.getString(key, "");
        List<String> guesses = new ArrayList<>();
        if (!raw.isEmpty()) {
            for (String g : raw.split("\\|")) guesses.add(g);
        }
        return guesses;
    }

    private void markDateCompleted(String date) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("completed_" + date + "_" + userId, true).apply();
    }

    private boolean isDateCompleted(String date) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("completed_" + date + "_" + userId, false);
    }

    // ─── GRID ───

    private void buildEmptyGrid() {
        llWordleGrid.removeAllViews();
        gridRows.clear();
        int wordLen = targetWord.length();

        for (int row = 0; row < MAX_ATTEMPTS; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = dpToPx(4);
            rowLayout.setLayoutParams(rowParams);

            for (int col = 0; col < wordLen; col++) {
                rowLayout.addView(createCell());
            }
            llWordleGrid.addView(rowLayout);
            gridRows.add(rowLayout);
        }
    }

    private TextView createCell() {
        int size = dpToPx(48);
        TextView cell = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
        cell.setLayoutParams(params);
        cell.setGravity(Gravity.CENTER);
        cell.setTextSize(22);
        cell.setTypeface(null, Typeface.BOLD);
        cell.setTextColor(getColor(R.color.text_primary));
        cell.setBackgroundResource(R.drawable.bg_wordle_empty);
        return cell;
    }

    // ─── KLAVYE ───

    private void buildKeyboard() {
        llKeyboard.removeAllViews();
        keyButtons.clear();

        llKeyboard.addView(buildKeyboardRow(KEYBOARD_ROW_1));
        llKeyboard.addView(buildKeyboardRow(KEYBOARD_ROW_2));

        // 3. satır: GÖNDER + harfler + SİL
        LinearLayout row3 = new LinearLayout(this);
        row3.setGravity(Gravity.CENTER);
        row3.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // GÖNDER butonu - belirgin
        MaterialButton btnEnter = new MaterialButton(this);
        LinearLayout.LayoutParams enterParams = new LinearLayout.LayoutParams(dpToPx(64), dpToPx(48));
        enterParams.setMargins(dpToPx(2), dpToPx(3), dpToPx(2), dpToPx(3));
        btnEnter.setLayoutParams(enterParams);
        btnEnter.setPadding(0, 0, 0, 0);
        btnEnter.setInsetTop(0);
        btnEnter.setInsetBottom(0);
        btnEnter.setTextSize(11);
        btnEnter.setText("GÖN");
        btnEnter.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.success)));
        btnEnter.setTextColor(getColor(R.color.white));
        btnEnter.setOnClickListener(v -> onEnterPressed());
        row3.addView(btnEnter);

        for (String letter : KEYBOARD_ROW_3) {
            row3.addView(createLetterKey(letter));
        }

        // SİL butonu
        MaterialButton btnDel = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(dpToPx(56), dpToPx(48));
        delParams.setMargins(dpToPx(2), dpToPx(3), dpToPx(2), dpToPx(3));
        btnDel.setLayoutParams(delParams);
        btnDel.setPadding(0, 0, 0, 0);
        btnDel.setInsetTop(0);
        btnDel.setInsetBottom(0);
        btnDel.setTextSize(16);
        btnDel.setText("⌫");
        btnDel.setOnClickListener(v -> onBackspacePressed());
        row3.addView(btnDel);

        llKeyboard.addView(row3);

        // Kayıtlı renkleri uygula
        for (Map.Entry<String, Integer> entry : letterStates.entrySet()) {
            applyKeyColor(entry.getKey(), entry.getValue());
        }
    }

    private LinearLayout buildKeyboardRow(String[] letters) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        for (String letter : letters) {
            row.addView(createLetterKey(letter));
        }
        return row;
    }

    private MaterialButton createLetterKey(String letter) {
        MaterialButton btn = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(48), 1f);
        params.setMargins(dpToPx(2), dpToPx(3), dpToPx(2), dpToPx(3));
        btn.setLayoutParams(params);
        btn.setPadding(0, 0, 0, 0);
        btn.setInsetTop(0);
        btn.setInsetBottom(0);
        btn.setTextSize(16);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setText(letter);
        btn.setOnClickListener(v -> onLetterPressed(letter));
        keyButtons.put(letter, btn);
        return btn;
    }

    private void updateKeyboardColors(String guess) {
        int[] result = evaluateGuess(guess);
        for (int i = 0; i < guess.length(); i++) {
            String letter = String.valueOf(guess.charAt(i));
            int newState = result[i];
            Integer currentState = letterStates.get(letter);

            // Yeşil > Sarı > Gri (sadece yükselt, düşürme)
            if (currentState == null || newState > currentState) {
                letterStates.put(letter, newState);
                applyKeyColor(letter, newState);
            }
        }
    }

    private void applyKeyColor(String letter, int state) {
        MaterialButton btn = keyButtons.get(letter);
        if (btn == null) return;

        int bgColor;
        switch (state) {
            case 2: bgColor = 0xFF6AAA64; break; // yeşil
            case 1: bgColor = 0xFFC9B458; break; // sarı
            default: bgColor = 0xFF787C7E; break; // gri
        }
        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(getColor(R.color.white));
        btn.setStrokeWidth(0);
    }

    private void setKeyboardEnabled(boolean enabled) {
        for (int i = 0; i < llKeyboard.getChildCount(); i++) {
            View row = llKeyboard.getChildAt(i);
            if (row instanceof LinearLayout) {
                for (int j = 0; j < ((LinearLayout) row).getChildCount(); j++) {
                    ((LinearLayout) row).getChildAt(j).setEnabled(enabled);
                }
            }
        }
    }

    // ─── KLAVYE OLAYLARI ───

    private void onLetterPressed(String letter) {
        if (gameOver || currentAttempt >= MAX_ATTEMPTS) return;
        if (currentCol >= targetWord.length()) return;

        LinearLayout row = gridRows.get(currentAttempt);
        TextView cell = (TextView) row.getChildAt(currentCol);
        cell.setText(letter);
        currentCol++;
    }

    private void onBackspacePressed() {
        if (gameOver || currentCol <= 0) return;
        currentCol--;
        LinearLayout row = gridRows.get(currentAttempt);
        TextView cell = (TextView) row.getChildAt(currentCol);
        cell.setText("");
    }

    private void onEnterPressed() {
        if (gameOver) return;
        if (currentCol < targetWord.length()) return;

        String guess = buildCurrentGuess();
        applyColors(currentAttempt, guess);
        updateKeyboardColors(guess);
        saveGuess(selectedDate, guess);

        if (guess.equals(targetWord)) {
            endGame(true);
        } else if (currentAttempt + 1 >= MAX_ATTEMPTS) {
            currentAttempt++;
            endGame(false);
        } else {
            currentAttempt++;
            currentCol = 0;
        }
    }

    private String buildCurrentGuess() {
        LinearLayout row = gridRows.get(currentAttempt);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.getChildCount(); i++) {
            sb.append(((TextView) row.getChildAt(i)).getText());
        }
        return sb.toString();
    }

    // ─── RENK DEĞERLENDİRME ───

    private void applyColors(int rowIndex, String guess) {
        LinearLayout row = gridRows.get(rowIndex);
        int len = targetWord.length();
        int[] result = evaluateGuess(guess);

        for (int i = 0; i < len; i++) {
            TextView cell = (TextView) row.getChildAt(i);
            cell.setTextColor(getColor(R.color.white));
            switch (result[i]) {
                case 2: cell.setBackgroundResource(R.drawable.bg_wordle_correct); break;
                case 1: cell.setBackgroundResource(R.drawable.bg_wordle_misplaced); break;
                default: cell.setBackgroundResource(R.drawable.bg_wordle_absent); break;
            }
        }
    }

    private int[] evaluateGuess(String guess) {
        int len = targetWord.length();
        int[] result = new int[len];
        boolean[] targetUsed = new boolean[len];
        boolean[] guessUsed = new boolean[len];

        for (int i = 0; i < len; i++) {
            if (guess.charAt(i) == targetWord.charAt(i)) {
                result[i] = 2;
                targetUsed[i] = true;
                guessUsed[i] = true;
            }
        }
        for (int i = 0; i < len; i++) {
            if (guessUsed[i]) continue;
            for (int j = 0; j < len; j++) {
                if (!targetUsed[j] && guess.charAt(i) == targetWord.charAt(j)) {
                    result[i] = 1;
                    targetUsed[j] = true;
                    break;
                }
            }
        }
        return result;
    }

    // ─── OYUN SONU ───

    private void endGame(boolean won) {
        gameOver = true;
        setKeyboardEnabled(false);
        markDateCompleted(selectedDate);
        showResult(won);
        buildCalendar();
    }

    private void showResult(boolean won) {
        tvResult.setVisibility(View.VISIBLE);
        if (won) {
            tvResult.setText(getString(R.string.wordle_win_message, targetWord));
            tvResult.setTextColor(getColor(R.color.success));
        } else {
            tvResult.setText(getString(R.string.wordle_lose_message, targetWord));
            tvResult.setTextColor(getColor(R.color.error));
        }
    }

    private void showNoWordsState() {
        tvStatus.setText(R.string.wordle_no_words);
        llKeyboard.setVisibility(View.GONE);
    }

    // ─── YARDIMCI ───

    private String todayString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private String toDateString(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.getTime());
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
