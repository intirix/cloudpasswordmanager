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

import android.view.View;
import android.widget.Button;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.ssl.MockCertPinningService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class LoginActivityLayoutSpec extends BaseTestCase {

    @Test
    public void verifyFormElementsExist() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();

        Assert.assertNotNull(activity.urlInput);
        Assert.assertNotNull(activity.userInput);
        Assert.assertNotNull(activity.passInput);
        Assert.assertNotNull(activity.errorMessageView);
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());
        Assert.assertNotNull(activity.findViewById(R.id.login_login_button));

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyPinButtonVisibleWhenNotPinned() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();
        activity.urlInput.setText("https://cloud.intirix.com");
        MockCertPinningService certPinningService = (MockCertPinningService)activity.certPinningService;
        certPinningService.setEnabled(false);
        activity.updateLoginForm(true);

        Button pinButton = (Button)activity.findViewById(R.id.login_pin_button);
        Assert.assertEquals(View.VISIBLE, pinButton.getVisibility());
        Assert.assertTrue(pinButton.isEnabled());

        Button unpinButton = (Button)activity.findViewById(R.id.login_unpin_button);
        Assert.assertNotEquals(View.VISIBLE, unpinButton.getVisibility());
        Assert.assertFalse(unpinButton.isEnabled());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyUnpinButtonVisibleWhenPinned() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start().resume();
        LoginActivity activity = controller.get();
        MockCertPinningService certPinningService = (MockCertPinningService)activity.certPinningService;
        certPinningService.setEnabled(true);
        activity.urlInput.setText("https://cloud.example.com");
        activity.updateLoginForm(true);

        Assert.assertFalse("Url input should be disabled", activity.urlInput.isEnabled());

        Button pinButton = (Button)activity.findViewById(R.id.login_pin_button);
        Assert.assertNotEquals(View.VISIBLE, pinButton.getVisibility());
        Assert.assertFalse(pinButton.isEnabled());

        Button unpinButton = (Button)activity.findViewById(R.id.login_unpin_button);
        Assert.assertEquals(View.VISIBLE, unpinButton.getVisibility());
        Assert.assertTrue(unpinButton.isEnabled());

        controller.pause().stop().destroy();
    }


}
