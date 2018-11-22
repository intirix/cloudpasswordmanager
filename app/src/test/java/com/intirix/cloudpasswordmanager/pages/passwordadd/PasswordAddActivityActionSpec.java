package com.intirix.cloudpasswordmanager.pages.passwordadd;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.intirix.cloudpasswordmanager.ActivityLifecycleTestUtil;
import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.InfoEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginActivity;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordDetailActivity;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowProgressDialog;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class PasswordAddActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyUserCannotAddPasswordWithNoWebsite() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start().resume();
        PasswordAddActivity activity = controller.get();

        activity.addButton.performClick();
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());
        Assert.assertEquals(activity.getString(R.string.error_empty_url).toString(),activity.errorMessageView.getText().toString());

        /*
        // verify the error message is still there after a rotate
        controller = ActivityLifecycleTestUtil.recreateActivity(controller);
        activity = controller.get();
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());
        Assert.assertEquals(activity.getString(R.string.error_empty_url).toString(),activity.errorMessageView.getText().toString());
        */

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyProgressBarIsUpIfCrudRequestIsRunning() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start();
        PasswordAddActivity activity = controller.get();
        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isCrudRunning()).andReturn(true).anyTimes();
        EasyMock.replay(passwordRequestService);
        controller.resume();

        Assert.assertNotNull(activity.progressDialog);
        Assert.assertTrue(activity.progressDialog.isShowing());

        controller.pause().stop().destroy();

    }

    @Test
    public void verifyProgressBarIsDownIfCrudRequestIsNotRunning() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start();
        PasswordAddActivity activity = controller.get();
        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isCrudRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);
        controller.resume();

        Assert.assertTrue(activity.progressDialog==null||activity.progressDialog.isShowing()==false);

        controller.pause().stop().destroy();

    }

    @Test
    public void verifyInfoEventUpdatesProgressBar() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start();
        PasswordAddActivity activity = controller.get();
        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isCrudRunning()).andReturn(true).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.resume();

        ShadowProgressDialog progressDialog = Shadows.shadowOf(activity.progressDialog);

        Assert.assertNotNull(activity.progressDialog);
        Assert.assertTrue(activity.progressDialog.isShowing());
        Assert.assertEquals(activity.getString(R.string.password_add_progress_message),progressDialog.getMessage().toString());

        activity.onInfo(new InfoEvent("TEST"));


        Assert.assertNotNull(activity.progressDialog);
        Assert.assertTrue(activity.progressDialog.isShowing());
        Assert.assertEquals("TEST",progressDialog.getMessage().toString());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifySuccessfulAddRedirectsToPasswordList() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start();
        PasswordAddActivity activity = controller.get();
        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isCrudRunning()).andReturn(false).anyTimes();
        passwordRequestService.listCategories();
        EasyMock.expectLastCall();
        passwordRequestService.listPasswords();
        EasyMock.expectLastCall();
        EasyMock.replay(passwordRequestService);

        controller.resume();

        activity.onPasswordAdded(new PasswordAddedEvent());

        // verify that we are starting the PasswordListActivity
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull(intent);
        Assert.assertEquals(PasswordListActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);


        controller.pause().stop().destroy();

    }

        /**
         * Verify that the logff button works
         * @throws Exception
         */
    @Test
    public void verifyLogoffButton() throws Exception {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordAddActivity> controller = Robolectric.buildActivity(PasswordAddActivity.class).create().start().resume();
        PasswordAddActivity activity = controller.get();

        ShadowActivity sact = Shadows.shadowOf(activity);
        Shadows.shadowOf(activity.findViewById(R.id.my_toolbar)).dump();
        sact.clickMenuItem(R.id.menuitem_logout);


        // verify that the sessionService was cleared out
        Assert.assertNull(sessionService.getCurrentSession());
        assertLogOff(activity);


        controller.pause().stop().destroy();
    }




}
