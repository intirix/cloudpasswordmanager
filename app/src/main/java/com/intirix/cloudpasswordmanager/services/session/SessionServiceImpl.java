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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import javax.inject.Inject;

/**
 * Created by jeff on 6/18/16.
 */
public class SessionServiceImpl implements SessionService {
    private static final String TAG = SessionServiceImpl.class.getSimpleName();

    public static final StorageType DEFAULT_STORAGE_TYPE = StorageType.OWNCLOUD_PASSWORDS;

    public static final String STORAGE_TYPE_KEY = "last.storageType";
    public static final String URL_KEY = "last.url";
    public static final String USERNAME_KEY = "last.user";

    private Context context;

    SharedPreferences preferences;

    private StorageType storageType = DEFAULT_STORAGE_TYPE;

    private String url;

    private String username;

    private SessionInfo currentSession;

    @Inject
    public SessionServiceImpl(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String storageTypeName = preferences.getString(STORAGE_TYPE_KEY,DEFAULT_STORAGE_TYPE.name());
        try {
            setStorageType(StorageType.valueOf(storageTypeName));
        } catch (IllegalArgumentException e) {
            Log.w(TAG,"Invalid storage type: "+storageTypeName, e);
            setStorageType(DEFAULT_STORAGE_TYPE);
        }
        setUrl(preferences.getString(URL_KEY, null));
        setUsername(preferences.getString(USERNAME_KEY, null));
    }

    @Override
    public void start() {
        preferences.edit()
                .putString(URL_KEY,url)
                .putString(USERNAME_KEY,username)
                .putString(STORAGE_TYPE_KEY,storageType.name())
                .commit();
        currentSession = new SessionInfo();
    }

    @Override
    public void end() {
        currentSession = null;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public void setStorageType(StorageType storageType) {
        if (storageType!=null) {
            this.storageType = storageType;
        }
    }

    @Override
    public SessionInfo getCurrentSession() {
        return currentSession;
    }
}
