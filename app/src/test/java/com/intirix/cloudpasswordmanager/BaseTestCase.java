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

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.intirix.cloudpasswordmanager.injection.CloudPasswordManagerModule;
import com.intirix.cloudpasswordmanager.injection.MockCloudPasswordManagerModule;
import com.intirix.cloudpasswordmanager.injection.ServiceRef;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;

import org.junit.Assert;
import org.junit.Before;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

/**
 * Created by jeff on 6/19/16.
 */
public class BaseTestCase {

    protected TestPasswordApplication app;

    protected ServiceRef serviceRef;

    @Before
    public void setup() {
        app = (TestPasswordApplication) RuntimeEnvironment.application;
        // Setting up the mock module
        CloudPasswordManagerModule module = createMockModule();
        app.setModule(module);
        app.initObjects();

        serviceRef = app.getInjector();
    }

    @NonNull
    protected CloudPasswordManagerModule createMockModule() {
        return new MockCloudPasswordManagerModule(RuntimeEnvironment.application);
    }

    protected void assertLogOff(Activity activity) {
        // verify that we are starting the LoginActivity
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull("We expected to change activity, but are not", intent);
        Assert.assertEquals(LoginActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
