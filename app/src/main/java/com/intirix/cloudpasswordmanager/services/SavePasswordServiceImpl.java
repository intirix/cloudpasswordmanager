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
import android.util.Base64;
import android.util.Log;

import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.ssl.CustomHostnameVerifier;
import com.intirix.cloudpasswordmanager.services.ssl.CustomTrustManager;
import com.intirix.cloudpasswordmanager.services.ui.EventService;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import static android.R.attr.data;

/**
 * Created by jeff on 10/23/16.
 */
public class SavePasswordServiceImpl implements SavePasswordService {

    private static final String TAG = SavePasswordServiceImpl.class.getSimpleName();

    private Context context;

    private SharedPreferences preferences;

    private SharedPreferences deviceSpecific;

    static final String PREF_SAVE_PASSWORD_SETTING = "SAVE_PASSWORD_SETTING_KEY";

    SavePasswordEnum currentSetting = SavePasswordEnum.NEVER;

    private SessionService sessionService;

    @Inject
    public SavePasswordServiceImpl(Context context, SessionService sessionService) {
        this.context = context;
        this.sessionService = sessionService;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        deviceSpecific = context.getSharedPreferences("device.xml", Context.MODE_PRIVATE);

        if (deviceSpecific.contains(PREF_SAVE_PASSWORD_SETTING)) {
            try {
                currentSetting = SavePasswordEnum.valueOf(deviceSpecific.getString(PREF_SAVE_PASSWORD_SETTING, SavePasswordEnum.NEVER.name()));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid Save Password setting: "+deviceSpecific.getString(PREF_SAVE_PASSWORD_SETTING,""));
            }
        }
    }

    private void initKey() {
        if (!deviceSpecific.contains("key")) {

            final int outputKeyLength = 256;

            try {
                SecureRandom secureRandom = new SecureRandom();
                // Do *not* seed secureRandom! Automatically seeded from system entropy.
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(outputKeyLength, secureRandom);
                SecretKey key = keyGenerator.generateKey();
                deviceSpecific.edit().putString("key", Base64.encodeToString(key.getEncoded(), Base64.DEFAULT)).commit();
            } catch (Exception e){
                Log.e(TAG, "Failed to create encryption key", e);
            }
        }
    }

    String encryptPassword(String password) {
        initKey();
        try {
            byte[] key = Base64.decode(deviceSpecific.getString("key", ""), Base64.DEFAULT);

            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return Base64.encodeToString(cipher.doFinal(password.getBytes(Charset.defaultCharset())), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Failed to encrypt password", e);
            return null;
        }
    }

    String decryptPassword(String encrypted) {
        initKey();
        try {
            byte[] key = Base64.decode(deviceSpecific.getString("key", ""), Base64.DEFAULT);

            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] data = cipher.doFinal(Base64.decode(encrypted.getBytes(Charset.defaultCharset()),Base64.DEFAULT));
            return new String(data);
        } catch (Exception e) {
            Log.e(TAG, "Failed to encrypt password", e);
            return null;
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
        return decryptPassword(deviceSpecific.getString("password",""));
    }

    @Override
    public void changeSavePasswordSetting(SavePasswordEnum value) {
        SharedPreferences.Editor ed = deviceSpecific.edit();
        ed.putString(PREF_SAVE_PASSWORD_SETTING, value.name());
        if (SavePasswordEnum.ALWAYS.equals(value)) {
            ed.putString("password", encryptPassword(sessionService.getCurrentSession().getPassword()));
        } else {
            ed.remove("password");
        }
        ed.commit();
        currentSetting = value;
    }
}
