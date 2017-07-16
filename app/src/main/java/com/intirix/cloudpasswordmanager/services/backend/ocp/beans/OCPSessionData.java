package com.intirix.cloudpasswordmanager.services.backend.ocp.beans;

import java.util.List;

/**
 * Created by jeff on 7/15/17.
 */

public class OCPSessionData {
    private List<PasswordInfo> passwords;

    public List<PasswordInfo> getPasswordList() {
        return passwords;
    }

    public void setPasswordList(List<PasswordInfo> passwords) {
        this.passwords = passwords;
    }
}
