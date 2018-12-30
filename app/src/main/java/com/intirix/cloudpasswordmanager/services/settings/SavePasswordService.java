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
package com.intirix.cloudpasswordmanager.services.settings;

import java.util.List;

/**
 * Created by jeff on 10/23/16.
 */
public interface SavePasswordService {

    /**
     * Get the current save password setting
     * @return
     */
    public SavePasswordEnum getCurrentSetting();

    /**
     * Is the password currently saved on the device
     * @return
     */
    public boolean isPasswordSaved();

    /**
     * Is the password currently available to be retrieved
     * @return
     */
    public boolean isPasswordAvailable();

    /**
     * Get the password
     * @return
     */
    public String getPassword();

    /**
     * List all the options available on the device
     * @return
     */
    public List<SavePasswordEnum> listAvailableOptions();

    /**
     * Change the save password setting
     * @param value
     */
    public boolean changeSavePasswordSetting(SavePasswordEnum value);
}
