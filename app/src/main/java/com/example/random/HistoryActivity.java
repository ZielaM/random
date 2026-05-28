package com.example.random;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.random.databinding.ActivityHistoryBinding;
import com.example.random.db.AppDatabase;
import com.example.random.db.HistoryEntity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnClear.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase.getDatabase(getApplicationContext()).historyDao().deleteAll();
                runOnUiThread(() -> loadHistory());
            }).start();
        });

        loadHistory();
    }

    private void loadHistory() {
        new Thread(() -> {
            List<HistoryEntity> historyList = AppDatabase.getDatabase(getApplicationContext()).historyDao().getAllHistory();
            runOnUiThread(() -> {
                HistoryAdapter adapter = new HistoryAdapter(historyList);
                binding.recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}
