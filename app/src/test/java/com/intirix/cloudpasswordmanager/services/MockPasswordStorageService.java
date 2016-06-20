package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

/**
 * Created by jeff on 6/19/16.
 */
public class MockPasswordStorageService implements PasswordStorageService {

    private VersionCallback lastVersionCallback;

    @Override
    public void getServerVersion(VersionCallback cb) {
        this.lastVersionCallback = cb;
    }

    public VersionCallback getLastVersionCallback() {
        return lastVersionCallback;
    }
}
