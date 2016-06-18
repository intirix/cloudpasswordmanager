package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

/**
 * Created by jeff on 6/18/16.
 */
public interface PasswordStorageService {

    void getServerVersion(VersionCallback cb);
}
