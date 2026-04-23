package com.samil.kelimequiz.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.samil.kelimequiz.data.local.entity.WordEntity;

import java.util.List;

@Dao
public interface WordDao {
    @Insert
    long insert(WordEntity word);

    @Update
    void update(WordEntity word);

    @Delete
    void delete(WordEntity word);

    @Query("SELECT * FROM words WHERE userId = :userId ORDER BY createdAt DESC")
    List<WordEntity> listByUser(int userId);

    @Query("SELECT COUNT(*) FROM words WHERE userId = :userId")
    int countByUser(int userId);

    @Query("SELECT * FROM words WHERE userId = :userId AND wordId = :wordId LIMIT 1")
    WordEntity findByUserAndId(int userId, int wordId);

    @Query("SELECT * FROM words WHERE userId = :userId AND LOWER(engWord) = LOWER(:engWord) LIMIT 1")
    WordEntity findByUserAndEnglishWord(int userId, String engWord);
}
