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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.intirix.cloudpasswordmanager.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jeff on 8/8/16.
 */
public class CertPinningServiceImpl implements CertPinningService {

    private static final String TAG = CertPinningServiceImpl.class.getSimpleName();
    public static final String KEYSTORE_FILENAME = "keystore";
    public static final String PREF_PINNED_VALID_CERT = "PIN_VALID";

    private Context context;

    private EventService eventService;

    private CustomTrustManager customTrustManager;

    private boolean valid = false;

    private boolean enabled = false;

    private static final String KEYSTORE_PASSWORD = "Vq2kgW{yc2Z%{7_<";

    SharedPreferences preferences;

    @Inject
    public CertPinningServiceImpl(Context context, CustomTrustManager customTrustManager, EventService eventService) {
        this.context = context;
        this.customTrustManager = customTrustManager;
        this.eventService = eventService;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void init() {
        enabled = false;
        valid = false;
        customTrustManager.setPinnedTrustManager(null);
        try {
            final FileInputStream fis = context.openFileInput(KEYSTORE_FILENAME);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, KEYSTORE_PASSWORD.toCharArray());

            customTrustManager.setPinnedTrustManager(getTrustManagerForKeystore(ks));
            enabled = true;
            valid = preferences.getBoolean(PREF_PINNED_VALID_CERT, valid);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No pinned keystore found");
        } catch (Exception e) {
            Log.w(TAG, "Error during init", e);
        }
    }

    @Override
    public void disable() {
        enabled = false;
        customTrustManager.setPinnedTrustManager(null);
        context.deleteFile(KEYSTORE_FILENAME);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void pin(final String url) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                pinUrl(url);
            }
        });
    }

    private void pinUrl(String url) {
        try {
            SavingTrustManager stm = downloadCert(url);
            X509TrustManager tm = createKeystore(stm.getChain()[0]);
            customTrustManager.setPinnedTrustManager(tm);
            enabled = true;
            valid = stm.isValid();
            preferences.edit().putBoolean(PREF_PINNED_VALID_CERT, valid).commit();
            eventService.postEvent(new PinSuccessfulEvent());
        } catch (Exception e) {
            String message = e.getMessage();
            if (message==null) {
                message = context.getString(R.string.error_unknown_error);
            }
            eventService.postEvent(new PinFailedEvent(message));
        }
    }

    protected SavingTrustManager downloadCert(String aurl) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        URL url = new URL(aurl);
        int port = url.getPort();
        if (port<=0) {
            port = 443;
        }

        X509TrustManager defaultTrustManager = getDefaultTrustManager();

        SSLContext context = SSLContext.getInstance("TLS");
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[] {tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();


        SSLSocket socket = (SSLSocket)factory.createSocket(url.getHost(), port);
        socket.setSoTimeout(10000);
        socket.startHandshake();
        socket.close();

        return tm;

    }

    /**
     * Get the default trust manager
     * @return the default trust manager
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    @Nullable
    private X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Using null here initialises the TMF with the default trust store.
        tmf.init((KeyStore) null);

        // Get hold of the default trust manager
        X509TrustManager defaultTrustManager = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                defaultTrustManager = (X509TrustManager) tm;
                break;
            }
        }
        return defaultTrustManager;
    }

    /**
     * Create and save the keystore for the certificate
     * @param cert
     * @return the trust manager for the keystore
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    protected X509TrustManager createKeystore(X509Certificate cert) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("server", cert);

        final FileOutputStream fos = context.openFileOutput(KEYSTORE_FILENAME, Context.MODE_PRIVATE);
        try {
            ks.store(fos, KEYSTORE_PASSWORD.toCharArray());
        } finally {
            fos.close();
        }
        return getTrustManagerForKeystore(ks);
    }

    /**
     * Get the trust manager for a keystore
     * @param ks
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private X509TrustManager getTrustManagerForKeystore(KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(ks);
        return (X509TrustManager)trustManagerFactory.getTrustManagers()[0];
    }
}
