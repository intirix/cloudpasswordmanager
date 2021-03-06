/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.injection;

import android.content.Context;

import com.intirix.cloudpasswordmanager.services.ssl.CertPinningService;
import com.intirix.cloudpasswordmanager.services.ssl.CertPinningServiceImpl;
import com.intirix.cloudpasswordmanager.services.ssl.MockCertPinningService;
import com.intirix.cloudpasswordmanager.services.backend.ocp.MockPasswordStorageService;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestServiceImpl;
import com.intirix.cloudpasswordmanager.services.backend.ocp.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.backend.ocp.PasswordStorageServiceImpl;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.session.SessionServiceImpl;

import org.easymock.EasyMock;

/**
 * Created by jeff on 6/19/16.
 */
public class MockCloudPasswordManagerModule extends CloudPasswordManagerModule {

    public MockCloudPasswordManagerModule(Context context) {
        super(context);
    }

    @Override
    PasswordRequestService providePasswordRequestService(PasswordRequestServiceImpl impl) {
        return EasyMock.createMock(PasswordRequestService.class);
    }

    @Override
    SessionService provideSessionService(SessionServiceImpl impl) {
        return new MockSessionService();
    }

    @Override
    PasswordStorageService providePasswordStorageService(PasswordStorageServiceImpl impl) {
        return new MockPasswordStorageService();
    }

    @Override
    CertPinningService provideCertPinningService(CertPinningServiceImpl impl) {
        return new MockCertPinningService();
    }
}
