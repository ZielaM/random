package com.example.random.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PublicMemeApi {
    @GET("gimme")
    Call<PublicMemeResponse> getRandomMeme();
}
