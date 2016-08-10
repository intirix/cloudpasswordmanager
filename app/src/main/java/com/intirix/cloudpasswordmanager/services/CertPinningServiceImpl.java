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
import android.os.Handler;

import com.intirix.cloudpasswordmanager.R;

import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.net.ssl.X509TrustManager;

/**
 * Created by jeff on 8/8/16.
 */
public class CertPinningServiceImpl implements CertPinningService {

    private Context context;

    private EventService eventService;

    private CustomTrustManager customTrustManager;

    private boolean valid = false;

    private boolean enabled = false;

    @Inject
    public CertPinningServiceImpl(Context context, CustomTrustManager customTrustManager, EventService eventService) {
        this.context = context;
        this.customTrustManager = customTrustManager;
        this.eventService = eventService;
    }

    @Override
    public void init() {

    }

    @Override
    public void disable() {
        enabled = false;
        customTrustManager.setPinnedTrustManager(null);
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
            eventService.postEvent(new PinSuccessfulEvent());
        } catch (Exception e) {
            String message = e.getMessage();
            if (message==null) {
                message = context.getString(R.string.error_unknown_error);
            }
            eventService.postEvent(new PinFailedEvent(message));
        }
    }

    protected SavingTrustManager downloadCert(String url) {
        return null;
    }

    protected X509TrustManager createKeystore(X509Certificate cert) {
        return null;
    }
}
