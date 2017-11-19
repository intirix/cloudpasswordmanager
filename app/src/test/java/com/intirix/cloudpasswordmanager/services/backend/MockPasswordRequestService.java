package com.intirix.cloudpasswordmanager.services.backend;

/**
 * Created by jeff on 11/19/17.
 */

public class MockPasswordRequestService implements PasswordRequestService {

    private boolean supportSharing = false;

    @Override
    public void login() {

    }

    @Override
    public boolean isLoginRunning() {
        return false;
    }

    @Override
    public void listCategories() {

    }

    @Override
    public void listPasswords() {

    }

    @Override
    public boolean backendSupportsSharingPasswords() {
        return supportSharing;
    }

    public void setSupportSharing(boolean supportSharing) {
        this.supportSharing = supportSharing;
    }
}
