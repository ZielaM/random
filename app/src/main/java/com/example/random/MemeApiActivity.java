package com.example.random;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.random.api.MemeApi;
import com.example.random.api.MemeResponse;
import com.example.random.api.RetrofitClient;
import com.example.random.databinding.ActivityMemeApiBinding;
import com.example.random.db.AppDatabase;
import com.example.random.db.HistoryEntity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemeApiActivity extends AppCompatActivity {

    private ActivityMemeApiBinding binding;

    private static final String PREFS_NAME = "MemeApiPrefs";
    private static final String KEY_API_KEY = "apiKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemeApiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Load saved API key
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedKey = prefs.getString(KEY_API_KEY, "");
        binding.etApiKey.setText(savedKey);

        binding.btnFetch.setOnClickListener(v -> fetchMeme());
    }

    private void fetchMeme() {
        binding.tilApiKey.setError(null);
        String apiKey = binding.etApiKey.getText().toString().trim();
        
        binding.progressBar.setVisibility(View.VISIBLE);

        if (apiKey.isEmpty()) {
            // Use public fallback API
            com.example.random.api.PublicMemeApi publicApi = RetrofitClient.getPublicClient().create(com.example.random.api.PublicMemeApi.class);
            Call<com.example.random.api.PublicMemeResponse> call = publicApi.getRandomMeme();
            call.enqueue(new Callback<com.example.random.api.PublicMemeResponse>() {
                @Override
                public void onResponse(Call<com.example.random.api.PublicMemeResponse> call, Response<com.example.random.api.PublicMemeResponse> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.random.api.PublicMemeResponse meme = response.body();
                        binding.tvDescription.setText(meme.getTitle() != null ? meme.getTitle() : getString(R.string.msg_no_desc));
                        
                        Glide.with(MemeApiActivity.this)
                                .load(meme.getUrl())
                                .into(binding.ivMeme);

                        new Thread(() -> {
                            HistoryEntity history = new HistoryEntity("MEME", meme.getUrl(), System.currentTimeMillis());
                            AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
                        }).start();
                    } else {
                        Toast.makeText(MemeApiActivity.this, getString(R.string.msg_error) + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.random.api.PublicMemeResponse> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(MemeApiActivity.this, getString(R.string.msg_failed) + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        // Save API key
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_API_KEY, apiKey)
                .apply();

        MemeApi memeApi = RetrofitClient.getClient().create(MemeApi.class);

        Call<MemeResponse> call = memeApi.getRandomMeme(apiKey, 30);
        call.enqueue(new Callback<MemeResponse>() {
            @Override
            public void onResponse(Call<MemeResponse> call, Response<MemeResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    MemeResponse meme = response.body();
                    binding.tvDescription.setText(meme.getDescription() != null ? meme.getDescription() : getString(R.string.msg_no_desc));
                    
                    Glide.with(MemeApiActivity.this)
                            .load(meme.getUrl())
                            .into(binding.ivMeme);

                    new Thread(() -> {
                        HistoryEntity history = new HistoryEntity("MEME", meme.getUrl(), System.currentTimeMillis());
                        AppDatabase.getDatabase(getApplicationContext()).historyDao().insert(history);
                    }).start();

                } else {
                    Toast.makeText(MemeApiActivity.this, getString(R.string.msg_error) + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MemeResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MemeApiActivity.this, getString(R.string.msg_failed) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
