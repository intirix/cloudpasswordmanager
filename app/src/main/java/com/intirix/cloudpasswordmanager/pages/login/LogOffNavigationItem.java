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
package com.intirix.cloudpasswordmanager.pages.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.navigation.NavigationItem;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

/**
 * Created by jeff on 7/31/16.
 */
public class LogOffNavigationItem extends NavigationItem {
    private SessionService sessionService;

    public LogOffNavigationItem(Context context, SessionService sessionService) {
        super(context);
        this.sessionService = sessionService;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.logout_nav_label);
    }

    @Override
    public void onClick(Activity activity) {
        sessionService.end();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }
}
