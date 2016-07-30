package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;

import javax.inject.Inject;

/**
 * Created by jeff on 7/29/16.
 */
public class AutoLogoffSessionImpl implements AutoLogoffService {

    private SessionService sessionService;

    public static final long TIMEOUT = 5 * 60 * 1000;

    @Inject
    public AutoLogoffSessionImpl(SessionService sessionService) {
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
