package com.intirix.cloudpasswordmanager.injection;

import android.content.Context;

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
    OkHttpClient provideHttpClient() {
        OkHttpClient okClient = new OkHttpClient.Builder().build();
        return okClient;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides @Singleton
    SessionService provideSessionService(SessionServiceImpl impl) {
        return impl;
    }

    @Provides
    PasswordStorageService providePasswordStorageService(PasswordStorageServiceImpl impl) {
        return impl;
    }
}
