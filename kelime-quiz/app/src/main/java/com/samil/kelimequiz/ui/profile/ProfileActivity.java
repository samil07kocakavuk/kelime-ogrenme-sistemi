package com.samil.kelimequiz.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.samil.kelimequiz.R;
import com.samil.kelimequiz.data.local.AppDatabase;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import com.samil.kelimequiz.domain.model.CategoryReport;
import com.samil.kelimequiz.ui.auth.LoginActivity;
import com.samil.kelimequiz.ui.word.WordPoolActivity;
import com.samil.kelimequiz.util.AppContainer;
import com.samil.kelimequiz.util.AppExecutors;
import com.samil.kelimequiz.util.NavigationHelper;
import com.samil.kelimequiz.util.QuizSettingsManager;
import com.samil.kelimequiz.util.SessionManager;
import com.samil.kelimequiz.util.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileInfo;
    private TextView tvThemeMode;
    private TextView tvQuizQuestionLimit;
    private LinearLayout llCategoryReports;
    private MaterialButton btnPrintReport;
    private List<CategoryReport> currentReports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            openLoginAndClose();
            return;
        }

        tvProfileInfo = findViewById(R.id.tvProfileInfo);
        MaterialButton btnWordPool = findViewById(R.id.btnWordPool);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnThemeLight = findViewById(R.id.btnThemeLight);
        MaterialButton btnThemeDark = findViewById(R.id.btnThemeDark);
        MaterialButton btnDecreaseQuizLimit = findViewById(R.id.btnDecreaseQuizLimit);
        MaterialButton btnIncreaseQuizLimit = findViewById(R.id.btnIncreaseQuizLimit);
        tvThemeMode = findViewById(R.id.tvThemeMode);
        tvQuizQuestionLimit = findViewById(R.id.tvQuizQuestionLimit);
        llCategoryReports = findViewById(R.id.llCategoryReports);
        btnPrintReport = findViewById(R.id.btnPrintReport);

        NavigationHelper.bindTopBar(this, false);
        NavigationHelper.bindBottomBar(this);
        bindThemeSettings(btnThemeLight, btnThemeDark);
        bindQuizLimitSettings(btnDecreaseQuizLimit, btnIncreaseQuizLimit);
        btnWordPool.setOnClickListener(v -> startActivity(new Intent(this, WordPoolActivity.class)));
        btnPrintReport.setOnClickListener(v -> printReport());
        btnLogout.setOnClickListener(v -> {
            sessionManager.clear();
            openLoginAndClose();
        });

        int userId = sessionManager.getUserId();
        loadUserInfo(userId);
        loadCategoryReports(userId);
    }

    private void loadUserInfo(int userId) {
        AppExecutors.io().execute(() -> {
            UserEntity user = AppContainer.from(this).authRepository.findUserById(userId);
            runOnUiThread(() -> showUser(user));
        });
    }

    private void loadCategoryReports(int userId) {
        showReportLoading();
        AppExecutors.io().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<String> categories = db.wordDao().listCategories(userId);
            List<CategoryReport> reports = new ArrayList<>();
            for (String category : categories) {
                int total = db.wordDao().countByCategory(userId, category);
                int correct = db.quizProgressDao().countCorrectByCategory(userId, category);
                reports.add(new CategoryReport(category, total, correct));
            }
            runOnUiThread(() -> showCategoryReports(reports));
        });
    }

    private void showReportLoading() {
        llCategoryReports.removeAllViews();
        TextView loading = new TextView(this);
        loading.setText(R.string.report_loading);
        loading.setTextColor(getColor(R.color.text_secondary));
        llCategoryReports.addView(loading);
    }

    private void showCategoryReports(List<CategoryReport> reports) {
        llCategoryReports.removeAllViews();
        currentReports = reports;

        if (reports.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(R.string.report_no_data);
            empty.setTextColor(getColor(R.color.text_secondary));
            llCategoryReports.addView(empty);
            btnPrintReport.setEnabled(false);
            return;
        }

        btnPrintReport.setEnabled(true);
        for (CategoryReport report : reports) {
            addCategoryRow(report);
        }
    }

    private void addCategoryRow(CategoryReport report) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        int paddingPx = dpToPx(8);
        row.setPadding(0, paddingPx, 0, paddingPx);

        TextView label = new TextView(this);
        label.setText(getString(R.string.report_category_format,
                report.getCategory(),
                report.getCorrectWords(),
                report.getTotalWords(),
                report.getSuccessPercent()));
        label.setTextColor(getColor(R.color.text_primary));
        label.setTextSize(15);
        row.addView(label);

        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(10));
        barParams.topMargin = dpToPx(6);
        progressBar.setLayoutParams(barParams);
        progressBar.setMax(100);
        progressBar.setProgress(report.getSuccessPercent());
        row.addView(progressBar);

        llCategoryReports.addView(row);
    }

    private void printReport() {
        if (currentReports.isEmpty()) {
            return;
        }

        String html = buildReportHtml();
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
                String jobName = getString(R.string.print_report_job_name);
                printManager.print(jobName, view.createPrintDocumentAdapter(jobName),
                        new PrintAttributes.Builder()
                                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                                .build());
            }
        });
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private String buildReportHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'><style>")
                .append("body{font-family:sans-serif;padding:24px}")
                .append("h1{color:#333;font-size:22px}")
                .append("table{width:100%;border-collapse:collapse;margin-top:16px}")
                .append("th,td{border:1px solid #ccc;padding:10px;text-align:left}")
                .append("th{background:#f5f5f5}")
                .append(".bar{height:14px;background:#4CAF50;border-radius:4px}")
                .append(".bar-bg{height:14px;background:#eee;border-radius:4px;width:100%}")
                .append("</style></head><body>")
                .append("<h1>Kelime Quiz - Kategori Raporu</h1>")
                .append("<table><tr><th>Kategori</th><th>Doğru</th><th>Toplam</th><th>Başarı</th><th>Grafik</th></tr>");

        for (CategoryReport r : currentReports) {
            sb.append("<tr><td>").append(r.getCategory()).append("</td>")
                    .append("<td>").append(r.getCorrectWords()).append("</td>")
                    .append("<td>").append(r.getTotalWords()).append("</td>")
                    .append("<td>%").append(r.getSuccessPercent()).append("</td>")
                    .append("<td><div class='bar-bg'><div class='bar' style='width:")
                    .append(r.getSuccessPercent()).append("%'></div></div></td></tr>");
        }

        sb.append("</table></body></html>");
        return sb.toString();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void bindThemeSettings(MaterialButton btnThemeLight, MaterialButton btnThemeDark) {
        showThemeMode();
        btnThemeLight.setOnClickListener(v -> changeTheme(ThemeManager.THEME_LIGHT));
        btnThemeDark.setOnClickListener(v -> changeTheme(ThemeManager.THEME_DARK));
    }

    private void changeTheme(int themeMode) {
        if (ThemeManager.getSavedTheme(this) == themeMode) {
            showThemeMode();
            return;
        }
        ThemeManager.saveAndApplyTheme(this, themeMode);
    }

    private void showThemeMode() {
        boolean darkTheme = ThemeManager.getSavedTheme(this) == ThemeManager.THEME_DARK;
        tvThemeMode.setText(darkTheme ? R.string.theme_mode_dark : R.string.theme_mode_light);
    }

    private void bindQuizLimitSettings(MaterialButton btnDecreaseQuizLimit, MaterialButton btnIncreaseQuizLimit) {
        QuizSettingsManager settingsManager = new QuizSettingsManager(this);
        showQuizQuestionLimit(settingsManager.getQuestionLimit());
        btnDecreaseQuizLimit.setOnClickListener(v -> showQuizQuestionLimit(settingsManager.decreaseQuestionLimit()));
        btnIncreaseQuizLimit.setOnClickListener(v -> showQuizQuestionLimit(settingsManager.increaseQuestionLimit()));
    }

    private void showQuizQuestionLimit(int questionLimit) {
        tvQuizQuestionLimit.setText(String.valueOf(questionLimit));
    }

    private void showUser(UserEntity user) {
        if (user == null) {
            tvProfileInfo.setText(R.string.profile_not_found);
            return;
        }
        tvProfileInfo.setText(getString(R.string.profile_username, user.username));
    }

    private void openLoginAndClose() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
