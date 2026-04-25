package com.samil.kelimequiz.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.samil.kelimequiz.data.local.entity.WordSampleEntity;
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
public final class WordSampleDao_Impl implements WordSampleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WordSampleEntity> __insertionAdapterOfWordSampleEntity;

  public WordSampleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWordSampleEntity = new EntityInsertionAdapter<WordSampleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `word_samples` (`wordSampleId`,`wordId`,`sampleText`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final WordSampleEntity entity) {
        statement.bindLong(1, entity.wordSampleId);
        statement.bindLong(2, entity.wordId);
        if (entity.sampleText == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.sampleText);
        }
      }
    };
  }

  @Override
  public void insertAll(final List<WordSampleEntity> samples) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfWordSampleEntity.insert(samples);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<WordSampleEntity> listByWordId(final int wordId) {
    final String _sql = "SELECT * FROM word_samples WHERE wordId = ? ORDER BY wordSampleId ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, wordId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfWordSampleId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordSampleId");
      final int _cursorIndexOfWordId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordId");
      final int _cursorIndexOfSampleText = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleText");
      final List<WordSampleEntity> _result = new ArrayList<WordSampleEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final WordSampleEntity _item;
        _item = new WordSampleEntity();
        _item.wordSampleId = _cursor.getInt(_cursorIndexOfWordSampleId);
        _item.wordId = _cursor.getInt(_cursorIndexOfWordId);
        if (_cursor.isNull(_cursorIndexOfSampleText)) {
          _item.sampleText = null;
        } else {
          _item.sampleText = _cursor.getString(_cursorIndexOfSampleText);
        }
        _result.add(_item);
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
