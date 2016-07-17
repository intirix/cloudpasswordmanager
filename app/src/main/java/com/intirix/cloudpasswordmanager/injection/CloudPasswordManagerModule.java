package com.intirix.cloudpasswordmanager.injection;

import android.content.Context;

import com.intirix.cloudpasswordmanager.services.AuthenticationInterceptor;
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
}
