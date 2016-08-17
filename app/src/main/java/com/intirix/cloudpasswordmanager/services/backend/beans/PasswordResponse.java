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

/**
 * Created by jeff on 6/28/16.
 */
public class PasswordResponse {

    // {"id":5,"user_id":"pwuser1","loginname":"","website":"www.github.com","address":"","pass":"password","properties":"\"loginname\" : \"joebob2\", \"address\" : \"www.github.com\/joe\", \"strength\" : \"2\", \"length\" : \"8\", \"lower\" : \"1\", \"upper\" : \"0\", \"number\" : \"0\", \"special\" : \"0\", \"category\" : \"1\", \"datechanged\" : \"2016-06-28\", \"notes\" : \"notes\"","notes":false,"creation_date":"1970-01-01","deleted":"0"}

    private String id;

    private String user_id;

    private String website;

    private String pass;

    private String properties;

    private boolean notes;

    private String deleted;

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNotes() {
        return notes;
    }

    public void setNotes(boolean notes) {
        this.notes = notes;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
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

    /**
     * Generated
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordResponse that = (PasswordResponse) o;

        if (isNotes() != that.isNotes()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getUser_id() != null ? !getUser_id().equals(that.getUser_id()) : that.getUser_id() != null)
            return false;
        if (getWebsite() != null ? !getWebsite().equals(that.getWebsite()) : that.getWebsite() != null)
            return false;
        if (getPass() != null ? !getPass().equals(that.getPass()) : that.getPass() != null)
            return false;
        if (getProperties() != null ? !getProperties().equals(that.getProperties()) : that.getProperties() != null)
            return false;
        return getDeleted() != null ? getDeleted().equals(that.getDeleted()) : that.getDeleted() == null;

    }

    /**
     * Generated
     * @return
     */
    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUser_id() != null ? getUser_id().hashCode() : 0);
        result = 31 * result + (getWebsite() != null ? getWebsite().hashCode() : 0);
        result = 31 * result + (getPass() != null ? getPass().hashCode() : 0);
        result = 31 * result + (getProperties() != null ? getProperties().hashCode() : 0);
        result = 31 * result + (isNotes() ? 1 : 0);
        result = 31 * result + (getDeleted() != null ? getDeleted().hashCode() : 0);
        return result;
    }
}
