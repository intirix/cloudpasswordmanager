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

/**
 * Created by jeff on 6/29/16.
 */
public interface BackendRequestInterface {

    /**
     * Request a login to the password service
     */
    public void login();

    /**
     * Is the login request still running
     * @return
     */
    public boolean isLoginRunning();

    /**
     * Request the list of categories
     */
    public void listCategories();

    /**
     * Request the list of passwords
     */
    public void listPasswords();
}
