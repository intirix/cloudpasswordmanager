package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

/**
 * Created by jeff on 11/19/17.
 */

public class MockPasswordRequestService implements PasswordRequestService {

    private boolean supportSharing = false;

    private boolean supportAdding = false;

    @Override
    public void login() {

    }

    @Override
    public boolean isLoginRunning() {
        return false;
    }

    @Override
    public boolean isCrudRunning() {
        return false;
    }

    @Override
    public void addPassword(PasswordBean bean) {

    }

    @Override
    public void listCategories() {

    }

    @Override
    public void listPasswords() {

    }

    @Override
    public void listUsers() {

    }

    @Override
    public boolean backendSupportsSharingPasswords() {
        return supportSharing;
    }

    public void setSupportSharing(boolean supportSharing) {
        this.supportSharing = supportSharing;
    }

    @Override
    public boolean backendSupportsAddingPassword() {
        return supportAdding;
    }

    public void setSupportAdding(boolean supportAdding) {
        this.supportAdding = supportAdding;
    }
}
