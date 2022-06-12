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

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.R;
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
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.android.controller.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class SettingsActivityActionSpec extends BaseTestCase {

    private final String MOCK_URL = "https://www.example.com/owncloud";
    private final String MOCK_USER = "myusername";
    private final String MOCK_PASS = "mypassword";

    SharedPreferences preferences;

    @Before
    public void setUp() {
        SessionService sessionService = serviceRef.sessionService();


        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
    }

    @Test
    public void verifySavePasswordOptions() throws Exception {
        SessionService sessionService = serviceRef.sessionService();


        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();


        Assert.assertEquals("Settings", activity.getTitle().toString());
        Assert.assertNotNull(activity.findViewById(R.id.settings_savepass_label));

        activity.findViewById(R.id.settings_savepass_row).performClick();

        Assert.assertNotNull(activity.findViewById(R.id.settings_savepass_options_recycler));

        RecyclerView rv = (RecyclerView)activity.findViewById(R.id.settings_savepass_options_recycler);

        // verify that there is an entry in the list
        Assert.assertEquals(4, rv.getAdapter().getItemCount());

        rv.measure(0,0);
        rv.layout(0,0,100,1000);
        Shadows.shadowOf(rv).dump();

        // click ALWAYS
        TextView tv = (TextView)rv.getChildAt(1).findViewById(R.id.savepassword_option_row_label);
        Assert.assertEquals(activity.getString(R.string.settings_savepass_always_label), tv.getText().toString());
        rv.getChildAt(1).performClick();

        controller.pause().stop().destroy();
    }


    @Test
    public void verifySavePasswordAlways() throws Exception {



        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();

        Assert.assertEquals(SavePasswordEnum.NEVER, activity.savePasswordService.getCurrentSetting());

        // Change save password
        activity.findViewById(R.id.settings_savepass_row).performClick();

        RecyclerView rv = (RecyclerView)activity.findViewById(R.id.settings_savepass_options_recycler);

        rv.measure(0,0);
        rv.layout(0,0,100,1000);
        Shadows.shadowOf(rv).dump();

        // click ALWAYS
        TextView tv = (TextView)rv.getChildAt(1).findViewById(R.id.savepassword_option_row_label);
        Assert.assertEquals(activity.getString(R.string.settings_savepass_always_label), tv.getText().toString());
        rv.getChildAt(1).performClick();

        // the setting should be ALWAYS
        Assert.assertEquals(SavePasswordEnum.ALWAYS, activity.savePasswordService.getCurrentSetting());
        Assert.assertEquals("Password should have been saved", MOCK_PASS, activity.savePasswordService.getPassword());


        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull(intent);
        Assert.assertEquals(SettingsActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP);

        controller.pause().stop().destroy();
    }


    @Test
    public void verifySavePasswordNever() throws Exception {

        SessionService sessionService = serviceRef.sessionService();

        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();

        Assert.assertEquals(SavePasswordEnum.NEVER, activity.savePasswordService.getCurrentSetting());
        activity.savePasswordService.changeSavePasswordSetting(SavePasswordEnum.ALWAYS);
        Assert.assertEquals(SavePasswordEnum.ALWAYS, activity.savePasswordService.getCurrentSetting());

        // Change save password
        activity.findViewById(R.id.settings_savepass_row).performClick();

        RecyclerView rv = (RecyclerView)activity.findViewById(R.id.settings_savepass_options_recycler);

        rv.measure(0,0);
        rv.layout(0,0,100,1000);
        Shadows.shadowOf(rv).dump();

        // click NEVER
        TextView tv = (TextView)rv.getChildAt(0).findViewById(R.id.savepassword_option_row_label);
        Assert.assertEquals(activity.getString(R.string.settings_savepass_never_label), tv.getText().toString());
        rv.getChildAt(0).performClick();

        // the setting should be NEVER
        Assert.assertEquals(SavePasswordEnum.NEVER, activity.savePasswordService.getCurrentSetting());
        String pass = activity.savePasswordService.getPassword();
        Assert.assertTrue("Saved password should have been cleared out", pass==null||pass.length()==0);

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull(intent);
        Assert.assertEquals(SettingsActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP);

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyEnableOfflineMode() throws Exception {

        SessionService sessionService = serviceRef.sessionService();

        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_offline_checkbox);
        Assert.assertFalse(cb.isChecked());
        cb.performClick();

        Assert.assertTrue(preferences.getBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,false));

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyDisableOfflineMode() throws Exception {

        preferences.edit().putBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,true).commit();

        SessionService sessionService = serviceRef.sessionService();

        ActivityController<SettingsActivity> controller = Robolectric.buildActivity(SettingsActivity.class).create().start().resume();
        SettingsActivity activity = controller.get();

        final CheckBox cb = (CheckBox)activity.findViewById(R.id.settings_offline_checkbox);
        Assert.assertTrue(cb.isChecked());
        cb.performClick();

        Assert.assertFalse(preferences.getBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,true));

        controller.pause().stop().destroy();
    }

}
