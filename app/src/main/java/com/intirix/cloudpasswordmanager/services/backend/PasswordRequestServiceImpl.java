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
package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.services.backend.ocp.OCPBackendRequestImpl;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import javax.inject.Inject;

/**
 * Adapter for the PasswordStorageService that uses an EventBus to notify the UI
 * Created by jeff on 6/29/16.
 */
public class PasswordRequestServiceImpl implements PasswordRequestService {

    private static final String TAG = PasswordRequestServiceImpl.class.getSimpleName();

    private OCPBackendRequestImpl ocpImpl;

    private SessionService sessionService;

    @Inject
    public PasswordRequestServiceImpl(SessionService sessionService, OCPBackendRequestImpl ocpImpl) {
        this.sessionService = sessionService;
        this.ocpImpl = ocpImpl;
    }

    @Override
    public void login() {

        final SessionInfo session = sessionService.getCurrentSession();
        ocpImpl.login();
    }

    @Override
    public boolean isLoginRunning() {
        return ocpImpl.isLoginRunning();
    }

    @Override
    public void listCategories() {
        final SessionInfo session = sessionService.getCurrentSession();
        ocpImpl.listCategories();
    }

    @Override
    public void listPasswords() {
        final SessionInfo session = sessionService.getCurrentSession();
        ocpImpl.listPasswords();
    }
}
