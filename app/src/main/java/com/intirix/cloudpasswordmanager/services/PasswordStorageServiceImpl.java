package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

import javax.inject.Inject;

/**
 * Created by jeff on 6/18/16.
 */
public class PasswordStorageServiceImpl implements PasswordStorageService {

    private SessionService sessionService;

    @Inject
    public PasswordStorageServiceImpl(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    @Override
    public void getServerVersion(VersionCallback cb) {
        cb.onError("Not implemented: "+sessionService.getPassword());
    }
}
