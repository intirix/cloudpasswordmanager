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

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.OCPSessionData;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.PasswordInfo;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class PasswordListActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyLogoffWhenSessionIsNull() {
        SessionService sessionService = serviceRef.sessionService();

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        Assert.assertNull(sessionService.getCurrentSession());

        assertLogOff(activity);


        controller.pause().stop().destroy();

    }

    @Test
    public void verifyLogoffButton() throws Exception {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        ShadowActivity sact = Shadows.shadowOf(activity);
        sact.onCreateOptionsMenu(new RoboMenu(activity));
        Shadows.shadowOf(activity.findViewById(R.id.my_toolbar)).dump();
        sact.clickMenuItem(R.id.menuitem_logout);


        // verify that the sessionService was cleared out
        Assert.assertNull(sessionService.getCurrentSession());
        assertLogOff(activity);


        controller.pause().stop().destroy();
    }

    @Test
    public void verifyFatalErrorLogsOut() throws Exception {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        activity.onFatalError(new FatalErrorEvent("ERROR"));

        // verify that the sessionService was cleared out
        Assert.assertNull(sessionService.getCurrentSession());
        assertLogOff(activity);


        controller.pause().stop().destroy();
    }

    @Test
    public void verifyProgressDialogIsVisibleUntilBothPasswordsAndCategoriesAreLoaded() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // both should be empty right now
        Assert.assertNull(sessionService.getCurrentSession().getPasswordBeanList());
        Assert.assertNull(sessionService.getCurrentSession().getCategoryList());

        // the progress dialog should be showing
        Assert.assertNotNull("ProgressDialog should exist now", activity.progressDialog);
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());


        // simulate the password list request finishing
        // categories should still be null
        sessionService.getCurrentSession().setPasswordBeanList(Collections.<PasswordBean>emptyList());
        activity.onPasswordsUpdated(null);
        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordBeanList());
        Assert.assertNull(sessionService.getCurrentSession().getCategoryList());


        // the progress dialog should be showing
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());



        // simulate the category list request finishing
        sessionService.getCurrentSession().setCategoryList(new ArrayList<Category>());
        activity.onCategoriesUpdated(null);
        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordBeanList());
        Assert.assertNotNull(sessionService.getCurrentSession().getCategoryList());



        Assert.assertFalse("ProgressDialog should not be visible", activity.progressDialog.isShowing());



        controller.pause().stop().destroy();

    }

    @Test
    public void verifyPasswordListUpdateNotifiesRecyclerView() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();


        activity.adapter = EasyMock.createMock(PasswordListAdapter.class);
        activity.adapter.refreshFromSession();
        EasyMock.expectLastCall();
        EasyMock.replay(activity.adapter);

        activity.onPasswordsUpdated(null);

        EasyMock.verify(activity.adapter);

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyCategoryListUpdateNotifiesRecyclerView() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();


        activity.adapter = EasyMock.createMock(PasswordListAdapter.class);
        activity.adapter.refreshFromSession();
        EasyMock.expectLastCall();
        EasyMock.replay(activity.adapter);

        activity.onCategoriesUpdated(null);

        EasyMock.verify(activity.adapter);

        controller.pause().stop().destroy();

    }

    @Test
    public void verifyErrantServerResponseDoesNotCrashApp() {
        SessionService sessionService = serviceRef.sessionService();

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();


        // this should be null because we don't have a valid session
        Assert.assertNull(activity.adapter);

        // should not crash
        activity.onPasswordsUpdated(null);
        activity.onCategoriesUpdated(null);

        controller.pause().stop().destroy();

    }


    @Test
    public void verifyRowClick() {
        SessionService sessionService = serviceRef.sessionService();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";

        sessionService.setUrl(MOCK_URL);
        sessionService.setUsername(MOCK_USER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(MOCK_PASS);

        PasswordBean pass1 = new PasswordBean();
        pass1.setWebsite("www.gmail.com");
        pass1.setLoginName("myGmailUsername");
        pass1.setId("23423");

        PasswordBean pass2 = new PasswordBean();
        pass2.setId("5433");
        pass2.setWebsite("www.yahoo.com");
        pass2.setLoginName("myYahooUsername");



        List<PasswordBean> passwords = new ArrayList<>();
        passwords.add(pass1);
        passwords.add(pass2);
        sessionService.getCurrentSession().setPasswordBeanList(passwords);

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // verify that there is an entry in the list
        Assert.assertEquals(2, activity.adapter.getItemCount());

        activity.recyclerView.measure(0,0);
        activity.recyclerView.layout(0,0,100,1000);
        Shadows.shadowOf(activity.recyclerView).dump();

        final int ROW_TO_CLICK = 0;

        activity.recyclerView.getChildAt(ROW_TO_CLICK).performClick();

        ShadowActivity sact = Shadows.shadowOf(activity);
        Intent intent = sact.peekNextStartedActivity();
        Assert.assertNotNull("We should be changing activity, but we are not", intent);
        Assert.assertEquals("Changing to wrong activity", PasswordDetailActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(pass1.getId(), intent.getStringExtra(PasswordDetailActivity.KEY_PASSWORD_ID));

        controller.pause().stop().destroy();

    }


    protected void assertLogOff(PasswordListActivity activity) {
        // verify that we are starting the LoginActivity
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull("We expected to change activity, but are not", intent);
        Assert.assertEquals(LoginActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
    }

}
