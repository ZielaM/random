package com.example.random;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.random.databinding.ActivityRandomStringBinding;
import com.example.random.db.AppDatabase;
import com.example.random.db.HistoryEntity;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomStringActivity extends AppCompatActivity {

    private ActivityRandomStringBinding binding;
    private List<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomStringBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v -> {
            binding.tilInput.setError(null);
            String text = binding.etInput.getText().toString().trim();
            if (!text.isEmpty()) {
                items.add(text);
                addChip(text);
                binding.etInput.setText("");
            } else {
                binding.tilInput.setError(getString(R.string.error_enter_string));
            }
        });

        binding.btnDraw.setOnClickListener(v -> drawString());
    }

    private void addChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            binding.chipGroup.removeView(chip);
            items.remove(text);
        });
        binding.chipGroup.addView(chip);
    }

    private void drawString() {
        if (items.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_list), Toast.LENGTH_SHORT).show();
            return;
        }

        int index = new Random().nextInt(items.size());
        String result = items.get(index);
        binding.tvResult.setText(result);

        new Thread(() -> {
            HistoryEntity history = new HistoryEntity("STRING", result, System.currentTimeMillis());
            AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
        }).start();
    }
}
