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

import android.support.v4.app.Fragment;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;

/**
 * Created by jeff on 10/3/16.
 */
public class SettingsActivity extends SecureActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment;
    }

    @Override
    protected Fragment createInitialFragment() {
        return new SettingsFragment();
    }
}
