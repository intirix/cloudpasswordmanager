/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.services.session;

import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordInfo;

import java.util.List;

/**
 * Created by jeff on 6/29/16.
 */
public class SessionInfo {

    private String password;

    private StorageType storageType;

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

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }
}
