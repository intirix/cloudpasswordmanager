package com.intirix.cloudpasswordmanager.services.beans;

/**
 * Created by jeff on 6/27/16.
 */
public class Category {

    private String id;

    private String user_id;

    private String category_name;

    private String category_colour;

    public String getCategory_colour() {
        return category_colour;
    }

    public void setCategory_colour(String category_colour) {
        this.category_colour = category_colour;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

        Category category = (Category) o;

        if (getId() != null ? !getId().equals(category.getId()) : category.getId() != null)
            return false;
        if (getUser_id() != null ? !getUser_id().equals(category.getUser_id()) : category.getUser_id() != null)
            return false;
        if (getCategory_name() != null ? !getCategory_name().equals(category.getCategory_name()) : category.getCategory_name() != null)
            return false;
        return getCategory_colour() != null ? getCategory_colour().equals(category.getCategory_colour()) : category.getCategory_colour() == null;

    }

    /**
     * Generated
     * @return
     */
    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUser_id() != null ? getUser_id().hashCode() : 0);
        result = 31 * result + (getCategory_name() != null ? getCategory_name().hashCode() : 0);
        result = 31 * result + (getCategory_colour() != null ? getCategory_colour().hashCode() : 0);
        return result;
    }
}
