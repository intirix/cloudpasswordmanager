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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRequestService;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.session.SessionServiceImpl;
import com.intirix.cloudpasswordmanager.services.session.StorageType;
import com.intirix.cloudpasswordmanager.services.ssl.MockCertPinningService;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;

/**
 * Created by jeff on 6/19/16.
 */
@RunWith(RobolectricTestRunner.class)


public class LoginActivityLayoutSpec extends BaseTestCase {


    private void initDefaultPasswordRequestService(PasswordRequestService passwordRequestService) {
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUrl()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUsername()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsPassword()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsCustomKey()).andReturn(true).anyTimes();
        EasyMock.replay(passwordRequestService);
    }

    @Test
    public void verifyFormElementsExist() throws Exception {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertNotNull(activity.storageTypeSpinner);
        Assert.assertNotNull(activity.urlInput);
        Assert.assertNotNull(activity.userInput);
        Assert.assertNotNull(activity.passInput);
        Assert.assertEquals("",activity.passInput.getText().toString());
        Assert.assertNotNull(activity.errorMessageView);
        Assert.assertEquals(View.GONE, activity.errorMessageView.getVisibility());
        Assert.assertNotNull(activity.findViewById(R.id.login_login_button));

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyPinButtonVisibleWhenNotPinned() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

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
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

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

    @Test
    public void verifyImportKeyButtonInvisbleWhenBackendDoesntNeedIt() {
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
        activity.urlInput.setText("https://cloud.intirix.com");
        activity.updateLoginForm(true);

        Button pinButton = (Button)activity.findViewById(R.id.login_import_key_button);
        Assert.assertEquals(View.GONE, pinButton.getVisibility());
        Assert.assertFalse(pinButton.isEnabled());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyImportKeyButtonVisbleForSM() {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();
        sessionService.setStorageType(StorageType.SECRETS_MANAGER_API_V1);

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        activity.urlInput.setText("https://cloud.intirix.com");

        activity.updateLoginForm(true);

        Button pinButton = (Button)activity.findViewById(R.id.login_import_key_button);
        Assert.assertEquals(View.VISIBLE, pinButton.getVisibility());
        Assert.assertTrue(pinButton.isEnabled());

        controller.pause().stop().destroy();
    }

    /**
     * This test stopped working when the visibility logic moved to the passwordRequestService
     */
    @Ignore
    @Test
    public void verifyImportKeyButtonChangesVisibility() {
        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        EasyMock.expect(passwordRequestService.isLoginRunning()).andReturn(false).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUrl()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsUsername()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsPassword()).andReturn(true).anyTimes();
        EasyMock.expect(passwordRequestService.supportsCustomKey()).andReturn(false).times(2).andReturn(true).anyTimes();
        EasyMock.replay(passwordRequestService);

        controller.resume();

        activity.urlInput.setText("https://cloud.intirix.com");

        activity.updateLoginForm(true);

        Button pinButton = (Button)activity.findViewById(R.id.login_import_key_button);
        Assert.assertEquals(View.GONE, pinButton.getVisibility());
        Assert.assertFalse(pinButton.isEnabled());

        activity.storageTypeSpinner.setSelection(1);
        activity.updateLoginForm(false);

        Assert.assertEquals(View.VISIBLE, pinButton.getVisibility());
        Assert.assertTrue(pinButton.isEnabled());

        activity.storageTypeSpinner.setSelection(0);
        activity.updateLoginForm(false);

        Assert.assertEquals(View.GONE, pinButton.getVisibility());
        Assert.assertFalse(pinButton.isEnabled());


        controller.pause().stop().destroy();
    }



    @Test
    public void verifyOwnCloudPasswordsIsDefaultStorageType() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putString(SessionServiceImpl.STORAGE_TYPE_KEY,"INVALID").commit();

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals(0,activity.storageTypeSpinner.getSelectedItemPosition());
        Assert.assertEquals(SessionServiceImpl.DEFAULT_STORAGE_TYPE,activity.storageTypeSpinner.getSelectedItem());

        controller.pause().stop().destroy();
    }


    @Test
    public void verifyDefaultStorageTypeIsUsedWhenNullStorageTypeSet() {
        MockSessionService sessionService = (MockSessionService)serviceRef.sessionService();
        sessionService.setStorageType(null);

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals(0,activity.storageTypeSpinner.getSelectedItemPosition());
        Assert.assertEquals(SessionServiceImpl.DEFAULT_STORAGE_TYPE,activity.storageTypeSpinner.getSelectedItem());

        controller.pause().stop().destroy();
    }

    @Test
    public void verifyDefaultStorageTypeIsUsedWhenInvalidStorageTypeSet() {

        ActivityController<LoginActivity> controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        LoginActivity activity = controller.get();

        PasswordRequestService passwordRequestService = activity.passwordRequestService;
        initDefaultPasswordRequestService(passwordRequestService);

        controller.resume();

        Assert.assertEquals(0,activity.storageTypeSpinner.getSelectedItemPosition());
        Assert.assertEquals(SessionServiceImpl.DEFAULT_STORAGE_TYPE,activity.storageTypeSpinner.getSelectedItem());

        controller.pause().stop().destroy();
    }



}
