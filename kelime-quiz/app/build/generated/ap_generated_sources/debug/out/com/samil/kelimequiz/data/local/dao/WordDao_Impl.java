package com.samil.kelimequiz.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.samil.kelimequiz.data.local.entity.WordEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WordDao_Impl implements WordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WordEntity> __insertionAdapterOfWordEntity;

  private final EntityDeletionOrUpdateAdapter<WordEntity> __deletionAdapterOfWordEntity;

  private final EntityDeletionOrUpdateAdapter<WordEntity> __updateAdapterOfWordEntity;

  public WordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWordEntity = new EntityInsertionAdapter<WordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `words` (`wordId`,`userId`,`engWord`,`trWord`,`picturePath`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final WordEntity entity) {
        statement.bindLong(1, entity.wordId);
        statement.bindLong(2, entity.userId);
        if (entity.engWord == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.engWord);
        }
        if (entity.trWord == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.trWord);
        }
        if (entity.picturePath == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.picturePath);
        }
        statement.bindLong(6, entity.createdAt);
      }
    };
    this.__deletionAdapterOfWordEntity = new EntityDeletionOrUpdateAdapter<WordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `words` WHERE `wordId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final WordEntity entity) {
        statement.bindLong(1, entity.wordId);
      }
    };
    this.__updateAdapterOfWordEntity = new EntityDeletionOrUpdateAdapter<WordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `words` SET `wordId` = ?,`userId` = ?,`engWord` = ?,`trWord` = ?,`picturePath` = ?,`createdAt` = ? WHERE `wordId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final WordEntity entity) {
        statement.bindLong(1, entity.wordId);
        statement.bindLong(2, entity.userId);
        if (entity.engWord == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.engWord);
        }
        if (entity.trWord == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.trWord);
        }
        if (entity.picturePath == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.picturePath);
        }
        statement.bindLong(6, entity.createdAt);
        statement.bindLong(7, entity.wordId);
      }
    };
  }

  @Override
  public long insert(final WordEntity word) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfWordEntity.insertAndReturnId(word);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final WordEntity word) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfWordEntity.handle(word);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final WordEntity word) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfWordEntity.handle(word);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<WordEntity> listByUser(final int userId) {
    final String _sql = "SELECT * FROM words WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfWordId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfEngWord = CursorUtil.getColumnIndexOrThrow(_cursor, "engWord");
      final int _cursorIndexOfTrWord = CursorUtil.getColumnIndexOrThrow(_cursor, "trWord");
      final int _cursorIndexOfPicturePath = CursorUtil.getColumnIndexOrThrow(_cursor, "picturePath");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<WordEntity> _result = new ArrayList<WordEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final WordEntity _item;
        _item = new WordEntity();
        _item.wordId = _cursor.getInt(_cursorIndexOfWordId);
        _item.userId = _cursor.getInt(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfEngWord)) {
          _item.engWord = null;
        } else {
          _item.engWord = _cursor.getString(_cursorIndexOfEngWord);
        }
        if (_cursor.isNull(_cursorIndexOfTrWord)) {
          _item.trWord = null;
        } else {
          _item.trWord = _cursor.getString(_cursorIndexOfTrWord);
        }
        if (_cursor.isNull(_cursorIndexOfPicturePath)) {
          _item.picturePath = null;
        } else {
          _item.picturePath = _cursor.getString(_cursorIndexOfPicturePath);
        }
        _item.createdAt = _cursor.getLong(_cursorIndexOfCreatedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int countByUser(final int userId) {
    final String _sql = "SELECT COUNT(*) FROM words WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public WordEntity findByUserAndId(final int userId, final int wordId) {
    final String _sql = "SELECT * FROM words WHERE userId = ? AND wordId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, wordId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfWordId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfEngWord = CursorUtil.getColumnIndexOrThrow(_cursor, "engWord");
      final int _cursorIndexOfTrWord = CursorUtil.getColumnIndexOrThrow(_cursor, "trWord");
      final int _cursorIndexOfPicturePath = CursorUtil.getColumnIndexOrThrow(_cursor, "picturePath");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final WordEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new WordEntity();
        _result.wordId = _cursor.getInt(_cursorIndexOfWordId);
        _result.userId = _cursor.getInt(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfEngWord)) {
          _result.engWord = null;
        } else {
          _result.engWord = _cursor.getString(_cursorIndexOfEngWord);
        }
        if (_cursor.isNull(_cursorIndexOfTrWord)) {
          _result.trWord = null;
        } else {
          _result.trWord = _cursor.getString(_cursorIndexOfTrWord);
        }
        if (_cursor.isNull(_cursorIndexOfPicturePath)) {
          _result.picturePath = null;
        } else {
          _result.picturePath = _cursor.getString(_cursorIndexOfPicturePath);
        }
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
  public WordEntity findByUserAndEnglishWord(final int userId, final String engWord) {
    final String _sql = "SELECT * FROM words WHERE userId = ? AND LOWER(engWord) = LOWER(?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    if (engWord == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, engWord);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfWordId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfEngWord = CursorUtil.getColumnIndexOrThrow(_cursor, "engWord");
      final int _cursorIndexOfTrWord = CursorUtil.getColumnIndexOrThrow(_cursor, "trWord");
      final int _cursorIndexOfPicturePath = CursorUtil.getColumnIndexOrThrow(_cursor, "picturePath");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final WordEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new WordEntity();
        _result.wordId = _cursor.getInt(_cursorIndexOfWordId);
        _result.userId = _cursor.getInt(_cursorIndexOfUserId);
        if (_cursor.isNull(_cursorIndexOfEngWord)) {
          _result.engWord = null;
        } else {
          _result.engWord = _cursor.getString(_cursorIndexOfEngWord);
        }
        if (_cursor.isNull(_cursorIndexOfTrWord)) {
          _result.trWord = null;
        } else {
          _result.trWord = _cursor.getString(_cursorIndexOfTrWord);
        }
        if (_cursor.isNull(_cursorIndexOfPicturePath)) {
          _result.picturePath = null;
        } else {
          _result.picturePath = _cursor.getString(_cursorIndexOfPicturePath);
        }
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
