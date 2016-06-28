package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.beans.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by jeff on 6/20/16.
 */
public interface PasswordRestService {

    @GET("version")
    Call<String> getVersion();

    @GET("categories")
    Call<List<Category>> listCategories();
}
