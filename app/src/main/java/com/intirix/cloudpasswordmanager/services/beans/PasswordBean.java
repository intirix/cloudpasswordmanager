package com.intirix.cloudpasswordmanager.services.beans;

import java.util.Calendar;

/**
 * Created by jeff on 6/28/16.
 */
public class PasswordBean {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordBean that = (PasswordBean) o;

        if (isHasNotes() != that.isHasNotes()) return false;
        if (getStrength() != that.getStrength()) return false;
        if (getLength() != that.getLength()) return false;
        if (isHasLower() != that.isHasLower()) return false;
        if (isHasUpper() != that.isHasUpper()) return false;
        if (isHasNumber() != that.isHasNumber()) return false;
        if (isHasSpecial() != that.isHasSpecial()) return false;
        if (getCategoryBackground() != that.getCategoryBackground()) return false;
        if (getCategoryForeground() != that.getCategoryForeground()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getUser_id() != null ? !getUser_id().equals(that.getUser_id()) : that.getUser_id() != null)
            return false;
        if (getWebsite() != null ? !getWebsite().equals(that.getWebsite()) : that.getWebsite() != null)
            return false;
        if (getAddress() != null ? !getAddress().equals(that.getAddress()) : that.getAddress() != null)
            return false;
        if (getLoginName() != null ? !getLoginName().equals(that.getLoginName()) : that.getLoginName() != null)
            return false;
        if (getPass() != null ? !getPass().equals(that.getPass()) : that.getPass() != null)
            return false;
        if (getNotes() != null ? !getNotes().equals(that.getNotes()) : that.getNotes() != null)
            return false;
        if (getCategory() != null ? !getCategory().equals(that.getCategory()) : that.getCategory() != null)
            return false;
        if (getCategoryName() != null ? !getCategoryName().equals(that.getCategoryName()) : that.getCategoryName() != null)
            return false;
        return getDateChanged() != null ? getDateChanged().equals(that.getDateChanged()) : that.getDateChanged() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUser_id() != null ? getUser_id().hashCode() : 0);
        result = 31 * result + (getWebsite() != null ? getWebsite().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getLoginName() != null ? getLoginName().hashCode() : 0);
        result = 31 * result + (getPass() != null ? getPass().hashCode() : 0);
        result = 31 * result + (getNotes() != null ? getNotes().hashCode() : 0);
        result = 31 * result + (isHasNotes() ? 1 : 0);
        result = 31 * result + getStrength();
        result = 31 * result + getLength();
        result = 31 * result + (isHasLower() ? 1 : 0);
        result = 31 * result + (isHasUpper() ? 1 : 0);
        result = 31 * result + (isHasNumber() ? 1 : 0);
        result = 31 * result + (isHasSpecial() ? 1 : 0);
        result = 31 * result + (getCategory() != null ? getCategory().hashCode() : 0);
        result = 31 * result + getCategoryBackground();
        result = 31 * result + getCategoryForeground();
        result = 31 * result + (getCategoryName() != null ? getCategoryName().hashCode() : 0);
        result = 31 * result + (getDateChanged() != null ? getDateChanged().hashCode() : 0);
        return result;
    }
}
