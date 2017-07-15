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
package com.intirix.cloudpasswordmanager.services.session;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by jeff on 6/20/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SessionServiceImplUnitSpec {

    public static final String TESTURL = "https://www.example.com/owncloud";
    public static final String TESTUSER = "testuser";
    public static final String TESTPASS = "testpass";

    private static final StorageType DEFAULT_STORAGE_TYPE = StorageType.OWNCLOUD_PASSWORDS;
    private static final StorageType NON_DEFAULT_STORAGE_TYPE = StorageType.SECRETS_MANAGER_API_V1;

    private SessionServiceImpl impl;


    @Test
    public void verifyNewInstallHasNoValues() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);
        Assert.assertEquals(DEFAULT_STORAGE_TYPE,impl.getStorageType());
        Assert.assertNull(impl.getUrl());
        Assert.assertNull(impl.getUsername());
    }

    @Test
    public void verifyExistingInstallHasNoValues() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putString(SessionServiceImpl.STORAGE_TYPE_KEY, NON_DEFAULT_STORAGE_TYPE.name()).commit();
        sharedPreferences.edit().putString(SessionServiceImpl.URL_KEY, TESTURL).commit();
        sharedPreferences.edit().putString(SessionServiceImpl.USERNAME_KEY, TESTUSER).commit();


        impl = new SessionServiceImpl(RuntimeEnvironment.application);
        Assert.assertEquals(NON_DEFAULT_STORAGE_TYPE, impl.getStorageType());
        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());
    }

    @Test
    public void verifyStartSessionSavesUrlAndUsername() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);

        impl.setStorageType(NON_DEFAULT_STORAGE_TYPE);
        impl.setUrl(TESTURL);
        impl.setUsername(TESTUSER);
        impl.start();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);

        Assert.assertEquals(NON_DEFAULT_STORAGE_TYPE.name(), sharedPreferences.getString(SessionServiceImpl.STORAGE_TYPE_KEY, null));
        Assert.assertEquals(TESTURL, sharedPreferences.getString(SessionServiceImpl.URL_KEY, null));
        Assert.assertEquals(TESTUSER, sharedPreferences.getString(SessionServiceImpl.USERNAME_KEY, null));

        Assert.assertEquals(NON_DEFAULT_STORAGE_TYPE, impl.getStorageType());
        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());
    }

    @Test
    public void verifyStartDoesNotClearFields() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);

        impl.setStorageType(NON_DEFAULT_STORAGE_TYPE);
        impl.setUrl(TESTURL);
        impl.setUsername(TESTUSER);
        impl.start();


        Assert.assertEquals(NON_DEFAULT_STORAGE_TYPE, impl.getStorageType());
        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());

    }

    @Test
    public void verifyEndClearsOnlyPasswordField() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);

        impl.setStorageType(NON_DEFAULT_STORAGE_TYPE);
        impl.setUrl(TESTURL);
        impl.setUsername(TESTUSER);

        impl.start();

        impl.getCurrentSession().setPassword(TESTPASS);

        impl.end();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);

        Assert.assertEquals(NON_DEFAULT_STORAGE_TYPE.name(),sharedPreferences.getString(SessionServiceImpl.STORAGE_TYPE_KEY, null));
        Assert.assertEquals(TESTURL, sharedPreferences.getString(SessionServiceImpl.URL_KEY, null));
        Assert.assertEquals(TESTUSER, sharedPreferences.getString(SessionServiceImpl.USERNAME_KEY, null));

        Assert.assertEquals(NON_DEFAULT_STORAGE_TYPE, impl.getStorageType());
        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());
        Assert.assertNull(impl.getCurrentSession());

    }

}
