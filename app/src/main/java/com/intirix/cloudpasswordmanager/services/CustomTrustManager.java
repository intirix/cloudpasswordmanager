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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * An X509TrustManager that an use either a pinned or default implementation.
 *
 * Created by jeff on 8/8/16.
 */
public class CustomTrustManager implements X509TrustManager {

    private final X509TrustManager defaultTrustManager;

    private X509TrustManager pinnedTrustManager;

    public CustomTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        defaultTrustManager = getDefaultTrustManager();
    }

    /**
     * Get the default trust manager
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    protected X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Using null here initialises the TMF with the default trust store.
        tmf.init((KeyStore) null);

        // Get hold of the default trust manager
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager)tm;
            }
        }
        return null;
    }

    public void setPinnedTrustManager(X509TrustManager pinnedTrustManager) {
        this.pinnedTrustManager = pinnedTrustManager;
    }

    public X509TrustManager getPinnedTrustManager() {
        return pinnedTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (pinnedTrustManager==null) {
            defaultTrustManager.checkClientTrusted(chain, authType);
        } else {
            pinnedTrustManager.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (pinnedTrustManager==null) {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } else {
            pinnedTrustManager.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (pinnedTrustManager==null) {
            return defaultTrustManager.getAcceptedIssuers();
        }
        return pinnedTrustManager.getAcceptedIssuers();
    }
}
