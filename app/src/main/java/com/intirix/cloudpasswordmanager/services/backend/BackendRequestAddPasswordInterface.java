package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

public interface BackendRequestAddPasswordInterface {

    /**
     * Add a password
     * @param bean
     */
    public void addPassword(PasswordBean bean);
}
