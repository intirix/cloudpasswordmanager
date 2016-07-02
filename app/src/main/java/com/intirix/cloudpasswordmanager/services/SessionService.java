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
