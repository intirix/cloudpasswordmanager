package com.intirix.cloudpasswordmanager.services;

/**
 * Created by jeff on 6/29/16.
 */
public interface PasswordRequestService {

    /**
     * Request a login to the password service
     */
    public void login();

    /**
     * Request the list of categories
     */
    public void listCategories();

    /**
     * Request the list of passwords
     */
    public void listPasswords();
}
