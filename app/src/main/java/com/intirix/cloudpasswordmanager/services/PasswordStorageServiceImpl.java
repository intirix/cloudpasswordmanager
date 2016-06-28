package com.intirix.cloudpasswordmanager.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.beans.Category;
import com.intirix.cloudpasswordmanager.services.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jeff on 6/18/16.
 */
public class PasswordStorageServiceImpl implements PasswordStorageService {

    private Context context;

    private OkHttpClient client;

    private SessionService sessionService;

    private String currentUrl;

    private PasswordRestService restService;

    @Inject
    public PasswordStorageServiceImpl(@NonNull Context context, @NonNull SessionService sessionService, OkHttpClient client) {
        this.context = context;
        this.sessionService = sessionService;
        this.client = client;
    }

    protected PasswordRestService getRestService() {
        if (currentUrl==null||restService==null||!currentUrl.equals(sessionService.getUrl())) {
            currentUrl = sessionService.getUrl();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(currentUrl+"/index.php/apps/passwords/api/0.1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            restService = retrofit.create(PasswordRestService.class);
        }
        return restService;
    }

    @Override
    public void getServerVersion(final VersionCallback cb) {
            Call<String> call = getRestService().getVersion();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code()==401) {
                        cb.onError(context.getString(R.string.error_invalid_username_password).toString());
                    } else if (response.code()==200) {
                        cb.onReturn(response.body());
                    } else {
                        cb.onError(response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    cb.onError("Failed: " + t);
                }
            });
    }

    @Override
    public void listCategories(final CategoryListCallback cb) {
        Call<List<Category>> call = getRestService().listCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                final List<Category> list = new ArrayList<Category>(response.body().size());
                for (final Category c: response.body()) {
                    if (sessionService.getUsername().equals(c.getUser_id())) {
                        list.add(c);
                    }
                }
                cb.onReturn(list);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                cb.onError("Failed: " + t);
            }
        });
    }
}
