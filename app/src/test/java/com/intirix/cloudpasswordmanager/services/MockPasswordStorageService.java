package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

/**
 * Mock of the PasswordStorageService that gives us access to the last
 * callback that was passed in
 * Created by jeff on 6/19/16.
 */
public class MockPasswordStorageService implements PasswordStorageService {

    private VersionCallback lastVersionCallback;

    private CategoryListCallback lastCategoryListCallback;

    private PasswordListCallback lastPasswordListCallack;

    @Override
    public void getServerVersion(VersionCallback cb) {
        this.lastVersionCallback = cb;
    }

    public VersionCallback getLastVersionCallback() {
        return lastVersionCallback;
    }

    @Override
    public void listCategories(CategoryListCallback cb) {
        this.lastCategoryListCallback = cb;
    }

    public CategoryListCallback getLastCategoryListCallback() {
        return lastCategoryListCallback;
    }

    @Override
    public void listPasswords(PasswordListCallback cb) {
        this.lastPasswordListCallack = cb;
    }

    public PasswordListCallback getLastPasswordListCallack() {
        return lastPasswordListCallack;
    }
}
