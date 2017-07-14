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
package com.intirix.cloudpasswordmanager.pages.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListActivity;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.session.StorageType;

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
        application = TestPasswordApplication.class, sdk = 23)
public class LoginActivityActionSpec extends BaseTestCase {

    @Test
    public void verifyFailedLogin() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

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
        Assert.assertEquals(MOCK_URL, sessionService.getUrl());
        Assert.assertEquals(MOCK_USER, sessionService.getUsername());

        // the sessionService was started, but hasn't ended yet
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
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

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
        Assert.assertEquals(MOCK_URL, sessionService.getUrl());
        Assert.assertEquals(MOCK_USER, sessionService.getUsername());
        Assert.assertEquals(MOCK_PASS, sessionService.getCurrentSession().getPassword());
        Assert.assertEquals(StorageType.OWNCLOUD_PASSWORDS, sessionService.getCurrentSession().getStorageType());

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
    public void verifyNoCrashWhenUrlIsEmpty() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        final String MOCK_URL = "";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        final String MOCK_ERROR = "myerror";

        // and the form is filled out with incorrect information
        activity.urlInput.setText(MOCK_URL);
        activity.userInput.setText(MOCK_USER);
        activity.passInput.setText(MOCK_PASS);

        // when the user hits the login button
        Button button = (Button)activity.findViewById(R.id.login_login_button);
        button.performClick();

        // then
        // the progress dialog is showing
        Assert.assertTrue("ProgressDialog should not be visible", activity.progressDialog==null||!activity.progressDialog.isShowing());

        // the sessionService should not have started
        Assert.assertFalse(sessionService.isStarted());

        Assert.assertEquals("You must specify a URL", activity.errorMessageView.getText().toString());
        // verify that the error message is visible
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();

        // verify that login() was called
        EasyMock.verify(passwordRequestService);
    }

    @Test
    public void verifyNoCrashWhenUrlDoesNotStartWithHttp() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        final String MOCK_URL = "cloud.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        final String MOCK_ERROR = "myerror";

        // and the form is filled out with incorrect information
        activity.urlInput.setText(MOCK_URL);
        activity.userInput.setText(MOCK_USER);
        activity.passInput.setText(MOCK_PASS);

        // when the user hits the login button
        Button button = (Button)activity.findViewById(R.id.login_login_button);
        button.performClick();

        // then
        // the progress dialog is showing
        Assert.assertTrue("ProgressDialog should not be visible", activity.progressDialog==null||!activity.progressDialog.isShowing());

        // the sessionService should not have started
        Assert.assertFalse(sessionService.isStarted());

        Assert.assertEquals("You must specify a URL that starts with http:// or https://", activity.errorMessageView.getText().toString());
        // verify that the error message is visible
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();

        // verify that login() was called
        EasyMock.verify(passwordRequestService);
    }

    @Test
    public void verifyNoCrashWhenUrlIsInvalid() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        final String MOCK_URL = "httpcloud.example.com/owncloud";
        final String MOCK_USER = "myusername";
        final String MOCK_PASS = "mypassword";
        final String MOCK_ERROR = "myerror";

        // and the form is filled out with incorrect information
        activity.urlInput.setText(MOCK_URL);
        activity.userInput.setText(MOCK_USER);
        activity.passInput.setText(MOCK_PASS);

        // when the user hits the login button
        Button button = (Button)activity.findViewById(R.id.login_login_button);
        button.performClick();

        // then
        // the progress dialog is showing
        Assert.assertTrue("ProgressDialog should not be visible", activity.progressDialog==null||!activity.progressDialog.isShowing());

        // the sessionService should not have started
        Assert.assertFalse(sessionService.isStarted());

        Assert.assertEquals("no protocol: "+MOCK_URL, activity.errorMessageView.getText().toString());
        // verify that the error message is visible
        Assert.assertEquals(View.VISIBLE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();

        // verify that login() was called
        EasyMock.verify(passwordRequestService);
    }

    @Test
    public void verifyErrorMessageClearsOnLoginButtonClick() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        passwordRequestService.login();
        EasyMock.expectLastCall();
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).andReturn(false).andReturn(true);
        passwordRequestService.listPasswords();
        EasyMock.expectLastCall();
        passwordRequestService.listCategories();
        EasyMock.expectLastCall();
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        activity.onFatalError(new FatalErrorEvent("Random error"));

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

        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        // verify that the error message is not visible
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();

        // verify that login() was called
        //EasyMock.verify(passwordRequestService);
    }

    @Test
    public void verifyErrorMessageClearsOnPinButtonClick() throws Exception {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        // given the user is on the login page
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.start().resume();

        activity.onFatalError(new FatalErrorEvent("Random error"));

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
        Button button = (Button)activity.findViewById(R.id.login_pin_button);
        button.performClick();

        // then

        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        // verify that the error message is not visible
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();

        // verify that login() was called
        //EasyMock.verify(passwordRequestService);
    }

}
