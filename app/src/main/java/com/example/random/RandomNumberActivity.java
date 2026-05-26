package com.example.random;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.random.databinding.ActivityRandomNumberBinding;
import com.example.random.db.AppDatabase;
import com.example.random.db.HistoryEntity;

import java.util.Random;

public class RandomNumberActivity extends AppCompatActivity {

    private ActivityRandomNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnGenerate.setOnClickListener(v -> generateNumber());
    }

    private void generateNumber() {
        binding.tilMin.setError(null);
        binding.tilMax.setError(null);
        
        String minStr = binding.etMin.getText().toString();
        String maxStr = binding.etMax.getText().toString();

        if (minStr.isEmpty()) {
            binding.tilMin.setError(getString(R.string.error_required));
            return;
        }
        if (maxStr.isEmpty()) {
            binding.tilMax.setError(getString(R.string.error_required));
            return;
        }

        try {
            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);

            if (min > max) {
                binding.tilMin.setError(getString(R.string.error_min_max));
                return;
            }

            int randomNum = new Random().nextInt((max - min) + 1) + min;
            binding.tvResult.setText(String.valueOf(randomNum));

            // Save to DB
            new Thread(() -> {
                HistoryEntity history = new HistoryEntity("NUMBER", String.valueOf(randomNum), System.currentTimeMillis());
                AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_invalid_num), Toast.LENGTH_SHORT).show();
        }
    }
}
