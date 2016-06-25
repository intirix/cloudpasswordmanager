package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 6/19/16.
 */
public class MockSessionService implements SessionService {
    private String url;

    private String username;

    private String password;

    private boolean started = false;

    private boolean ended = false;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void end() {
        ended = true;
        password = null;
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
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isStarted() {
        return started;
    }
}
