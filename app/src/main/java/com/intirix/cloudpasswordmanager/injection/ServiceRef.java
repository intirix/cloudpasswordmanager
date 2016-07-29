package com.intirix.cloudpasswordmanager.injection;

import com.intirix.cloudpasswordmanager.pages.BaseActivity;
import com.intirix.cloudpasswordmanager.pages.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.SessionService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jeff on 6/18/16.
 */
@Component(modules = CloudPasswordManagerModule.class)
@Singleton
public interface ServiceRef {
//    PasswordStorageService storageService();

    SessionService sessionService();

    void inject(LoginActivity activity);
    void inject(BaseActivity activity);
    void inject(SecureActivity activity);
    void inject(PasswordListActivity activity);
    void inject(PasswordDetailActivity activity);
}
