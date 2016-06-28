package com.intirix.cloudpasswordmanager.services.callbacks;

import com.intirix.cloudpasswordmanager.services.beans.Category;

import java.util.List;

/**
 * Created by jeff on 6/28/16.
 */
public interface CategoryListCallback extends BaseCallback {
    public void onReturn(List<Category> categories);
}
