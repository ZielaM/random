package com.example.random;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.random.databinding.ActivityDiceRollBinding;
import com.example.random.db.AppDatabase;
import com.example.random.db.HistoryEntity;

import java.util.Random;

public class DiceRollActivity extends AppCompatActivity {

    private ActivityDiceRollBinding binding;
    private int[] diceDrawables = {
            R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
            R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiceRollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnRoll.setOnClickListener(v -> rollDice());
    }

    private void rollDice() {
        binding.tilCount.setError(null);
        String countStr = binding.etCount.getText().toString();
        if (countStr.isEmpty()) {
            binding.tilCount.setError(getString(R.string.error_enter_dice));
            return;
        }

        int count = Integer.parseInt(countStr);
        if (count < 1 || count > 12) {
            binding.tilCount.setError(getString(R.string.error_dice_range));
            return;
        }

        binding.diceGrid.removeAllViews();
        Random random = new Random();
        StringBuilder resultBuilder = new StringBuilder();

        boolean animEnabled = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("animations", true);

        for (int i = 0; i < count; i++) {
            int val = random.nextInt(6);
            resultBuilder.append(val + 1).append(" ");
            
            ImageView diceImage = new ImageView(this);
            diceImage.setImageResource(diceDrawables[val]);
            
            float scale = getResources().getDisplayMetrics().density;
            int sizeInPx = (int) (100 * scale + 0.5f);
            int marginInPx = (int) (8 * scale + 0.5f);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = sizeInPx;
            params.height = sizeInPx;
            params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
            diceImage.setLayoutParams(params);
            
            if (animEnabled) {
                diceImage.setScaleX(0f);
                diceImage.setScaleY(0f);
                diceImage.setRotation((random.nextFloat() - 0.5f) * 180f);
            }
            
            binding.diceGrid.addView(diceImage);
            
            if (animEnabled) {
                diceImage.animate()
                    .scaleX(1f).scaleY(1f)
                    .rotation(0f)
                    .setDuration(400)
                    .setStartDelay(i * 50L) // Staggered animation
                    .setInterpolator(new android.view.animation.OvershootInterpolator())
                    .start();
            }
        }
        
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);
        if (v != null) {
            // Vibrate a pattern: wait 0, vibrate 30, wait 50, vibrate 30... for a cool effect
            long[] pattern = new long[count * 2];
            for (int i=0; i<count; i++) {
                pattern[i*2] = 50; // delay before
                pattern[i*2+1] = 20; // vibration length
            }
            if (pattern.length > 0) pattern[0] = 0; // vibrate immediately on first
            v.vibrate(pattern, -1);
        }

        new Thread(() -> {
            String dbResult = count + ";" + resultBuilder.toString().trim();
            HistoryEntity history = new HistoryEntity("DICE", dbResult, System.currentTimeMillis());
            AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
        }).start();
    }
}
