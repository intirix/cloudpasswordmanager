package com.intirix.cloudpasswordmanager.services;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/27/16.
 */
public class AuthenticationInterceptor implements Interceptor {

    private SessionService sessionService;

    public AuthenticationInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().addHeader("Authorization", getAuthHeader()).build();
        return chain.proceed(request);
    }


    private String getAuthHeader() {
        return "Basic " + Base64.encodeToString(String.format("%s:%s", sessionService.getUsername(), sessionService.getPassword()).getBytes(), Base64.NO_WRAP);
    }

}
