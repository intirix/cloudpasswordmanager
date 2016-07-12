package com.intirix.cloudpasswordmanager.pages;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.events.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.events.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.MockSessionService;
import com.intirix.cloudpasswordmanager.services.PasswordRequestService;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class LoginActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyFailedLogin() throws Exception {
        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        passwordRequestService.login();
        EasyMock.expectLastCall();
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).andReturn(true).andReturn(false);
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        final String MOCK_ERROR = "myerror";

        // and the form is filled out with incorrect information
        activity.urlInput.setText(MOCK_URL);
        activity.userInput.setText(MOCK_USER);
        activity.passInput.setText(MOCK_PASS);
        Assert.assertNull("ProgressDialog shouldn't be created yet", activity.progressDialog);

        // when the user hits the login button
        Button button = (Button)activity.findViewById(R.id.login_login_button);
        button.performClick();

        // then
        // the progress dialog is showing
        Assert.assertNotNull("ProgressDialog should exist now", activity.progressDialog);
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());
        // we have to simulate a back press to determine if it is cancellable
        activity.progressDialog.onBackPressed();
        Assert.assertTrue("ProgressDialog should not be cancellable", activity.progressDialog.isShowing());

        // The form elements are saved in the sessionService
        Assert.assertEquals(MOCK_URL, activity.session.getUrl());
        Assert.assertEquals(MOCK_USER, activity.session.getUsername());

        // the sessionService was started, but hasn't ended yet
        MockSessionService sessionService = (MockSessionService)activity.session;
        Assert.assertTrue(sessionService.isStarted());
        Assert.assertFalse(sessionService.isEnded());

        // notify the activity of the error
        activity.onFatalError(new FatalErrorEvent(MOCK_ERROR));
        Assert.assertFalse("ProgressDialog shouldn't be visible", activity.progressDialog.isShowing());
        Assert.assertEquals(MOCK_ERROR, activity.errorMessageView.getText().toString());
        // verify that the error message is visible
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());
        // verify that the sessionService was ended
        Assert.assertTrue(sessionService.isEnded());

        controller.pause().stop().destroy();

        // verify that login() was called
        EasyMock.verify(passwordRequestService);
    }

    @Test
    public void verifySuccessfulLogin() throws Exception {
        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        passwordRequestService.login();
        EasyMock.expectLastCall();
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).andReturn(true).andReturn(false);
        passwordRequestService.listPasswords();
        EasyMock.expectLastCall();
        passwordRequestService.listCategories();
        EasyMock.expectLastCall();
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        final String MOCK_URL = "https://www.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        final String MOCK_ERROR = "myerror";
        final String VERSION = "19.0";

        // the user has entered correct information in the form
        activity.urlInput.setText(MOCK_URL);
        activity.userInput.setText(MOCK_USER);
        activity.passInput.setText(MOCK_PASS);
        Assert.assertNull("ProgressDialog shouldn't be created yet", activity.progressDialog);

        // when the user taps the login button
        Button button = (Button)activity.findViewById(R.id.login_login_button);
        button.performClick();

        // then
        // the progress dialog is showing
        Assert.assertNotNull("ProgressDialog should exist now", activity.progressDialog);
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());
        // we have to simulate a back press to determine if it is cancellable
        activity.progressDialog.onBackPressed();
        Assert.assertTrue("ProgressDialog should not be cancellable", activity.progressDialog.isShowing());

        // The form elements are saved in the sessionService
        Assert.assertEquals(MOCK_URL, activity.session.getUrl());
        Assert.assertEquals(MOCK_USER, activity.session.getUsername());
        Assert.assertEquals(MOCK_PASS, activity.session.getCurrentSession().getPassword());

        MockSessionService sessionService = (MockSessionService)activity.session;
        Assert.assertTrue(sessionService.isStarted());
        Assert.assertFalse(sessionService.isEnded());

        // notify the activity of result
        activity.onLogin(new LoginSuccessfulEvent());

        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        // verify that the error message is not visible
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        // verify that we are starting the PasswordListActivity
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent intent = shadowActivity.peekNextStartedActivity();
        Assert.assertNotNull(intent);
        Assert.assertEquals(PasswordListActivity.class.getName(), intent.getComponent().getClassName());
        Assert.assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);

        controller.pause().stop().destroy();

        // verify that login() was called
        EasyMock.verify(passwordRequestService);
    }

    @Test
    public void verifyProgressDialogStillDisplaysWhenRestartingActivity() {
        // given the login request is running
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(true);
        EasyMock.replay(passwordRequestService);

        // when the user restarts the page
        controller.start().resume();


        // then
        // the progress dialog is showing
        Assert.assertNotNull("ProgressDialog should exist now", activity.progressDialog);
        Assert.assertTrue("ProgressDialog should be visisble", activity.progressDialog.isShowing());

        controller.pause().stop().destroy();

    }

    @Test
    public void verifyFormIsBlankOnFirstStart() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyFormIsPrepopulatedWhenRelaunched() {
        final String TESTURL = "https://www.example.com/owncloud";
        final String TESTUSER = "testuser";

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        activity.session.setUrl(TESTURL);
        activity.session.setUsername(TESTUSER);

        controller.start().resume();

        Assert.assertEquals(TESTURL, activity.urlInput.getText().toString());
        Assert.assertEquals(TESTUSER, activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyFormDoesNotOverrideValuesWhenBackgrounded() {
        final String TESTURL1 = "https://www.example.com/owncloud";
        final String TESTURL2 = "https://www.example.com/nextcloud";
        final String TESTUSER1 = "testuser";
        final String TESTUSER2 = "testuser2";

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);


        activity.session.setUrl(TESTURL1);
        activity.session.setUsername(TESTUSER1);

        controller.start().resume();

        Assert.assertEquals(TESTURL1, activity.urlInput.getText().toString());
        Assert.assertEquals(TESTUSER1, activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());

        activity.urlInput.setText(TESTURL2);
        activity.userInput.setText(TESTUSER2);

        controller.pause().resume();

        Assert.assertEquals(TESTURL2, activity.urlInput.getText().toString());
        Assert.assertEquals(TESTUSER2, activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());

        controller.pause().stop().destroy();

        EasyMock.verify(passwordRequestService);
    }

}
