package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 7/29/16.
 */
public interface AutoLogoffService {

    /**
     * Notify the service that a user-initiated event has occurred
     */
    public void notifyUserEvent();

    /**
     * Check to see if the session is still valid
     * @return true if the session is still valid
     */
    public boolean isSessionStillValid();
}
