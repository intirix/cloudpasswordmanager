package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.Log;

import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jeff on 7/15/17.
 */

public class SMAuthenticationInterceptor extends AuthenticationInterceptor {
    private static final String TAG = SMAuthenticationInterceptor.class.getSimpleName();
    private KeyStorageService keyStorageService;
    private SMEncryptionService encryptionService;

    SMAuthenticationInterceptor(SessionService sessionService, KeyStorageService keyStorageService, SMEncryptionService encryptionService) {
        super(sessionService);
        this.keyStorageService = keyStorageService;
        this.encryptionService = encryptionService;
    }

    @Override
    protected String getAuthHeader() {
        try {
            if (keyStorageService.isPrivateKeyStored()) {
                byte[] aesKey = encryptionService.keyExtend(sessionService.getUsername(), sessionService.getCurrentSession().getPassword());
                byte[] encryptedPrivateKey = encryptionService.decodeBase64(keyStorageService.getEncryptedPrivateKey());
                byte[] privateKey = encryptionService.decryptAES(aesKey, encryptedPrivateKey);

                String token = generateToken();
                byte[] sig = encryptionService.signRSA(new String(privateKey,"ASCII"), token.getBytes("ASCII"));
                String ssig = base64Encode(sig);
                String password = String.format("{\"token\":\"%s\",\"signed\":\"%s\"}", token, ssig);

                byte[] value = String.format("%s:%s", sessionService.getUsername(), password).getBytes("ASCII");
                return "Basic " + base64Encode(value);
            }
        } catch (Exception e) {
            Log.w(TAG,"Failed to decrypt private key",e);
        }
        return super.getAuthHeader();
    }

    private String generateToken() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
