package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.services.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

/**
 * Created by jeff on 6/18/16.
 */
public interface PasswordStorageService {

    /**
     * Get the version of the Password service
     * @param cb
     */
    void getServerVersion(VersionCallback cb);

    /**
     * List all the categories that a user has
     * @param cb
     */
    void listCategories(CategoryListCallback cb);

    /**
     * List all the passwords that a user has
     * @param cb
     */
    void listPasswords(PasswordListCallback cb);
}
