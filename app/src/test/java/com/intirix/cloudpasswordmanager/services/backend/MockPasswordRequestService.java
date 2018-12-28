package com.intirix.cloudpasswordmanager.services.backend;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;

import java.util.Set;

/**
 * Created by jeff on 11/19/17.
 */

public class MockPasswordRequestService implements PasswordRequestService {

    private boolean supportSharing = false;

    private boolean supportAdding = false;

    private boolean supportsUrls = false;
    private boolean supportsUsername = false;
    private boolean supportsPassword = false;
    private boolean supportsKey = false;

    @Override
    public void login() {

    }

    @Override
    public boolean supportsUrl() {
        return supportsUrls;
    }

    @Override
    public boolean supportsUsername() {
        return supportsUsername;
    }

    @Override
    public boolean supportsPassword() {
        return supportsPassword;
    }

    @Override
    public boolean supportsCustomKey() {
        return supportsKey;
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
    public void sharePassword(PasswordBean bean, String user) {

    }

    @Override
    public void unsharePassword(PasswordBean bean, String user) {

    }

    @Override
    public void updateSharingForPassword(PasswordBean bean, Set<String> usersToAdd, Set<String> usersToRemove) {

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

    public void setSupportsUrls(boolean supportsUrls) {
        this.supportsUrls = supportsUrls;
    }

    public void setSupportsUsername(boolean supportsUsername) {
        this.supportsUsername = supportsUsername;
    }

    public void setSupportsPassword(boolean supportsPassword) {
        this.supportsPassword = supportsPassword;
    }

    public void setSupportsKey(boolean supportsKey) {
        this.supportsKey = supportsKey;
    }
}
