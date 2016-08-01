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

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.intirix.cloudpasswordmanager.injection.CloudPasswordManagerModule;
import com.intirix.cloudpasswordmanager.injection.ServiceRef;

/**
 * Created by jeff on 6/19/16.
 */
public class PasswordApplication extends Application {

    private ServiceRef serviceRef;

    @Override
    public void onCreate() {
        super.onCreate();
        initObjects();
    }

    /**
     * Initialize the objects
     */
    protected void initObjects() {
        serviceRef = com.intirix.cloudpasswordmanager.injection.DaggerServiceRef.builder().cloudPasswordManagerModule(getModule()).build();
    }

    @NonNull
    protected CloudPasswordManagerModule getModule() {
        return new CloudPasswordManagerModule(this);
    }

    /**
     * Get the injector
     * @return
     */
    public ServiceRef getInjector() {
        return serviceRef;
    }

    /**
     * Get the injector
     * @param ctx
     * @return
     */
    public static ServiceRef getSInjector(Context ctx) {
        PasswordApplication app = (PasswordApplication)ctx.getApplicationContext();
        return app.getInjector();
    }
}
