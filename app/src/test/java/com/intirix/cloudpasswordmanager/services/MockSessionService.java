package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;

/**
 * Created by jeff on 6/19/16.
 */
public class MockSessionService implements SessionService {
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
