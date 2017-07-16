package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;

/**
 * Created by jeff on 7/15/17.
 */

public class SMAuthenticationInterceptor extends AuthenticationInterceptor {
    private KeyStorageService keyStorageService;

    SMAuthenticationInterceptor(SessionService sessionService, KeyStorageService keyStorageService) {
        super(sessionService);
        this.keyStorageService = keyStorageService;
    }

    @Override
    protected String getAuthHeader() {
        return super.getAuthHeader();
    }
}
