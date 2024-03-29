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
package com.intirix.cloudpasswordmanager.services.backend.ocp;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.PasswordResponse;
import com.intirix.cloudpasswordmanager.services.backend.ocp.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.backend.ocp.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.backend.ocp.callbacks.VersionCallback;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private static final String TAG = PasswordStorageServiceImpl.class.getSimpleName();

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
                        cb.onError(context.getString(R.string.error_invalid_username_password));
                    } else if (response.code()==200) {
                        cb.onReturn(response.body());
                    } else {
                        Log.w(TAG, "getServerVersion() - "+response.code()+":  "+response.message());
                        cb.onError(response.code()+": "+response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.w(TAG, "getServerVersion() failed", t);
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
                final List<Category> list = new ArrayList<>(response.body().size());
                for (final Category c: response.body()) {
                    if (sessionService.getUsername().equals(c.getUser_id())) {
                        list.add(c);
                    }
                }
                cb.onReturn(list);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.w(TAG, "listCategories() failed", t);
                cb.onError("Failed: " + t);
            }
        });
    }

    protected boolean shouldUseNewApi(String serverVersion) {
        if (serverVersion==null) {
            return true;
        }

        if (serverVersion.startsWith("18-")||serverVersion.equals("18")) {
            return false;
        }
        if (serverVersion.startsWith("19-")||serverVersion.equals("19")) {
            return false;
        }

        return true;
    }

    @Override
    public void listPasswords(final PasswordListCallback cb) {
        String version = sessionService.getCurrentSession().getPasswordServerAppVersion();
        if (shouldUseNewApi(version)) {
            listPasswordsV2(cb);
        } else {
            listPasswordsV1(cb);
        }
    }

    private void listPasswordsV2(final PasswordListCallback cb) {
        Call<Map<String,PasswordResponse>> call = getRestService().listPasswordsV2();
        call.enqueue(new Callback<Map<String,PasswordResponse>>() {
            @Override
            public void onResponse(Call<Map<String,PasswordResponse>> call, Response<Map<String,PasswordResponse>> response) {
                try {
                    List<PasswordInfo> list = translatePasswordList(response.body().values());
                    cb.onReturn(list);
                } catch (Exception e) {
                    Log.w(TAG, "listPasswords() errored", e);
                    cb.onError("Error: " + e);
                }
            }

            @Override
            public void onFailure(Call<Map<String,PasswordResponse>> call, Throwable t) {
                Log.w(TAG, "listPasswords() failed", t);
                cb.onError("Failed: " + t);
            }
        });
    }

    private void listPasswordsV1(final PasswordListCallback cb) {
        Call<List<PasswordResponse>> call = getRestService().listPasswords();
        call.enqueue(new Callback<List<PasswordResponse>>() {
            @Override
            public void onResponse(Call<List<PasswordResponse>> call, Response<List<PasswordResponse>> response) {
                try {
                    List<PasswordInfo> list = translatePasswordList(response.body());
                    cb.onReturn(list);
                } catch (Exception e) {
                    Log.w(TAG, "listPasswords() errored", e);
                    cb.onError("Error: " + e);
                }
            }

            @Override
            public void onFailure(Call<List<PasswordResponse>> call, Throwable t) {
                Log.w(TAG, "listPasswords() failed", t);
                cb.onError("Failed: " + t);
            }
        });
    }

    @NonNull
    List<PasswordInfo> translatePasswordList(Collection<PasswordResponse> response) throws ParseException {
        List<PasswordInfo> list = new ArrayList<>();
        for (final PasswordResponse pr : response) {
            boolean valid = "0".equals(pr.getDeleted()) || "false".equals(pr.getDeleted());
            if (pr.getProperties()!=null && valid) {
                list.add(createPasswordInfo(pr));
            }
        }
        return list;
    }

    PasswordInfo createPasswordInfo(PasswordResponse pr) throws ParseException {
        PasswordInfo ret = new PasswordInfo();

        // set the basic fields
        ret.setId(pr.getId());
        ret.setUser_id(pr.getUser_id());
        ret.setPass(pr.getPass());
        ret.setWebsite(pr.getWebsite());
        ret.setHasNotes(pr.isNotes());

        if (pr.getProperties()!=null && pr.getProperties().length()>2) {
            try {
                String json = pr.getProperties();
                if (!json.startsWith("{") && !json.endsWith("}")) {
                    json = '{' + pr.getProperties() + '}';
                }

                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

                if (obj.has("loginname")) {
                    ret.setLoginName(obj.get("loginname").getAsString());
                }
                if (obj.has("address")) {
                    ret.setAddress(obj.get("address").getAsString());
                }
                if (obj.has("notes")) {
                    ret.setNotes(obj.get("notes").getAsString());
                }
                if (obj.has("category")) {
                    ret.setCategory(obj.get("category").getAsString());
                }

                if (obj.has("length")) {
                    ret.setLength(obj.get("length").getAsInt());
                }
                if (obj.has("strength")) {
                    ret.setStrength(obj.get("strength").getAsInt());
                }

                if (obj.has("lower")) {
                    ret.setHasLower("1".equals(obj.get("lower").getAsString()));
                } else {
                    ret.setHasLower(false);
                }
                if (obj.has("upper")) {
                    ret.setHasUpper("1".equals(obj.get("upper").getAsString()));
                } else {
                    ret.setHasUpper(false);
                }
                if (obj.has("number")) {
                    ret.setHasNumber("1".equals(obj.get("number").getAsString()));
                } else {
                    ret.setHasNumber(false);
                }
                if (obj.has("special")) {
                    ret.setHasSpecial("1".equals(obj.get("special").getAsString()));
                } else {
                    ret.setHasSpecial(false);
                }

                if (obj.has("datechanged")) {
                    Date d = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("datechanged").getAsString());
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    ret.setDateChanged(c);
                }
                ret.setDecrypted(true);
            } catch (JsonSyntaxException e) {
                ret.setDecrypted(false);
                ret.setNotes(e.getMessage());
            }

        }

        return ret;
    }
}
