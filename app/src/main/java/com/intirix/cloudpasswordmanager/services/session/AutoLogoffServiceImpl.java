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

import javax.inject.Inject;

/**
 * Created by jeff on 7/29/16.
 */
public class AutoLogoffServiceImpl implements AutoLogoffService {

    private SessionService sessionService;

    public static final long TIMEOUT = 5 * 60 * 1000;

    @Inject
    public AutoLogoffServiceImpl(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean isSessionStillValid() {
        SessionInfo session = sessionService.getCurrentSession();
        if (session==null) {
            return false;
        }

        long expire = session.getLastUserEvent()+TIMEOUT;
        if (expire>getCurrentTime()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void notifyUserEvent() {
        if (isSessionStillValid()) {
            SessionInfo session = sessionService.getCurrentSession();
            session.setLastUserEvent(getCurrentTime());

        }
    }

    /**
     * Get the current time
     * @return
     */
    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
