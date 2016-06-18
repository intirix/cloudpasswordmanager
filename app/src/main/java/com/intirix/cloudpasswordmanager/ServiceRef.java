package com.intirix.cloudpasswordmanager;

import com.intirix.cloudpasswordmanager.services.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.SessionService;

import dagger.Component;

/**
 * Created by jeff on 6/18/16.
 */
@Component(modules = CloudPasswordManagerModule.class)
public interface ServiceRef {
    PasswordStorageService storageService();

    SessionService sessionService();
}
