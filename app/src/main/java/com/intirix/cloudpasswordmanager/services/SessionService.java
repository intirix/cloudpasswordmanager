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
package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;

/**
 * Created by jeff on 6/18/16.
 */
public interface SessionService {

    /**
     * Start a session
     */
    public void start();

    /**
     * End the session
     */
    public void end();

    /**
     * Set the url of the last started session
     * @param url
     */
    public void setUrl(String url);

    /**
     * Get the url of the last started session
     * @return
     */
    public String getUrl();

    /**
     * Set the username of the last started session
     * @param username
     */
    public void setUsername(String username);

    /**
     * Get the username of the last started session
     * @return
     */
    public String getUsername();

    /**
     * Get the current session
     * @return
     */
    public SessionInfo getCurrentSession();


}
