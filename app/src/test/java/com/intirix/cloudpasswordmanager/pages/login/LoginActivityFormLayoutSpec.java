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
public class LoginActivityFormLayoutSpec extends BaseTestCase {

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
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();

        final String TESTURL = "https://www.example.com/owncloud";
        final String TESTUSER = "testuser";

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create();
        LoginActivity activity = controller.get();

        sessionService.setUrl(TESTURL);
        sessionService.setUsername(TESTUSER);

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
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.replay(passwordRequestService);


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
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        final String INVALID_URL = "test.example.com";

        activity.urlInput.setText(INVALID_URL);
        Assert.assertEquals("no protocol: "+INVALID_URL, activity.errorMessageView.getText().toString());
        Assert.assertEquals("Error message should be visible", View.VISIBLE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyErrorMessageWhenUrlHasInvalidProtocol() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertEquals("", activity.urlInput.getText().toString());
        Assert.assertEquals("", activity.errorMessageView.getText().toString());
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());

        final String INVALID_URL = "htt://test.example.com";

        activity.urlInput.setText(INVALID_URL);
        Assert.assertEquals("unknown protocol: htt", activity.errorMessageView.getText().toString());
        Assert.assertEquals("Error message should be visible", View.VISIBLE, activity.errorMessageView.getVisibility());

        controller.pause().stop().destroy();
    }


}
