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
package com.intirix.cloudpasswordmanager.services.settings;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.intirix.cloudpasswordmanager.pages.ErrorEvent;
import com.intirix.cloudpasswordmanager.services.SharedEncryptionService;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;

import org.spongycastle.util.encoders.Hex;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

/**
 * Created by jeff on 10/23/16.
 */
public class SavePasswordServiceImpl implements SavePasswordService {

    private static final String TAG = SavePasswordServiceImpl.class.getSimpleName();
    public static final String SAVE_PASSWORD_KEY = "SavePasswordKey";

    private Context context;

    private SharedPreferences preferences;

    private SharedPreferences deviceSpecific;

    private EventService eventService;

    static final String PREF_SAVE_PASSWORD_SETTING = "SAVE_PASSWORD_SETTING_KEY";

    SavePasswordEnum currentSetting = SavePasswordEnum.NEVER;

    private SessionService sessionService;

    private SharedEncryptionService sharedEncryptionService;

    @Inject
    public SavePasswordServiceImpl(Context context, SessionService sessionService,
                                   SharedEncryptionService sharedEncryptionService,
                                   EventService eventService) {
        this.context = context;
        this.sessionService = sessionService;
        this.sharedEncryptionService = sharedEncryptionService;
        this.eventService = eventService;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        deviceSpecific = context.getSharedPreferences("device.xml", Context.MODE_PRIVATE);

        if (deviceSpecific.contains(PREF_SAVE_PASSWORD_SETTING)) {
            try {
                currentSetting = SavePasswordEnum.valueOf(deviceSpecific.getString(PREF_SAVE_PASSWORD_SETTING, SavePasswordEnum.NEVER.name()));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Invalid Save Password setting: "+deviceSpecific.getString(PREF_SAVE_PASSWORD_SETTING,""));
            }
        }

        initKey();
    }

    private boolean shouldUseKeystore() {
        return SavePasswordEnum.KEYSTORE.equals(currentSetting);
    }
    private boolean initKey() {
        return initKey(false);
    }

    private boolean initKey(boolean generateEvents) {
        try {
            if (shouldUseKeystore()) {
                if (!sharedEncryptionService.doesKeyExist(SAVE_PASSWORD_KEY)) {
                    Log.d(TAG, "Generating key in keystore");
                    sharedEncryptionService.generateKey(SAVE_PASSWORD_KEY, false);
                }
            } else {
                if (!deviceSpecific.contains("key")) {
                    Log.d(TAG, "Generating key in preferences");
                    byte[] key = sharedEncryptionService.generateKey(32);
                    deviceSpecific.edit().putString("key", sharedEncryptionService.encodeBase64(key)).commit();
                }
            }
            return true;
        } catch (Exception e) {
            Log.d(TAG,"Failed to generate key",e);
            if (generateEvents) {
                eventService.postEvent(new ErrorEvent("Failed to generate key: "+e.getMessage()));
            }
        }
        return false;
    }

    private byte[] getKey() throws IOException {
        initKey();
        Log.d(TAG,"Getting key from preferences");
        final byte[] key = sharedEncryptionService.decodeBase64(deviceSpecific.getString("key",""));
        return key;
    }

    String encryptPassword(String password) {
        try {
            if (initKey(true)) {
                byte[] data = null;
                byte[] bytes = password.getBytes(Charset.defaultCharset());
                if (shouldUseKeystore()) {
                    Log.d(TAG, "Encrypting password using keystore");
                    data = sharedEncryptionService.encryptWithKey(SAVE_PASSWORD_KEY, bytes);
                } else {
                    Log.d(TAG, "Encrypting password using preferences");
                    data = sharedEncryptionService.encryptAES(getKey(), bytes);
                }
                return sharedEncryptionService.encodeBase64(data);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to encrypt password", e);
            eventService.postEvent(new ErrorEvent("Failed to encrypt password: "+e.getMessage()));
            return null;
        }
    }

    String decryptPassword(String encrypted) {
        initKey();
        try {
            byte[] encryptedBytes = sharedEncryptionService.decodeBase64(encrypted);
            byte[] data = null;
            if (shouldUseKeystore()) {
                Log.d(TAG,"Decrypting password using keystore");
                data = sharedEncryptionService.decryptAESWithKey(SAVE_PASSWORD_KEY, encryptedBytes);
            } else {
                Log.d(TAG,"Decrypting password using preferences");
                data = sharedEncryptionService.decryptAES(getKey(), encryptedBytes);
            }
            return new String(data,Charset.defaultCharset());
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
        if (SavePasswordEnum.ALWAYS.equals(currentSetting)) {
            return true;
        }
        if (SavePasswordEnum.PASSWORD_PROTECTED.equals(currentSetting)) {
            return hasPasscode();
        }
        try {
            if (SavePasswordEnum.KEYSTORE.equals(currentSetting)) {
                return sharedEncryptionService.doesKeyExist(SAVE_PASSWORD_KEY);
            }
        } catch (Exception e) {
            Log.w(TAG,"Failed to get key",e);
        }
        return false;
    }

    @TargetApi(16)
    private boolean hasPasscode() {
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardSecure();
    }


    @Override
    public String getPassword() {
        return decryptPassword(deviceSpecific.getString("password",""));
    }

    @Override
    public List<SavePasswordEnum> listAvailableOptions() {
        final List<SavePasswordEnum> list = new ArrayList<>();

        list.add(SavePasswordEnum.NEVER);
        list.add(SavePasswordEnum.ALWAYS);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
            list.add(SavePasswordEnum.PASSWORD_PROTECTED);
        } else {
            Log.d(TAG, "Skipping password protected option because API level is too old");
        }
        list.add(SavePasswordEnum.KEYSTORE);

        return list;
    }

    @Override
    public boolean changeSavePasswordSetting(SavePasswordEnum value) {
        SavePasswordEnum oldSetting = currentSetting;
        try {
            Log.d(TAG,"Attempting to change setting to "+value.name());
            currentSetting = value;
            SharedPreferences.Editor ed = deviceSpecific.edit();
            ed.putString(PREF_SAVE_PASSWORD_SETTING, value.name());
            String encryptedPassword = null;
            boolean removePassword = false;
            if (SavePasswordEnum.ALWAYS.equals(value) || SavePasswordEnum.KEYSTORE.equals(value)) {
                encryptedPassword = encryptPassword(sessionService.getCurrentSession().getPassword());
            } else if (SavePasswordEnum.PASSWORD_PROTECTED.equals(value)) {
                if (hasPasscode()) {
                    Log.d(TAG,"Device has a passcode");
                    encryptedPassword = encryptPassword(sessionService.getCurrentSession().getPassword());
                } else {
                    Log.d(TAG,"Removing previously encrypting password");
                    encryptedPassword = null;
                    removePassword = true;
                }
            } else {
                Log.d(TAG,"Removing previously encrypting password");
                encryptedPassword = null;
                removePassword = true;
            }

            if (removePassword) {
                ed.remove("password");
            } else if (encryptedPassword==null) {
                currentSetting = oldSetting;
                Log.w(TAG,"Failed to change save password setting");
                return false;
            } else {
                ed.putString("password",encryptedPassword);
            }

            ed.commit();
            Log.d(TAG,"Successfully changed save password setting to"+value);
            return true;
        } catch (Exception e) {
            Log.d(TAG,"Failed to change settings",e);
            currentSetting = oldSetting;
            return false;
        }
    }
}
