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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Trust manager that saves the certificate chain so that it can later be trusted
 * Created by jeff on 8/8/16.
 */
public class SavingTrustManager implements X509TrustManager {
    private final X509TrustManager child;
    private X509Certificate[] chain;
    private boolean valid = false;

    public SavingTrustManager(X509TrustManager child) {
        this.child = child;
    }

    public X509Certificate[] getAcceptedIssuers() {
        throw new UnsupportedOperationException();
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        this.chain = chain;
        if (child!=null) {
            try {
                child.checkServerTrusted(chain, authType);
                valid = true;
            } catch (CertificateException e) {
                // ignore
            }
        }
    }

    public X509Certificate[] getChain() {
        return chain;
    }

    public boolean isValid() {
        return valid;
    }
}
