package com.intirix.cloudpasswordmanager.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by jeff on 6/20/16.
 */
public interface PasswordRestService {

    @GET("version")
    Call<String> getVersion(@Header("Authorization") String authorization);
}
