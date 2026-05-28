package com.example.random.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    void insert(HistoryEntity history);

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    List<HistoryEntity> getAllHistory();

    @Query("DELETE FROM history_table")
    void deleteAll();
}
