package com.intirix.cloudpasswordmanager.pages.passwordlist;

import android.content.Intent;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.LoginActivity;
import com.intirix.cloudpasswordmanager.services.SessionService;
import com.intirix.cloudpasswordmanager.services.beans.Category;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;

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

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
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
        Assert.assertNull(activity.sessionService.getCurrentSession());
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
        Assert.assertNull(sessionService.getCurrentSession().getPasswordList());
        Assert.assertNull(sessionService.getCurrentSession().getCategoryList());

        // the progress dialog should be showing
        Assert.assertNotNull("ProgressDialog should exist now", activity.progressDialog);
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());


        // simulate the password list request finishing
        // categories should still be null
        sessionService.getCurrentSession().setPasswordList(new ArrayList<PasswordInfo>());
        activity.onPasswordsUpdated(null);
        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordList());
        Assert.assertNull(sessionService.getCurrentSession().getCategoryList());


        // the progress dialog should be showing
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());



        // simulate the category list request finishing
        sessionService.getCurrentSession().setCategoryList(new ArrayList<Category>());
        activity.onCategoriesUpdated(null);
        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordList());
        Assert.assertNotNull(sessionService.getCurrentSession().getCategoryList());



        Assert.assertFalse("ProgressDialog should not be visisble", activity.progressDialog.isShowing());



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
        activity.adapter.notifyDataSetChanged();
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
        activity.adapter.notifyDataSetChanged();
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
