package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

public interface BackendRequestShareInterface {

    /**
     * Share a password with a user
     * @param bean
     * @param user
     */
    public void sharePassword(PasswordBean bean, String user);

    /**
     * Stop sharing a password with a user
     * @param bean
     * @param user
     */
    public void unsharePassword(PasswordBean bean, String user);
}
