package com.intirix.cloudpasswordmanager.services.beans;

import java.util.List;

/**
 * Created by jeff on 6/29/16.
 */
public class SessionInfo {

    private String password;

    private long lastUserEvent = System.currentTimeMillis();

    private List<PasswordInfo> passwordList;

    private List<Category> categoryList;

    private List<PasswordBean> passwordBeanList;

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

    public List<PasswordBean> getPasswordBeanList() {
        return passwordBeanList;
    }

    public void setPasswordBeanList(List<PasswordBean> passwordBeanList) {
        this.passwordBeanList = passwordBeanList;
    }

    public long getLastUserEvent() {
        return lastUserEvent;
    }

    public void setLastUserEvent(long lastUserEvent) {
        this.lastUserEvent = lastUserEvent;
    }
}
