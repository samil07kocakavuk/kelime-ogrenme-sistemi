package com.samil.kelimequiz.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.samil.kelimequiz.data.local.entity.UserEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePassword;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `users` (`userId`,`username`,`passwordHash`,`passwordSalt`,`passwordIterations`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UserEntity entity) {
        statement.bindLong(1, entity.userId);
        if (entity.username == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.username);
        }
        if (entity.passwordHash == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.passwordHash);
        }
        if (entity.passwordSalt == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.passwordSalt);
        }
        statement.bindLong(5, entity.passwordIterations);
        statement.bindLong(6, entity.createdAt);
      }
    };
    this.__preparedStmtOfUpdatePassword = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET passwordHash = ?, passwordSalt = ?, passwordIterations = ? WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final UserEntity user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfUserEntity.insertAndReturnId(user);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updatePassword(final int userId, final String hash, final String salt,
      final int iterations) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePassword.acquire();
    int _argIndex = 1;
    if (hash == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, hash);
    }
    _argIndex = 2;
    if (salt == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, salt);
    }
    _argIndex = 3;
    _stmt.bindLong(_argIndex, iterations);
    _argIndex = 4;
    _stmt.bindLong(_argIndex, userId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdatePassword.release(_stmt);
    }
  }

  @Override
  public UserEntity findByUsername(final String username) {
    final String _sql = "SELECT * FROM users WHERE username = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
      final int _cursorIndexOfPasswordSalt = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordSalt");
      final int _cursorIndexOfPasswordIterations = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordIterations");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final UserEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new UserEntity();
        _result.userId = _cursor.getInt(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _result.username = null;
        } else {
          _result.username = _cursor.getString(_cursorIndexOfUsername);
        }
        if (_cursor.isNull(_cursorIndexOfPasswordHash)) {
          _result.passwordHash = null;
        } else {
          _result.passwordHash = _cursor.getString(_cursorIndexOfPasswordHash);
        }
        if (_cursor.isNull(_cursorIndexOfPasswordSalt)) {
          _result.passwordSalt = null;
        } else {
          _result.passwordSalt = _cursor.getString(_cursorIndexOfPasswordSalt);
        }
        _result.passwordIterations = _cursor.getInt(_cursorIndexOfPasswordIterations);
        _result.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public UserEntity findById(final int userId) {
    final String _sql = "SELECT * FROM users WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfPasswordHash = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordHash");
      final int _cursorIndexOfPasswordSalt = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordSalt");
      final int _cursorIndexOfPasswordIterations = CursorUtil.getColumnIndexOrThrow(_cursor, "passwordIterations");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final UserEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new UserEntity();
        _result.userId = _cursor.getInt(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _result.username = null;
        } else {
          _result.username = _cursor.getString(_cursorIndexOfUsername);
        }
        if (_cursor.isNull(_cursorIndexOfPasswordHash)) {
          _result.passwordHash = null;
        } else {
          _result.passwordHash = _cursor.getString(_cursorIndexOfPasswordHash);
        }
        if (_cursor.isNull(_cursorIndexOfPasswordSalt)) {
          _result.passwordSalt = null;
        } else {
          _result.passwordSalt = _cursor.getString(_cursorIndexOfPasswordSalt);
        }
        _result.passwordIterations = _cursor.getInt(_cursorIndexOfPasswordIterations);
        _result.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
