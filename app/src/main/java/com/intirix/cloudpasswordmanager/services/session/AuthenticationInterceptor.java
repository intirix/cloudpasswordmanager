/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.services.session;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jeff on 6/27/16.
 */
public class AuthenticationInterceptor implements Interceptor {

    protected SessionService sessionService;

    public AuthenticationInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String authValue = getAuthHeader();
        Log.e(AuthenticationInterceptor.class.getSimpleName(),"auth="+authValue);
        Request request = chain.request().newBuilder().addHeader("Authorization", authValue).build();
        return chain.proceed(request);
    }

    protected String base64Encode(byte[] input) {
        return Base64.encodeToString(input,Base64.NO_WRAP);
    }

    protected String getAuthHeader() {
        byte[] value;
        try {
            value = String.format("%s:%s", sessionService.getUsername(), sessionService.getCurrentSession().getPassword()).getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            value = String.format("%s:%s", sessionService.getUsername(), sessionService.getCurrentSession().getPassword()).getBytes();
        }
        return "Basic " + base64Encode(value);
    }

}
