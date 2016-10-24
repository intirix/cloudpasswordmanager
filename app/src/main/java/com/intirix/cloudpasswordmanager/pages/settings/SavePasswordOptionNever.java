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

import com.intirix.cloudpasswordmanager.R;

/**
 * Created by jeff on 10/22/16.
 */
public class SavePasswordOptionNever extends SavePasswordOption {

    public SavePasswordOptionNever(Context ctx) {
        label = ctx.getString(R.string.settings_savepass_never_label);
        description = ctx.getString(R.string.settings_savepass_never_descr);
    }

    @Override
    public boolean isValid(Context ctx) {
        return true;
    }
}
