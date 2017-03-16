package com.example.parkhere.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("parkhere/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}

