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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.services.biometric.MockBiometricService;
import com.intirix.cloudpasswordmanager.services.settings.OfflineModeServiceImpl;
import com.intirix.cloudpasswordmanager.services.settings.SavePasswordEnum;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class SettingsActivityLayoutSpec extends BaseTestCase {

    SharedPreferences preferences;

    @Before
    public void setUp() {
        preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
    }

    @Test
    public void verifyBaseLayout() throws Exception {
        SessionService sessionService = serviceRef.sessionService();


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();


        Assert.assertEquals("Settings", activity.getTitle().toString());
        Assert.assertNotNull(activity.findViewById(R.id.settings_savepass_label));

        controller.pause().stop().destroy();
    }

    @Test
    public void verifySavePasswordSelectionWhenAlways() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.get();

        activity.savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);

        controller.start().resume();

        final TextView tv = (TextView)activity.findViewById(R.id.settings_savepass_value);
        Assert.assertEquals(activity.getString(R.string.settings_savepass_always_label), tv.getText().toString());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyOfflineModeOffByDefault() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.get();

        //activity.savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);

        controller.start().resume();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_offline_checkbox);
        Assert.assertFalse(cb.isChecked());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyOfflineModeCheckedWhenEnabled() {
        SessionService sessionService = serviceRef.sessionService();
        preferences.edit().putBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,true).commit();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.get();

        //activity.savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);

        controller.start().resume();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_offline_checkbox);
        Assert.assertTrue(cb.isChecked());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyBiometricSettingsNotVisibleForOlderOSVersions() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.start().get();

        MockBiometricService biometricService = new MockBiometricService();
        activity.fragment.biometricService = biometricService;

        //activity.savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);

        controller.resume();

        Assert.assertEquals(View.GONE,activity.findViewById(R.id.settings_biometric_row).getVisibility());

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyBiometricSettingsVisibleForP() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.start().get();

        MockBiometricService biometricService = new MockBiometricService();
        activity.fragment.biometricService = biometricService;
        biometricService.setAvailable(true);

        //activity.savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);

        controller.resume();

        Assert.assertEquals(View.VISIBLE,activity.findViewById(R.id.settings_biometric_row).getVisibility());

        controller.pause().stop().destroy();
    }



    @Test
    public void verifyBiometricCheckedWhenAlreadyEnrolled() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.start().get();

        MockBiometricService biometricService = new MockBiometricService();
        activity.fragment.biometricService = biometricService;

        biometricService.setAvailable(true);
        biometricService.setEnrolled(true);

        controller.resume();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_biometric_checkbox);
        Assert.assertTrue(cb.isChecked());


        controller.pause().stop().destroy();
    }

    @Test
    public void verifyBiometricNotCheckedWhenNotEnrolled() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.start().get();

        MockBiometricService biometricService = new MockBiometricService();
        activity.fragment.biometricService = biometricService;

        biometricService.setAvailable(true);
        biometricService.setEnrolled(false);

        controller.resume();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_biometric_checkbox);
        Assert.assertFalse(cb.isChecked());


        controller.pause().stop().destroy();
    }


    /**
     * There was a bug related to navigating to the settings page
     * When you load the page, the code checks the fingerprint checkbox,
     * but that action kicks off the enroll code as if the user checked it
     */
    @Test
    public void verifyBiometricDoesntReenrollWhenSettingsLoaded() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.start().get();

        MockBiometricService biometricService = new MockBiometricService();
        activity.fragment.biometricService = biometricService;

        biometricService.setAvailable(true);
        biometricService.setEnrolled(true);

        controller.resume();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_biometric_checkbox);
        Assert.assertTrue(cb.isChecked());

        Assert.assertFalse(biometricService.wasEnrollCalled());
        Assert.assertFalse(biometricService.wasUnenrollCalled());

        controller.pause().stop().destroy();
    }

    /**
     * There was a bug related to navigating to the settings page
     * When you load the page, the code checks the fingerprint checkbox,
     * but that action kicks off the enroll code as if the user checked it
     */
    @Test
    public void verifyBiometricDoesntUnenrollWhenSettingsLoaded() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create();
        SettingsActivity activity = controller.start().get();

        MockBiometricService biometricService = new MockBiometricService();
        activity.fragment.biometricService = biometricService;

        biometricService.setAvailable(true);
        biometricService.setEnrolled(false);

        controller.resume();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_biometric_checkbox);
        Assert.assertFalse(cb.isChecked());

        Assert.assertFalse(biometricService.wasEnrollCalled());
        Assert.assertFalse(biometricService.wasUnenrollCalled());


        controller.pause().stop().destroy();
    }


}
