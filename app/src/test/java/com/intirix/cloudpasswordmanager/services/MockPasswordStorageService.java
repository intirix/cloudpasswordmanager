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
