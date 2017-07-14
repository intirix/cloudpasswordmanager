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
package com.intirix.cloudpasswordmanager.services.backend.ocp;

import com.intirix.cloudpasswordmanager.services.backend.ocp.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.backend.ocp.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.backend.ocp.callbacks.VersionCallback;

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
