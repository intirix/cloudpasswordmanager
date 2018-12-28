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

import com.intirix.cloudpasswordmanager.pages.passwordadd.PasswordAddActivity;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

/**
 * Created by jeff on 6/29/16.
 */
public interface BackendRequestInterface {

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
     * Is a crud request still running
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

}
