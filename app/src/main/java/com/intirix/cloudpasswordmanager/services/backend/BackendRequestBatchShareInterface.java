package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import java.util.Set;

public interface BackendRequestBatchShareInterface {

    /**
     * Update the sharing of a password
     * @param bean
     * @param usersToAdd
     * @param usersToRemove
     */
    public void updateSharingForPassword(PasswordBean bean, Set<String> usersToAdd, Set<String> usersToRemove);

}
