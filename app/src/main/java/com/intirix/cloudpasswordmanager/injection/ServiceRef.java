package com.intirix.cloudpasswordmanager.injection;

import com.intirix.cloudpasswordmanager.LoginActivity;
import com.intirix.cloudpasswordmanager.PasswordListActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jeff on 6/18/16.
 */
@Component(modules = CloudPasswordManagerModule.class)
@Singleton
public interface ServiceRef {
//    PasswordStorageService storageService();

//    SessionService sessionService();

    void inject(LoginActivity activity);
    void inject(PasswordListActivity activity);
}
