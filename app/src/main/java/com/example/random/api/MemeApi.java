package com.example.random.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MemeApi {
    @GET("retrieve-random-meme")
    Call<MemeResponse> getRandomMeme(
            @Query("api-key") String apiKey,
            @Query("max-age-days") Integer maxAgeDays
    );
}
