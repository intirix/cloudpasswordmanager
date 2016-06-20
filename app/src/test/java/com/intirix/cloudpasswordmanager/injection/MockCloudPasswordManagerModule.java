package com.intirix.cloudpasswordmanager.injection;

import com.intirix.cloudpasswordmanager.services.MockPasswordStorageService;
import com.intirix.cloudpasswordmanager.services.MockSessionService;
import com.intirix.cloudpasswordmanager.services.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.PasswordStorageServiceImpl;
import com.intirix.cloudpasswordmanager.services.SessionService;

/**
 * Created by jeff on 6/19/16.
 */
public class MockCloudPasswordManagerModule extends CloudPasswordManagerModule {

    @Override
    SessionService provideSessionService() {
        return new MockSessionService();
    }

    @Override
    PasswordStorageService providePasswordStorageService(PasswordStorageServiceImpl impl) {
        return new MockPasswordStorageService();
    }
}
