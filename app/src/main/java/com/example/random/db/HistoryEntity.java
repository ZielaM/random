package com.example.random.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_table")
public class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String type; // e.g., "NUMBER", "STRING", "DICE", "COIN", "MEME"
    private String result;
    private long timestamp;

    public HistoryEntity(String type, String result, long timestamp) {
        this.type = type;
        this.result = result;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public String getResult() { return result; }
    public long getTimestamp() { return timestamp; }
}
