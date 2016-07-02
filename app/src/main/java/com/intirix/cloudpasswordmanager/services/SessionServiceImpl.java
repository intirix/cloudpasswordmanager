package com.intirix.cloudpasswordmanager.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;

import javax.inject.Inject;

/**
 * Created by jeff on 6/18/16.
 */
public class SessionServiceImpl implements SessionService {

    public static final String URL_KEY = "last.url";
    public static final String USERNAME_KEY = "last.user";

    private Context context;

    SharedPreferences preferences;

    private String url;

    private String username;

    private SessionInfo currentSession;

    @Inject
    public SessionServiceImpl(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        setUrl(preferences.getString(URL_KEY, null));
        setUsername(preferences.getString(USERNAME_KEY, null));
    }

    @Override
    public void start() {
        preferences.edit().putString(URL_KEY,url).putString(USERNAME_KEY,username).commit();
        currentSession = new SessionInfo();
    }

    @Override
    public void end() {
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
}
