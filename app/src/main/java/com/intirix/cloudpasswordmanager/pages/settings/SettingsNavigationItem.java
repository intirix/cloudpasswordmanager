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

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;

/**
 * Created by jeff on 7/28/16.
 */
public class SettingsNavigationItem extends NavigationItem {
    public SettingsNavigationItem(Context context) {
        super(context);
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.settings_nav_label);
    }

    @Override
    public void onClick(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);

    }
}
