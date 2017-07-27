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
package com.intirix.cloudpasswordmanager.services.backend.beans;

import java.util.Calendar;

/**
 * Created by jeff on 6/28/16.
 */
public class PasswordBean implements Comparable<PasswordBean> {

    private String id;

    private String user_id;

    private String website;

    private String address;

    private String loginName;

    private String pass;

    private String notes;

    private boolean hasNotes;

    private int strength;

    private int length;

    private boolean hasLower;

    private boolean hasUpper;

    private boolean hasNumber;

    private boolean hasSpecial;

    private String category;

    private int categoryBackground;

    private int categoryForeground;

    private String categoryName;

    private Calendar dateChanged;

    private boolean decrypted;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Calendar getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Calendar dateChanged) {
        this.dateChanged = dateChanged;
    }

    public boolean isHasLower() {
        return hasLower;
    }

    public void setHasLower(boolean hasLower) {
        this.hasLower = hasLower;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public boolean isHasNumber() {
        return hasNumber;
    }

    public void setHasNumber(boolean hasNumber) {
        this.hasNumber = hasNumber;
    }

    public boolean isHasSpecial() {
        return hasSpecial;
    }

    public void setHasSpecial(boolean hasSpecial) {
        this.hasSpecial = hasSpecial;
    }

    public boolean isHasUpper() {
        return hasUpper;
    }

    public void setHasUpper(boolean hasUpper) {
        this.hasUpper = hasUpper;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getCategoryBackground() {
        return categoryBackground;
    }

    public void setCategoryBackground(int categoryBackground) {
        this.categoryBackground = categoryBackground;
    }

    public int getCategoryForeground() {
        return categoryForeground;
    }

    public void setCategoryForeground(int categoryForeground) {
        this.categoryForeground = categoryForeground;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isDecrypted() {
        return decrypted;
    }

    public void setDecrypted(boolean decrypted) {
        this.decrypted = decrypted;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordBean that = (PasswordBean) o;

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public int compareTo(PasswordBean another) {
        int code = 0;

        if (getWebsite()!=null && another.getWebsite()!=null) {
            code = getWebsite().compareToIgnoreCase(another.getWebsite());
        }

        if (code==0 && getAddress()!=null && another.getAddress()!=null) {
            code = getAddress().compareToIgnoreCase(another.getAddress());
        }

        if (code==0 && getLoginName()!=null && another.getLoginName()!=null) {
            code = getLoginName().compareToIgnoreCase(another.getLoginName());
        }

        return code;
    }
}
