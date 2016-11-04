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
package com.intirix.cloudpasswordmanager.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by jeff on 10/23/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SavePasswordServiceImplUnitTest {

    private SharedPreferences deviceSpecific;

    @Before
    public void setUp() {
        deviceSpecific = RuntimeEnvironment.application.getSharedPreferences("device.xml", Context.MODE_PRIVATE);

    }

    @Test
    public void testFirstRun() {
        SavePasswordServiceImpl impl = new SavePasswordServiceImpl(RuntimeEnvironment.application, null);
        Assert.assertEquals(SavePasswordEnum.NEVER, impl.currentSetting);
    }

    @Test
    public void testNeverSelected() {
        deviceSpecific.edit().putString(SavePasswordServiceImpl.PREF_SAVE_PASSWORD_SETTING, SavePasswordEnum.NEVER.toString());

        SavePasswordServiceImpl impl = new SavePasswordServiceImpl(RuntimeEnvironment.application, null);
        Assert.assertEquals(SavePasswordEnum.NEVER, impl.currentSetting);
    }

    @Test
    public void testAlwaysSelected() {
        deviceSpecific.edit().putString(SavePasswordServiceImpl.PREF_SAVE_PASSWORD_SETTING, SavePasswordEnum.ALWAYS.toString()).commit();

        SavePasswordServiceImpl impl = new SavePasswordServiceImpl(RuntimeEnvironment.application, null);
        Assert.assertEquals(SavePasswordEnum.ALWAYS, impl.currentSetting);
    }

    @Test
    public void testUnknownSelected() {
        deviceSpecific.edit().putString(SavePasswordServiceImpl.PREF_SAVE_PASSWORD_SETTING, "UNKNOWN").commit();

        SavePasswordServiceImpl impl = new SavePasswordServiceImpl(RuntimeEnvironment.application, null);
        Assert.assertEquals(SavePasswordEnum.NEVER, impl.currentSetting);
    }

    @Test
    public void testEncryption() {
        SavePasswordServiceImpl impl = new SavePasswordServiceImpl(RuntimeEnvironment.application, null);
        String PASSWORD = "random_password";
        Assert.assertEquals(PASSWORD, impl.decryptPassword(impl.encryptPassword(PASSWORD)));

    }

}