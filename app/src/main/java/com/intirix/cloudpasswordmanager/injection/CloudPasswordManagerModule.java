package com.intirix.cloudpasswordmanager.injection;

import com.intirix.cloudpasswordmanager.services.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.PasswordStorageServiceImpl;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.SessionServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jeff on 6/18/16.
 */
@Module
@Singleton
public class CloudPasswordManagerModule {

    @Provides @Singleton
    SessionService provideSessionService() {
        return new SessionServiceImpl();
    }

    @Provides
    PasswordStorageService providePasswordStorageService(PasswordStorageServiceImpl impl) {
        return impl;
    }
}
