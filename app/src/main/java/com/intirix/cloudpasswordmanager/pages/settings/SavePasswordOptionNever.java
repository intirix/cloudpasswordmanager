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

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.SavePasswordService;

/**
 * Created by jeff on 10/22/16.
 */
public class SavePasswordOptionNever extends SavePasswordOption {

    private Activity activity;

    public SavePasswordOptionNever(Activity activity, SavePasswordService savePasswordService) {
        super(savePasswordService, SavePasswordEnum.NEVER);
        this.activity = activity;
        label = activity.getString(R.string.settings_savepass_never_label);
        description = activity.getString(R.string.settings_savepass_never_descr);
    }

    @Override
    public boolean isValid(Context ctx) {
        return true;
    }

    @Override
    public void onClick(View v) {
        savePasswordService.changeSavePasswordSetting(SavePasswordEnum.NEVER);

        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

    }


}
