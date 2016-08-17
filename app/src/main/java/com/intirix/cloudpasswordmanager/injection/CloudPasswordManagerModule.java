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
package com.intirix.cloudpasswordmanager.injection;

import android.content.Context;
import android.util.Log;

import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.AutoLogoffService;
import com.intirix.cloudpasswordmanager.services.session.AutoLogoffServiceImpl;
import com.intirix.cloudpasswordmanager.services.ssl.CertPinningService;
import com.intirix.cloudpasswordmanager.services.ssl.CertPinningServiceImpl;
import com.intirix.cloudpasswordmanager.services.ClipboardService;
import com.intirix.cloudpasswordmanager.services.ClipboardServiceImpl;
import com.intirix.cloudpasswordmanager.services.ColorService;
import com.intirix.cloudpasswordmanager.services.ColorServiceImpl;
import com.intirix.cloudpasswordmanager.services.ssl.CustomHostnameVerifier;
import com.intirix.cloudpasswordmanager.services.ssl.CustomTrustManager;
import com.intirix.cloudpasswordmanager.services.EventService;
import com.intirix.cloudpasswordmanager.services.EventServiceImpl;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestServiceImpl;
import com.intirix.cloudpasswordmanager.services.backend.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordStorageServiceImpl;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.session.SessionServiceImpl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Created by jeff on 6/18/16.
 */
@Module
@Singleton
public class CloudPasswordManagerModule {

    private static final String TAG = CloudPasswordManagerModule.class.getSimpleName();

    private Context context;

    public CloudPasswordManagerModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    CustomTrustManager provideCustomTrustManager() {
        try {
            Log.d(TAG,"provideCustomTrustManager()");
            return new CustomTrustManager();
        } catch (KeyStoreException e) {
            Log.e(TAG, "Failed to create CustomTrustManager", e);
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to create CustomTrustManager", e);
            throw new RuntimeException(e);
        }
    }

    @Provides @Singleton
    CustomHostnameVerifier provideCustomHostnameVerifier() {
        return new CustomHostnameVerifier(OkHostnameVerifier.INSTANCE);
    }

    @Provides
    SSLSocketFactory provideSSL(CustomTrustManager customTrustManager) {
        Log.d(TAG,"provideSSL()");
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] {customTrustManager}, null);
            SSLSocketFactory factory = context.getSocketFactory();
            return factory;
        } catch (KeyManagementException e) {
            Log.e(TAG, "Failed to create CustomTrustManager", e);
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to create CustomTrustManager", e);
            throw new RuntimeException(e);
        }

    }

    @Provides @Singleton
    OkHttpClient provideHttpClient(SSLSocketFactory sslSocketFactory, CustomTrustManager customTrustManager, CustomHostnameVerifier customHostnameVerifier, SessionService sessionService) {
        Log.d(TAG,"provideHttpClient()");

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(sessionService));

        builder.sslSocketFactory(sslSocketFactory, customTrustManager);
        builder.hostnameVerifier(customHostnameVerifier);

        OkHttpClient okClient = builder.build();
        return okClient;
    }

    @Provides @Singleton
    CertPinningService provideCertPinningService(CertPinningServiceImpl impl) {
        Log.d(TAG,"provideCertPinningService()");
        return impl;
    }

    @Provides
    EventService provideEventService() {
        return new EventServiceImpl();
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides @Singleton
    ColorService provideColorService(ColorServiceImpl impl) {
        return impl;
    }

    @Provides @Singleton
    SessionService provideSessionService(SessionServiceImpl impl) {
        return impl;
    }

    @Provides
    PasswordStorageService providePasswordStorageService(PasswordStorageServiceImpl impl) {
        return impl;
    }

    @Provides
    PasswordRequestService providePasswordRequestService(PasswordRequestServiceImpl impl) {
        return impl;
    }

    @Provides
    AutoLogoffService provideAutoLogoffService(AutoLogoffServiceImpl impl) {
        return impl;
    }

    @Provides
    ClipboardService provideClipboardService(ClipboardServiceImpl impl) {
        return impl;
    }
}
