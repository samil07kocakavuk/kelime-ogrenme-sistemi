package com.samil.kelimequiz.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.samil.kelimequiz.data.local.dao.UserDao;
import com.samil.kelimequiz.data.local.dao.QuizProgressDao;
import com.samil.kelimequiz.data.local.dao.WordDao;
import com.samil.kelimequiz.data.local.dao.WordSampleDao;
import com.samil.kelimequiz.data.local.entity.QuizProgressEntity;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.data.local.entity.WordSampleEntity;
import com.samil.kelimequiz.util.SessionManager;

@Database(
        entities = {UserEntity.class, WordEntity.class, WordSampleEntity.class, QuizProgressEntity.class},
        version = 8,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "kelime_quiz.db";

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();

    public abstract WordDao wordDao();

    public abstract WordSampleDao wordSampleDao();

    public abstract QuizProgressDao quizProgressDao();

    public static AppDatabase getInstance(Context context) {
        Context appContext = context.getApplicationContext();
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
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
}
