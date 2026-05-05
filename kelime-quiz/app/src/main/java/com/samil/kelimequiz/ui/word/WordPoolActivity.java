package com.samil.kelimequiz.ui.word;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.data.local.entity.WordWithLevel;
import com.samil.kelimequiz.domain.model.WordDetails;
import com.samil.kelimequiz.domain.model.WordCategories;
import com.samil.kelimequiz.domain.model.WordLevel;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.main.WordCardAdapter;
import com.samil.kelimequiz.ui.profile.ProfileActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordPoolActivity extends AppCompatActivity implements WordCardAdapter.WordActionListener {
    private static final String[] SORT_OPTIONS = {
            "En Yeni",
            "Seviye (Yüksekten Alçağa)",
            "Seviye (Alçaktan Yükseğe)"
    };

    private WordCardAdapter wordAdapter;
    private TextView tvEmptyState;
    private Spinner spCategoryFilter;
    private Spinner spSortOrder;
    private SessionManager sessionManager;
    private List<WordWithLevel> allWords = new ArrayList<>();

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
        spCategoryFilter = findViewById(R.id.spCategoryFilter);
        spSortOrder = findViewById(R.id.spSortOrder);
        RecyclerView rvWords = findViewById(R.id.rvWords);
        wordAdapter = new WordCardAdapter(this);
        rvWords.setAdapter(wordAdapter);

        NavigationHelper.bindTopBar(this, true, ProfileActivity.class);
        NavigationHelper.bindBottomBar(this);
        setupCategorySpinner();
        setupSortSpinner();

        findViewById(R.id.btnPrintReport).setOnClickListener(v -> printReport());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, WordCategories.FILTERS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoryFilter.setAdapter(adapter);

        spCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSortSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, SORT_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortOrder.setAdapter(adapter);

        spSortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters() {
        if (allWords == null || allWords.isEmpty()) return;

        String category = WordCategories.FILTERS[spCategoryFilter.getSelectedItemPosition()];
        int sortPosition = spSortOrder.getSelectedItemPosition();

        List<WordWithLevel> filtered;
        if (category.equals("Tümü")) {
            filtered = new ArrayList<>(allWords);
        } else {
            filtered = allWords.stream()
                    .filter(w -> category.equals(w.word.category))
                    .collect(Collectors.toList());
        }

        // Sort the filtered list
        if (sortPosition == 1) { // High to Low
            filtered.sort((w1, w2) -> Integer.compare(w2.level, w1.level));
        } else if (sortPosition == 2) { // Low to High
            filtered.sort((w1, w2) -> Integer.compare(w1.level, w2.level));
        } else { // Newest First
            filtered.sort((w1, w2) -> Long.compare(w2.word.createdAt, w1.word.createdAt));
        }

        wordAdapter.setWords(filtered);
        tvEmptyState.setText(filtered.isEmpty() ? R.string.word_pool_empty : R.string.word_pool_help);
        
        // Sıralama veya filtre değişince listenin en başına kaydır
        RecyclerView rvWords = findViewById(R.id.rvWords);
        if (rvWords != null && !filtered.isEmpty()) {
            rvWords.scrollToPosition(0);
        }
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
        tvEmptyState.setText(R.string.word_pool_loading);
        AppExecutors.io().execute(() -> {
            AppContainer.from(this).wordRepository.addInitialSeedWords(userId);
            List<WordWithLevel> words = AppContainer.from(this).wordRepository.listWords(userId);
            runOnUiThread(() -> {
                allWords = words;
                applyFilters();
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
                .setTitle(R.string.delete_word_title)
                .setMessage(getString(R.string.delete_word_message, word.engWord))
                .setNegativeButton(R.string.cancel_action, null)
                .setPositiveButton(R.string.delete_action, (dialog, which) -> deleteWord(word.wordId))
                .show();
    }

    private void showWordDialog(WordDetails details) {
        WordDetailBottomSheet bottomSheet = new WordDetailBottomSheet(details);
        bottomSheet.show(getSupportFragmentManager(), "WordDetail");
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

    private void printReport() {
        if (allWords == null || allWords.isEmpty()) return;

        List<WordWithLevel> learnedList = new ArrayList<>();
        List<WordWithLevel> inProgressList = new ArrayList<>();
        List<WordWithLevel> notStartedList = new ArrayList<>();

        for (WordWithLevel w : allWords) {
            if (w.level >= 6) learnedList.add(w);
            else if (w.level == 0) notStartedList.add(w);
            else inProgressList.add(w);
        }
        inProgressList.sort((a, b) -> Integer.compare(b.level, a.level));

        int a1Count = countByCefr(WordLevel.A1.name());
        int a2Count = countByCefr(WordLevel.A2.name());
        int b1Count = countByCefr(WordLevel.B1.name());
        int b2Count = countByCefr(WordLevel.B2.name());
        int c1Count = countByCefr(WordLevel.C1.name());
        int c2Count = countByCefr(WordLevel.C2.name());

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'><style>")
                .append("body{font-family:sans-serif;padding:24px}")
                .append("h1{color:#333;font-size:20px}")
                .append("h2{color:#555;font-size:16px;margin-top:20px;border-bottom:1px solid #ddd;padding-bottom:4px}")
                .append(".badge{display:inline-block;padding:4px 10px;border-radius:999px;margin-right:6px;margin-bottom:6px;background:#f1f5f9;color:#0f172a;font-size:12px;font-weight:bold}")
                .append("table{width:100%;border-collapse:collapse;margin-top:8px}")
                .append("th,td{border:1px solid #ddd;padding:6px 10px;text-align:left;font-size:13px}")
                .append("th{background:#f5f5f5}")
                .append("</style></head><body>")
                .append("<h1>Kelime Quiz - Kişisel Analiz Raporu</h1>")
                .append("<p>Toplam: ").append(allWords.size())
                .append(" | Öğrenilmiş: ").append(learnedList.size())
                .append(" | Devam Eden: ").append(inProgressList.size())
                .append(" | Başlanmamış: ").append(notStartedList.size()).append("</p>")
                .append("<p>")
                .append("<span class='badge'>A1: ").append(a1Count).append("</span>")
                .append("<span class='badge'>A2: ").append(a2Count).append("</span>")
                .append("<span class='badge'>B1: ").append(b1Count).append("</span>")
                .append("<span class='badge'>B2: ").append(b2Count).append("</span>")
                .append("<span class='badge'>C1: ").append(c1Count).append("</span>")
                .append("<span class='badge'>C2: ").append(c2Count).append("</span>")
                .append("</p>");

        if (!learnedList.isEmpty()) {
            sb.append("<h2>✓ Öğrenilmiş Kelimeler (").append(learnedList.size()).append(")</h2>");
            appendTable(sb, learnedList);
        }
        if (!inProgressList.isEmpty()) {
            sb.append("<h2>↻ Öğrenilmekte Olan (").append(inProgressList.size()).append(")</h2>");
            appendTable(sb, inProgressList);
        }
        if (!notStartedList.isEmpty()) {
            sb.append("<h2>○ Başlanmamış (").append(notStartedList.size()).append(")</h2>");
            appendTable(sb, notStartedList);
        }
        sb.append("</body></html>");

        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintManager pm = (PrintManager) getSystemService(PRINT_SERVICE);
                String jobName = getString(R.string.print_report_job_name);
                pm.print(jobName, view.createPrintDocumentAdapter(jobName),
                        new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build());
            }
        });
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "UTF-8", null);
    }

    private void appendTable(StringBuilder sb, List<WordWithLevel> words) {
        sb.append("<table><tr><th>İngilizce</th><th>Türkçe</th><th>Kategori</th><th>CEFR</th><th>Quiz Seviyesi</th></tr>");
        for (WordWithLevel w : words) {
            sb.append("<tr><td>").append(w.word.engWord).append("</td>")
                    .append("<td>").append(w.word.trWord).append("</td>")
                    .append("<td>").append(w.word.category != null ? w.word.category : "-").append("</td>")
                    .append("<td>").append(w.word.cefrLevel != null ? w.word.cefrLevel : "-").append("</td>")
                    .append("<td>").append(w.level).append("/6</td></tr>");
        }
        sb.append("</table>");
    }

    private int countByCefr(String cefrLevel) {
        int count = 0;
        for (WordWithLevel word : allWords) {
            if (cefrLevel.equalsIgnoreCase(word.word.cefrLevel == null ? "A1" : word.word.cefrLevel)) {
                count++;
            }
        }
        return count;
    }
}
