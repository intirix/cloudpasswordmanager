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

import android.content.Context;
import android.view.View;

import com.intirix.cloudpasswordmanager.services.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.SavePasswordService;

/**
 * Created by jeff on 10/22/16.
 */
public abstract class SavePasswordOption {

    protected SavePasswordService savePasswordService;

    private SavePasswordEnum option;

    protected String label;

    protected String description;

    protected SavePasswordOption(SavePasswordService savePasswordService, SavePasswordEnum option) {
        this.savePasswordService = savePasswordService;
        this.option = option;
    }

    /**
     * Is the option valid for this device
     * @param ctx
     * @return
     */
    public abstract boolean isValid(Context ctx);

    public boolean isCurrentlySelected() {
        return option.equals(savePasswordService.getCurrentSetting());
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public abstract void onClick(View v);
}
