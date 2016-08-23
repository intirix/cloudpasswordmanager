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
package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.intirix.cloudpasswordmanager.ActivityLifecycleTestUtil;
import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 8/17/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordListActivitySortingSpec extends BaseTestCase {

    private static final String MOCK_URL = "https://www.example.com/owncloud";
    private static final String MOCK_USER = "myusername";
    private static final String MOCK_PASS = "mypassword";

    private SessionService sessionService;

    private PasswordBean passC = new PasswordBean();

    private PasswordBean passB = new PasswordBean();

    private PasswordBean passA = new PasswordBean();

    @Before
    public void setUp() {
        sessionService = serviceRef.sessionService();

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        passC = new PasswordBean();
        passC.setWebsite("C");
        passC.setId("543");

        passB = new PasswordBean();
        passB.setWebsite("B");
        passB.setId("123");

        passA = new PasswordBean();
        passA.setWebsite("A");
        passA.setId("3454");

    }

    @Test
    public void verfiyBasicSorting() {
        final List<PasswordBean> passwords = new ArrayList<>();


        passwords.add(passC);
        passwords.add(passB);
        passwords.add(passA);

        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertEquals(3, activity.adapter.getItemCount());
        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        assertRowWebsite("A", activity.recyclerView, 0);
        assertRowWebsite("B", activity.recyclerView, 1);
        assertRowWebsite("C", activity.recyclerView, 2);


        controller.pause().stop().destroy();
    }


    @Test
    public void verifyBasicFiltering() {
        final List<PasswordBean> passwords = new ArrayList<>();


        passwords.add(passC);
        passwords.add(passB);
        passwords.add(passA);

        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertEquals(3, activity.adapter.getItemCount());

        activity.onQueryTextSubmit("B");

        Assert.assertEquals(1, activity.adapter.getItemCount());
        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        assertRowWebsite("B", activity.recyclerView, 0);

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyBlankFilterGivesEverything() {
        final List<PasswordBean> passwords = new ArrayList<>();


        passwords.add(passC);
        passwords.add(passB);
        passwords.add(passA);

        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertEquals(3, activity.adapter.getItemCount());

        activity.onQueryTextSubmit("B");

        Assert.assertEquals(1, activity.adapter.getItemCount());

        activity.onQueryTextSubmit("");

        Assert.assertEquals(3, activity.adapter.getItemCount());

        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        assertRowWebsite("A", activity.recyclerView, 0);
        assertRowWebsite("B", activity.recyclerView, 1);
        assertRowWebsite("C", activity.recyclerView, 2);

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyFilterRemainsOnRotate() {
        final List<PasswordBean> passwords = new ArrayList<>();


        passwords.add(passC);
        passwords.add(passB);
        passwords.add(passA);

        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertEquals(3, activity.adapter.getItemCount());

        activity.onQueryTextSubmit("B");

        Assert.assertEquals(1, activity.adapter.getItemCount());

        controller = ActivityLifecycleTestUtil.recreateActivity(controller);
        activity = controller.get();

        Assert.assertEquals(1, activity.adapter.getItemCount());

        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        assertRowWebsite("B", activity.recyclerView, 0);

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyRowClick() {
        final List<PasswordBean> passwords = new ArrayList<>();


        passwords.add(passC);
        passwords.add(passB);
        passwords.add(passA);

        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertEquals(3, activity.adapter.getItemCount());

        activity.onQueryTextSubmit("C");

        Assert.assertEquals(1, activity.adapter.getItemCount());
        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        assertRowWebsite("C", activity.recyclerView, 0);

        final int ROW_TO_CLICK = 0;

        Shadows.shadowOf(activity.recyclerView.getChildAt(ROW_TO_CLICK)).dump();
        activity.recyclerView.getChildAt(ROW_TO_CLICK).performClick();

        ShadowActivity sact = Shadows.shadowOf(activity);
        Intent intent = sact.peekNextStartedActivity();
        Assert.assertNotNull("We should be changing activity, but we are not", intent);
        Assert.assertEquals("Changing to wrong activity", PasswordDetailActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(passC.getId(), intent.getStringExtra(PasswordDetailActivity.KEY_PASSWORD_ID));


        controller.pause().stop().destroy();
    }



    private void assertRowWebsite(String expectedWebsite, RecyclerView recyclerView, int index){
        TextView websiteView = (TextView)recyclerView.getChildAt(index).findViewById(R.id.password_list_row_website);
        Assert.assertEquals(expectedWebsite, websiteView.getText().toString());

    }

}
