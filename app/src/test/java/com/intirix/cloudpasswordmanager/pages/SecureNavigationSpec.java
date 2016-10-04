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
package com.intirix.cloudpasswordmanager.pages;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.ActivityLifecycleTestUtil;
import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.about.AboutActivity;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.pages.settings.SettingsActivity;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 10/3/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SecureNavigationSpec extends BaseTestCase {

    private ActivityController<PasswordListActivity> controller;
    private PasswordListActivity activity;
    private ListView drawerListView;
    private ShadowListView slv;

    @Before
    public void setUp() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        controller = Robolectric.buildActivity(PasswordListActivity.class).create().postCreate(null).start().resume();
        activity = controller.get();

        drawerListView = activity.getDrawerListView();
        Assert.assertNotNull(drawerListView.getAdapter());
        slv = Shadows.shadowOf(drawerListView);
        slv.populateItems();
        Shadows.shadowOf(drawerListView).dump();
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

    @Test
    public void verifyAbout() throws Exception {
        assertNavItem(R.string.about_nav_label, AboutActivity.class);
    }

    @Test
    public void verifyLogoff() throws Exception {
        assertNavItem(R.string.logout_nav_label, LoginActivity.class);
    }

    @Test
    public void verifySettings() throws Exception {
        assertNavItem(R.string.settings_nav_label, SettingsActivity.class);
    }

    private void assertNavItem(int labelId, Class<? extends Activity> expectedActivity) {
        int index = -1;
        String label = drawerListView.getResources().getString(labelId);
        for (int i = 0; i<drawerListView.getChildCount(); i++) {
            View v = drawerListView.getChildAt(i);

            if (v instanceof TextView) {
                String text = ((TextView)v).getText().toString();
                if (text.equals(label)) {
                    index = i;
                    v.performClick();
                }
            }
        }


        Assert.assertTrue("Did not find nav item "+ label, index>=0);
        slv.performItemClick(index);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull("We expected to change activity, but are not", intent);
        Assert.assertEquals(expectedActivity.getName(), intent.getComponent().getClassName());
    }

}
