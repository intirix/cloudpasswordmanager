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
package com.intirix.cloudpasswordmanager.services.ssl;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by jeff on 8/11/16.
 */
public class CustomHostnameVerifier implements HostnameVerifier {

    private static final String TAG = CustomHostnameVerifier.class.getSimpleName();

    private boolean enabled;

    private HostnameVerifier child;

    public CustomHostnameVerifier(HostnameVerifier child) {
        this.child = child;
        enabled = true;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (enabled) {
            return child.verify(hostname, session);
        }
        Log.d(TAG, "verify("+hostname+") - skipping verification");
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
