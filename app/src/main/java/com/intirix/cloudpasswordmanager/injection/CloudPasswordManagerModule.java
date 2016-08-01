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

import com.intirix.cloudpasswordmanager.services.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.AutoLogoffService;
import com.intirix.cloudpasswordmanager.services.AutoLogoffServiceImpl;
import com.intirix.cloudpasswordmanager.services.ClipboardService;
import com.intirix.cloudpasswordmanager.services.ClipboardServiceImpl;
import com.intirix.cloudpasswordmanager.services.ColorService;
import com.intirix.cloudpasswordmanager.services.ColorServiceImpl;
import com.intirix.cloudpasswordmanager.services.EventService;
import com.intirix.cloudpasswordmanager.services.EventServiceImpl;
import com.intirix.cloudpasswordmanager.services.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.PasswordRequestServiceImpl;
import com.intirix.cloudpasswordmanager.services.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.PasswordStorageServiceImpl;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.SessionServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by jeff on 6/18/16.
 */
@Module
@Singleton
public class CloudPasswordManagerModule {

    private Context context;

    public CloudPasswordManagerModule(Context context) {
        this.context = context;
    }

    @Provides
    OkHttpClient provideHttpClient(SessionService sessionService) {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(sessionService))
                .build();
        return okClient;
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
