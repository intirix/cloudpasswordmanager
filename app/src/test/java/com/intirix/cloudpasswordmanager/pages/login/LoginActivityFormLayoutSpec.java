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

import com.intirix.cloudpasswordmanager.ActivityLifecycleTestUtil;
import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.session.StorageType;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class LoginActivityFormLayoutSpec extends BaseTestCase {

    private void initDefaultPasswordRequestService(PasswordRequestService passwordRequestService) {
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUrl()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUsername()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsPassword()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsCustomKey()).andReturn(true).anyTimes();
        EasyMock.replay(passwordRequestService);
    }


    @Test
    public void verifyFormIsBlankOnFirstStart() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertFalse(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyFormHasErrorMessageIfPassedIn() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(LoginActivity.PARAM_ERROR_MESSAGE, "ERROR");

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class,intent).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());
        Assert.assertEquals("ERROR", activity.errorMessageView.getText().toString());
        Assert.assertFalse(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        controller.pause().stop().destroy();
    }


    /* this test isn't working yet
    @Test
    public void verifyPassedInErrorDoesNotOverrideNewErrors() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(LoginActivity.PARAM_ERROR_MESSAGE, "ERROR");

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class,intent).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());
        Assert.assertEquals("ERROR", activity.errorMessageView.getText().toString());
        Assert.assertFalse(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        activity.errorMessageView.setText("NEW ERROR");

        controller = ActivityLifecycleTestUtil.recreateActivity(controller);
        activity = controller.get();

        Assert.assertEquals("NEW ERROR", activity.errorMessageView.getText().toString());

        controller.pause().stop().destroy();
    }
    */


    @Test
    public void verifyPinButtonIsDisabledForNonSSL() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        activity.urlInput.setText("http://cloud.example.com");
        Assert.assertTrue(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyFormIsPrepopulatedWhenRelaunched() {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        final String TESTURL = "https://www.example.com/owncloud";
        final String TESTUSER = "testuser";

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        sessionService.setUrl(TESTURL);
        sessionService.setUsername(TESTUSER);

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);


        Assert.assertSame(passwordRequestService,activity.passwordRequestService);
        controller.start().resume();

        Assert.assertEquals(TESTURL, activity.urlInput.getText().toString());
        Assert.assertEquals(TESTUSER, activity.userInput.getText().toString());
        Assert.assertEquals("", activity.passInput.getText().toString());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyFormDoesNotOverrideValuesWhenBackgrounded() {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        final String TESTURL1 = "https://www.example.com/owncloud";
        final String TESTURL2 = "https://www.example.com/nextcloud";
        final String TESTUSER1 = "testuser";
        final String TESTUSER2 = "testuser2";

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);


        sessionService.setUrl(TESTURL1);
        sessionService.setUsername(TESTUSER1);

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


    @Test
    public void verifyErrorMessageWhenUrlIsMissingProtocol() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        final String INVALID_URL = "test.example.com";

        activity.urlInput.setText(INVALID_URL);
        Assert.assertEquals("no protocol: "+INVALID_URL, activity.errorMessageView.getText().toString());
        Assert.assertEquals("Error message should be visible", View.VISIBLE, activity.errorMessageView.getVisibility());
        Assert.assertFalse(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyErrorMessageWhenUrlHasInvalidProtocol() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        final String INVALID_URL = "htt://test.example.com";

        activity.urlInput.setText(INVALID_URL);
        Assert.assertEquals("unknown protocol: htt", activity.errorMessageView.getText().toString());
        Assert.assertEquals("Error message should be visible", View.VISIBLE, activity.errorMessageView.getVisibility());
        Assert.assertFalse(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyAllFieldsGoneForDemoMode() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUrl()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUsername()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsPassword()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsCustomKey()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.resume();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        activity.storageTypeSpinner.setSelection(activity.storageTypeAdapter.getIndexOfStorageType(StorageType.DEMO));
        //activity.updateLoginForm(true);

        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());
        Assert.assertEquals(View.GONE, activity.urlInput.getVisibility());
        Assert.assertEquals(View.GONE, activity.userInput.getVisibility());
        Assert.assertEquals(View.GONE, activity.passInput.getVisibility());
        Assert.assertEquals(View.GONE, activity.pinButton.getVisibility());
        Assert.assertEquals(View.GONE, activity.importKeyButton.getVisibility());

        Assert.assertTrue(activity.loginButton.isEnabled());
        Assert.assertFalse(activity.pinButton.isEnabled());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyManageKeyButtonGoneForOCPBackend() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUrl()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUsername()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsPassword()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsCustomKey()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.resume();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        activity.storageTypeSpinner.setSelection(activity.storageTypeAdapter.getIndexOfStorageType(StorageType.OWNCLOUD_PASSWORDS));
        //activity.updateLoginForm(true);

        Assert.assertEquals(View.VISIBLE, activity.urlInput.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.userInput.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passInput.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.pinButton.getVisibility());
        Assert.assertEquals(View.GONE, activity.importKeyButton.getVisibility());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyManageKeyButtonVisibleForSMBackend() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();


        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        activity.storageTypeSpinner.setSelection(activity.storageTypeAdapter.getIndexOfStorageType(StorageType.SECRETS_MANAGER_API_V1));
        //activity.updateLoginForm(true);

        Assert.assertEquals(View.VISIBLE, activity.urlInput.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.userInput.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.passInput.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.pinButton.getVisibility());
        Assert.assertEquals(View.VISIBLE, activity.importKeyButton.getVisibility());

        controller.pause().stop().destroy();
    }


}
