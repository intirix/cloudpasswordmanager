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
package com.intirix.cloudpasswordmanager.pages.settings;

import android.app.Activity;

import com.intirix.cloudpasswordmanager.services.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.SavePasswordService;

/**
 * Created by jeff on 11/4/16.
 */
public class SavePasswordOptionFactory {

    static SavePasswordOption createOption(SavePasswordEnum option, Activity activity, SavePasswordService savePasswordService) {
        if (SavePasswordEnum.NEVER.equals(option)) {
            return new SavePasswordOptionNever(activity, savePasswordService);
        } else if (SavePasswordEnum.ALWAYS.equals(option)) {
            return new SavePasswordOptionAlways(activity, savePasswordService);
        } else if (SavePasswordEnum.PASSWORD_PROTECTED.equals(option)) {
            return new SavePasswordOptionWhenPasswordProtected(activity, savePasswordService);
        }
        return null;
    }
}
