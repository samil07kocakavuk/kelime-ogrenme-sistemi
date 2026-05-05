package com.samil.kelimequiz.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.samil.kelimequiz.data.local.dao.QuizProgressDao;
import com.samil.kelimequiz.data.local.dao.UserDao;
import com.samil.kelimequiz.data.local.dao.WordDao;
import com.samil.kelimequiz.data.local.dao.WordSampleDao;
import com.samil.kelimequiz.data.local.entity.QuizProgressEntity;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import com.samil.kelimequiz.data.local.entity.WordSampleEntity;

@Database(
        entities = {UserEntity.class, WordEntity.class, WordSampleEntity.class, QuizProgressEntity.class},
        version = 8,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "kelime_quiz.db";

    private static volatile AppDatabase instance;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `words` (" +
                            "`wordId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`engWord` TEXT, " +
                            "`trWord` TEXT, " +
                            "`picturePath` TEXT, " +
                            "`createdAt` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_words_userId` ON `words` (`userId`)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_words_userId_engWord` ON `words` (`userId`, `engWord`)");
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `word_samples` (" +
                            "`wordSampleId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`wordId` INTEGER NOT NULL, " +
                            "`sampleText` TEXT, " +
                            "FOREIGN KEY(`wordId`) REFERENCES `words`(`wordId`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_word_samples_wordId` ON `word_samples` (`wordId`)");
        }
    };

    private static final Migration MIGRATION_2_4 = new Migration(2, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `words` ADD COLUMN `category` TEXT");
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `quiz_progress` (" +
                            "`progressId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`wordId` INTEGER NOT NULL, " +
                            "`level` INTEGER NOT NULL, " +
                            "`nextReviewAt` INTEGER NOT NULL, " +
                            "`learned` INTEGER NOT NULL, " +
                            "`updatedAt` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`wordId`) REFERENCES `words`(`wordId`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_quiz_progress_userId` ON `quiz_progress` (`userId`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_quiz_progress_wordId` ON `quiz_progress` (`wordId`)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_quiz_progress_userId_wordId` ON `quiz_progress` (`userId`, `wordId`)");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            MIGRATION_2_4.migrate(database);
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // No schema change in this version bump.
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // No schema change in this version bump.
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // No schema change in this version bump.
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `words` ADD COLUMN `cefrLevel` TEXT");
        }
    };

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
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_4,
                                    MIGRATION_3_4,
                                    MIGRATION_4_5,
                                    MIGRATION_5_6,
                                    MIGRATION_6_7,
                                    MIGRATION_7_8
                            )
                            .build();
                }
            }
        }
        return instance;
    }
}
