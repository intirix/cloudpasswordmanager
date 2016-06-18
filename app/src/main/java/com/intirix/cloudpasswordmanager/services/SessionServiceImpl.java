package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 6/18/16.
 */
public class SessionServiceImpl implements SessionService {
    private static String url;

    private static String username;

    private static String password;


    @Override
    public void end() {
        password = null;
    }

    @Override
    public void setUrl(String url) {
        SessionServiceImpl.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUsername(String username) {
        SessionServiceImpl.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setPassword(String password) {
        SessionServiceImpl.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
