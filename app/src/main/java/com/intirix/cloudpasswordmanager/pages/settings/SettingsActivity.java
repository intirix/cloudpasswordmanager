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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.intirix.cloudpasswordmanager.PasswordApplication;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.pages.passwordadd.PasswordAddedEvent;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordsLoadedEvent;
import com.intirix.cloudpasswordmanager.services.settings.OfflineModeService;
import com.intirix.cloudpasswordmanager.services.settings.SavePasswordService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

/**
 * Created by jeff on 10/3/16.
 */
public class SettingsActivity extends SecureActivity {

    @Inject
    SavePasswordService savePasswordService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PasswordApplication.getSInjector(this).inject(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment;
    }

    @Override
    protected Fragment createInitialFragment() {
        return new SettingsFragment();
    }

}
