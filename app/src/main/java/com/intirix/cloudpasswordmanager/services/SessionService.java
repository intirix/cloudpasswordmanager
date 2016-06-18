package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 6/18/16.
 */
public interface SessionService {

    /**
     * End the session
     */
    public void end();

    /**
     * Set the url of the session
     * @param url
     */
    public void setUrl(String url);

    /**
     * Get the url of the session
     * @return
     */
    public String getUrl();

    /**
     * Set the username of the session
     * @param username
     */
    public void setUsername(String username);

    /**
     * Get the username of the session
     * @return
     */
    public String getUsername();

    /**
     * Get the password of the session
     * @param password
     */
    public void setPassword(String password);

    /**
     * Get the password for the session
     * @return
     */
    public String getPassword();
}
