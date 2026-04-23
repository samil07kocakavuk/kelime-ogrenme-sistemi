package com.samil.kelimequiz.util;

import android.content.Context;

import com.samil.kelimequiz.data.local.AppDatabase;
import com.samil.kelimequiz.data.repository.AuthRepository;
import com.samil.kelimequiz.util.security.PasswordHasher;

public class AppContainer {
    private static volatile AppContainer instance;

    public final AuthRepository authRepository;

    private AppContainer(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        authRepository = new AuthRepository(database.userDao(), new PasswordHasher());
    }

    public static AppContainer from(Context context) {
        if (instance == null) {
            synchronized (AppContainer.class) {
                if (instance == null) {
                    instance = new AppContainer(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
}
