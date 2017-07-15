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
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.intirix.cloudpasswordmanager.services.settings.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.settings.SavePasswordService;

/**
 * Created by jeff on 10/22/16.
 */
public abstract class SavePasswordOption {

    protected SavePasswordService savePasswordService;

    protected SavePasswordEnum option;

    protected Activity activity;

    protected String label;

    protected String description;

    protected SavePasswordOption(Activity activity, SavePasswordService savePasswordService, SavePasswordEnum option) {
        this.activity = activity;
        this.savePasswordService = savePasswordService;
        this.option = option;
    }

    /**
     * Is the option currently available
     * @param ctx
     * @return
     */
    public abstract boolean isAvailable(Context ctx);

    public boolean isCurrentlySelected() {
        return option.equals(savePasswordService.getCurrentSetting());
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public void onClick(View v) {
        if (isAvailable(activity)) {
            savePasswordService.changeSavePasswordSetting(option);

            Intent intent = new Intent(activity, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        }
    }
}
