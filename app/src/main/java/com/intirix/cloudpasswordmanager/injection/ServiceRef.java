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
package com.intirix.cloudpasswordmanager.injection;

import com.intirix.cloudpasswordmanager.pages.about.AboutActivity;
import com.intirix.cloudpasswordmanager.pages.BaseActivity;
import com.intirix.cloudpasswordmanager.pages.SecureActivity;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.pages.settings.SettingsFragment;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jeff on 6/18/16.
 */
@Component(modules = CloudPasswordManagerModule.class)
@Singleton
public interface ServiceRef {
//    PasswordStorageService storageService();

    SessionService sessionService();

    void inject(LoginActivity activity);
    void inject(BaseActivity activity);
    void inject(SecureActivity activity);
    void inject(PasswordListActivity activity);
    void inject(PasswordDetailActivity activity);
    void inject(AboutActivity activity);
    void inject(SettingsFragment fragment);
}
