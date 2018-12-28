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

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import java.util.Set;

/**
 * Created by jeff on 6/29/16.
 */
public interface PasswordRequestService {

    /**
     * Request a login to the password service
     */
    public void login();

    /**
     * Does the backend connect to an external url?
     * @return
     */
    public boolean supportsUrl();

    /**
     * Does the backend support having a username
     * @return
     */
    public boolean supportsUsername();

    /**
     * Does the backend support having a password
     * @return
     */
    public boolean supportsPassword();

    /**
     * Does the backend support having a custom key
     * @return
     */
    public boolean supportsCustomKey();

    /**
     * Is the login request still running
     * @return
     */
    public boolean isLoginRunning();

    /**
     * Is a create/update/delete request still running
     * @return
     */
    public boolean isCrudRunning();

    /**
     * Request the list of categories
     */
    public void listCategories();

    /**
     * Request the list of passwords
     */
    public void listPasswords();

    /**
     * List the users of the system
     */
    public void listUsers();

    /**
     * Does the backend support sharing passwords
     * @return
     */
    public boolean backendSupportsSharingPasswords();

    /**
     * Does the backend support adding a password
     * @return
     */
    public boolean backendSupportsAddingPassword();

    /**
     * Add a password
     * @param bean
     */
    public void addPassword(PasswordBean bean);

    /**
     * Share a password with a user
     * @param bean
     * @param user
     */
    public void sharePassword(PasswordBean bean, String user);

    /**
     * Stop sharing a password with a user
     * @param bean
     * @param user
     */
    public void unsharePassword(PasswordBean bean, String user);

    /**
     * Update the sharing of a password
     * @param bean
     * @param usersToAdd
     * @param usersToRemove
     */
    public void updateSharingForPassword(PasswordBean bean, Set<String> usersToAdd, Set<String> usersToRemove);
}
