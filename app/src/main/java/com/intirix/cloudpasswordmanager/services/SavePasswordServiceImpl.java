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
package com.intirix.cloudpasswordmanager.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.intirix.cloudpasswordmanager.services.ssl.CustomHostnameVerifier;
import com.intirix.cloudpasswordmanager.services.ssl.CustomTrustManager;
import com.intirix.cloudpasswordmanager.services.ui.EventService;

import javax.inject.Inject;

/**
 * Created by jeff on 10/23/16.
 */
public class SavePasswordServiceImpl implements SavePasswordService {

    private static final String TAG = SavePasswordServiceImpl.class.getSimpleName();

    private Context context;

    private SharedPreferences preferences;

    static final String PREF_SAVE_PASSWORD_SETTING = "SAVE_PASSWORD_SETTING_KEY";

    SavePasswordEnum currentSetting = SavePasswordEnum.NEVER;

    @Inject
    public SavePasswordServiceImpl(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.contains(PREF_SAVE_PASSWORD_SETTING)) {
            try {
                currentSetting = SavePasswordEnum.valueOf(preferences.getString(PREF_SAVE_PASSWORD_SETTING, SavePasswordEnum.NEVER.name()));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid Save Password setting: "+preferences.getString(PREF_SAVE_PASSWORD_SETTING,""));
            }
        }
    }

    public SavePasswordEnum getCurrentSetting() {
        return currentSetting;
    }

    @Override
    public boolean isPasswordSaved() {
        return false;
    }

    @Override
    public boolean isPasswordAvailable() {
        return false;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void changeSavePasswordSetting(SavePasswordEnum value) {
        preferences.edit().putString(PREF_SAVE_PASSWORD_SETTING, value.name()).commit();
        currentSetting = value;
    }
}
