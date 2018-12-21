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

import android.util.Log;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.ocp.OCPBackendRequestImpl;
import com.intirix.cloudpasswordmanager.services.backend.secretsmanager.SMBackendRequestImpl;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.session.StorageType;

import java.util.Set;

import javax.inject.Inject;

/**
 * Adapter for the PasswordStorageService that uses an EventBus to notify the UI
 * Created by jeff on 6/29/16.
 */
public class PasswordRequestServiceImpl implements PasswordRequestService {

    private static final String TAG = PasswordRequestServiceImpl.class.getSimpleName();

    private OCPBackendRequestImpl ocpImpl;

    private SMBackendRequestImpl smimpl;

    private SessionService sessionService;

    @Inject
    public PasswordRequestServiceImpl(SessionService sessionService, OCPBackendRequestImpl ocpImpl, SMBackendRequestImpl smimpl) {
        this.sessionService = sessionService;
        this.ocpImpl = ocpImpl;
        this.smimpl = smimpl;
    }

    private BackendRequestInterface getBackend() {
        if (sessionService.getStorageType()== StorageType.OWNCLOUD_PASSWORDS) {
            return ocpImpl;
        } else if (sessionService.getStorageType()==StorageType.SECRETS_MANAGER_API_V1) {
            return smimpl;
        }
        return null;
    }

    private <T> T getBackend(Class<T> klass) {
        return (T)getBackend();
    }

    /**
     * Does the backend support a particular interface
     * @param klass
     * @return
     */
    private boolean backendSupportsInterface(Class<?> klass) {
        return klass.isAssignableFrom(getBackend().getClass());
    }

    @Override
    public void login() {
        Log.d(TAG,"login()");
        getBackend().login();
    }

    @Override
    public boolean isLoginRunning() {
        if (sessionService.getCurrentSession()==null) {
            return false;
        }
        return getBackend().isLoginRunning();
    }

    @Override
    public boolean isCrudRunning() {
        return getBackend().isCrudRunning();
    }

    @Override
    public void addPassword(PasswordBean bean) {
        if (backendSupportsAddingPassword()) {
            getBackend(BackendRequestAddPasswordInterface.class).addPassword(bean);
        }
    }

    @Override
    public void sharePassword(PasswordBean bean, String user) {
        if (backendSupportsSharingPasswords()) {
            getBackend(BackendRequestShareInterface.class).sharePassword(bean,user);
        }
    }

    @Override
    public void unsharePassword(PasswordBean bean, String user) {
        if (backendSupportsSharingPasswords()) {
            getBackend(BackendRequestShareInterface.class).unsharePassword(bean,user);
        }
    }

    @Override
    public void updateSharingForPassword(PasswordBean bean, Set<String> usersToAdd, Set<String> usersToRemove) {
        if (backendSupportsSharingPasswords()) {
            // attempt to let the backend handle the request
            if (backendSupportsInterface(BackendRequestBatchShareInterface.class)) {
                getBackend(BackendRequestBatchShareInterface.class).updateSharingForPassword(bean,usersToAdd, usersToRemove);
            } else {
                // if the backend can't handle a single request, then just fire off a
                // bunch of requests at once
                BackendRequestShareInterface shareInterface = getBackend(BackendRequestShareInterface.class);
                for (final String user: usersToAdd) {
                    shareInterface.sharePassword(bean, user);
                }
                for (final String user: usersToRemove) {
                    shareInterface.unsharePassword(bean, user);
                }
            }
        }
    }

    @Override
    public void listCategories() {
        Log.d(TAG,"listCategories()");
        getBackend().listCategories();
    }

    @Override
    public void listPasswords() {
        Log.d(TAG,"listPassword()");
        getBackend().listPasswords();
    }

    @Override
    public void listUsers() {
        Log.d(TAG,"listUsers()");
        if (backendSupportsSharingPasswords()) {
            getBackend().listUsers();
        }
    }

    @Override
    public boolean backendSupportsSharingPasswords() {
        return backendSupportsInterface(BackendRequestShareInterface.class) ||
                backendSupportsInterface(BackendRequestBatchShareInterface.class);
    }

    @Override
    public boolean backendSupportsAddingPassword() {
        return backendSupportsInterface(BackendRequestAddPasswordInterface.class);
    }
}
