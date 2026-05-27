package com.example.random;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.random.databinding.ActivityCoinFlipBinding;
import com.example.random.db.AppDatabase;
import com.example.random.db.HistoryEntity;

import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity {

    private ActivityCoinFlipBinding binding;
    private boolean isHeads = true;
    private boolean isFlipping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoinFlipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnFlip.setOnClickListener(v -> flipCoin());
    }

    private void flipCoin() {
        if (isFlipping) return;
        isFlipping = true;

        binding.tvResult.setText("");
        boolean landOnHeads = new Random().nextBoolean();

        boolean animEnabled = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("animations", true);
        if (!animEnabled) {
            isHeads = landOnHeads;
            binding.ivCoin.setImageResource(landOnHeads ? R.drawable.coin_heads : R.drawable.coin_tails);
            binding.ivCoin.setScaleY(1f);
            
            android.os.Vibrator v = (android.os.Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (v != null) v.vibrate(50);
            
            String resultStr = landOnHeads ? getString(R.string.coin_heads) : getString(R.string.coin_tails);
            binding.tvResult.setText(resultStr);
            
            String dbResult = landOnHeads ? "HEADS" : "TAILS";
            new Thread(() -> {
                HistoryEntity history = new HistoryEntity("COIN", dbResult, System.currentTimeMillis());
                AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
            }).start();
            
            isFlipping = false;
            return;
        }

        // Flips on X axis, slows down over time
        ObjectAnimator flipAnim = ObjectAnimator.ofFloat(binding.ivCoin, "rotationX", 0f, 1800f + (landOnHeads ? 0 : 180));
        flipAnim.setDuration(2500);
        flipAnim.setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f));

        // Jump up
        ObjectAnimator upAnim = ObjectAnimator.ofFloat(binding.ivCoin, "translationY", 0f, -600f);
        upAnim.setDuration(1000);
        upAnim.setInterpolator(new android.view.animation.DecelerateInterpolator());

        // Fall down and bounce
        ObjectAnimator downAnim = ObjectAnimator.ofFloat(binding.ivCoin, "translationY", -600f, 0f);
        downAnim.setDuration(1500);
        downAnim.setInterpolator(new android.view.animation.BounceInterpolator());

        flipAnim.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            if (value % 360 > 90 && value % 360 < 270) {
                if (isHeads) {
                    binding.ivCoin.setImageResource(R.drawable.coin_tails);
                    binding.ivCoin.setScaleY(-1f);
                    isHeads = false;
                }
            } else {
                if (!isHeads) {
                    binding.ivCoin.setImageResource(R.drawable.coin_heads);
                    binding.ivCoin.setScaleY(1f);
                    isHeads = true;
                }
            }
        });

        android.animation.AnimatorSet transSet = new android.animation.AnimatorSet();
        transSet.playSequentially(upAnim, downAnim);

        android.animation.AnimatorSet animSet = new android.animation.AnimatorSet();
        animSet.playTogether(flipAnim, transSet);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isFlipping = false;
                
                android.os.Vibrator v = (android.os.Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);
                if (v != null) v.vibrate(50);
                
                String resultStr = landOnHeads ? getString(R.string.coin_heads) : getString(R.string.coin_tails);
                binding.tvResult.setText(resultStr);
                
                // save
                String dbResult = landOnHeads ? "HEADS" : "TAILS";
                new Thread(() -> {
                    HistoryEntity history = new HistoryEntity("COIN", dbResult, System.currentTimeMillis());
                    AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
                }).start();
            }
        });

        animSet.start();
    }
}
