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
package com.intirix.cloudpasswordmanager.services.session;

/**
 * Created by jeff on 6/19/16.
 */
public class MockSessionService implements SessionService {
    private StorageType storageType = SessionServiceImpl.DEFAULT_STORAGE_TYPE;

    private String url;

    private String username;

    private SessionInfo currentSession;

    private String serverVersion;

    private boolean started = false;

    private boolean ended = false;

    @Override
    public void start() {
        started = true;
        currentSession = new SessionInfo();
        currentSession.setUsername(username);
    }

    @Override
    public void end() {
        ended = true;
        currentSession = null;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public void setStorageType(StorageType storageType) {
        if (storageType!=null) {
            this.storageType = storageType;
        }
    }

    @Override
    public SessionInfo getCurrentSession() {
        return currentSession;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isStarted() {
        return started;
    }
}
