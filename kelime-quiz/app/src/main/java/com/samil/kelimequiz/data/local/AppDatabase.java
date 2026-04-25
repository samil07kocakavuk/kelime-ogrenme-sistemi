package com.samil.kelimequiz.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.samil.kelimequiz.data.local.dao.UserDao;
import com.samil.kelimequiz.data.local.dao.WordDao;
import com.samil.kelimequiz.data.local.dao.WordSampleDao;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.data.local.entity.WordSampleEntity;
import com.samil.kelimequiz.util.SessionManager;

@Database(entities = {UserEntity.class, WordEntity.class, WordSampleEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "kelime_quiz.db";
    private static final String RESET_PREF_NAME = "kelime_quiz_database_reset";
    private static final String KEY_RESET_V3_DONE = "reset_v3_done";

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();

    public abstract WordDao wordDao();

    public abstract WordSampleDao wordSampleDao();

    public static AppDatabase getInstance(Context context) {
        Context appContext = context.getApplicationContext();
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    resetDatabaseOnce(appContext);
                    instance = Room.databaseBuilder(
                                    appContext,
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    private static void resetDatabaseOnce(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(RESET_PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_RESET_V3_DONE, false)) {
            return;
        }

        context.deleteDatabase(DATABASE_NAME);
        SessionManager.clearSavedSession(context);
        prefs.edit().putBoolean(KEY_RESET_V3_DONE, true).apply();
    }
}
