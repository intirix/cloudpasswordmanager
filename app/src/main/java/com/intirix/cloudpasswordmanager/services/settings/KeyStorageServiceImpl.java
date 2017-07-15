package com.intirix.cloudpasswordmanager.services.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.intirix.cloudpasswordmanager.services.backend.ocp.OCPBackendRequestImpl;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * Created by jeff on 7/14/17.
 */

public class KeyStorageServiceImpl implements KeyStorageService {

    private static final String TAG = KeyStorageServiceImpl.class.getSimpleName();

    private Context context;

    private SessionService sessionService;

    private SharedPreferences preferences;

    private SharedPreferences deviceSpecific;

    static final String PREF_PRIVATE_KEY_SETTING = "PRIVATE_KEY";

    @Inject
    public KeyStorageServiceImpl(Context context, SessionService sessionService) {
        this.context = context;
        this.sessionService = sessionService;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        deviceSpecific = context.getSharedPreferences("device.xml", Context.MODE_PRIVATE);

    }


        @Override
    public boolean isPrivateKeyStored() {
        return deviceSpecific.contains(PREF_PRIVATE_KEY_SETTING);
    }

    @Override
    public void saveEncryptedPrivateKey(String key) throws IOException {
        Log.i(TAG,"Changing the private key");
        deviceSpecific.edit().putString(PREF_PRIVATE_KEY_SETTING, key).commit();
    }

    @Override
    public String getEncryptedPrivateKey() throws IOException {
        return deviceSpecific.getString(PREF_PRIVATE_KEY_SETTING, null);
    }

    @Override
    public void clearEncryptedPrivateKey() throws IOException {
        Log.i(TAG,"Removing the private key");
        deviceSpecific.edit().remove(PREF_PRIVATE_KEY_SETTING).commit();
    }
}
