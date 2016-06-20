package com.intirix.cloudpasswordmanager.services;

import android.content.Context;
import android.util.Base64;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

import javax.inject.Inject;

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

    private SessionService sessionService;

    private String currentUrl;

    private PasswordRestService restService;

    @Inject
    public PasswordStorageServiceImpl(Context context, SessionService sessionService) {
        this.context = context;
        this.sessionService = sessionService;
    }

    protected PasswordRestService getRestService() {
        if (currentUrl==null||restService==null||!currentUrl.equals(sessionService.getUrl())) {
            currentUrl = sessionService.getUrl();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(currentUrl+"/index.php/apps/passwords/api/0.1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            restService = retrofit.create(PasswordRestService.class);
        }
        return restService;
    }

    private String getAuthHeader() {
        return "Basic " + Base64.encodeToString(String.format("%s:%s", sessionService.getUsername(), sessionService.getPassword()).getBytes(), Base64.NO_WRAP);
    }

    @Override
    public void getServerVersion(final VersionCallback cb) {
            Call<String> call = getRestService().getVersion(getAuthHeader());
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
}
