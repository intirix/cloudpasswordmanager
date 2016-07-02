package com.intirix.cloudpasswordmanager.services.beans;

import java.util.List;

/**
 * Created by jeff on 6/29/16.
 */
public class SessionInfo {

    private String password;

    private List<PasswordInfo> passwordList;

    private List<Category> categoryList;

    private String serverVersion;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setPasswordList(List<PasswordInfo> list) {
        this.passwordList = list;
    }

    public List<PasswordInfo> getPasswordList() {
        return passwordList;
    }

    public void setCategoryList(List<Category> list) {
        categoryList = list;
    }

    public void setPasswordServerAppVersion(String version) {
        serverVersion = version;
    }

    public String getPasswordServerAppVersion() {
        return serverVersion;
    }
}
