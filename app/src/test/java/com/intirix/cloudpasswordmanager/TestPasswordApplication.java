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
package com.intirix.cloudpasswordmanager;

import androidx.annotation.NonNull;

import com.intirix.cloudpasswordmanager.injection.CloudPasswordManagerModule;

/**
 * Created by jeff on 6/19/16.
 */
public class TestPasswordApplication extends PasswordApplication {

    CloudPasswordManagerModule module;

    public void setModule(CloudPasswordManagerModule module) {
        this.module = module;
    }

    @NonNull
    @Override
    protected CloudPasswordManagerModule getModule() {
        if (module!=null) {
            return module;
        }
        return super.getModule();
    }
}
