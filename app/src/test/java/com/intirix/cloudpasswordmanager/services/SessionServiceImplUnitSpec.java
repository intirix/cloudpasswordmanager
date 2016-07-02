package com.intirix.cloudpasswordmanager.services;

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
        application = TestPasswordApplication.class)
public class SessionServiceImplUnitSpec {

    public static final String TESTURL = "https://www.example.com/owncloud";
    public static final String TESTUSER = "testuser";
    public static final String TESTPASS = "testpass";

    private SessionServiceImpl impl;


    @Test
    public void verifyNewInstallHasNoValues() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);
        Assert.assertNull(impl.getUrl());
        Assert.assertNull(impl.getUsername());
    }

    @Test
    public void verifyExistingInstallHasNoValues() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putString(SessionServiceImpl.URL_KEY, TESTURL).commit();
        sharedPreferences.edit().putString(SessionServiceImpl.USERNAME_KEY, TESTUSER).commit();


        impl = new SessionServiceImpl(RuntimeEnvironment.application);
        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());
    }

    @Test
    public void verifyStartSessionSavesUrlAndUsername() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);

        impl.setUrl(TESTURL);
        impl.setUsername(TESTUSER);
        impl.start();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);

        Assert.assertEquals(TESTURL, sharedPreferences.getString(SessionServiceImpl.URL_KEY, null));
        Assert.assertEquals(TESTUSER, sharedPreferences.getString(SessionServiceImpl.USERNAME_KEY, null));

        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());
    }

    @Test
    public void verifyStartDoesNotClearFields() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);

        impl.setUrl(TESTURL);
        impl.setUsername(TESTUSER);
        impl.start();


        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());

    }

    @Test
    public void verifyEndClearsOnlyPasswordField() {
        impl = new SessionServiceImpl(RuntimeEnvironment.application);

        impl.setUrl(TESTURL);
        impl.setUsername(TESTUSER);

        impl.start();

        impl.getCurrentSession().setPassword(TESTPASS);

        impl.end();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);

        Assert.assertEquals(TESTURL, sharedPreferences.getString(SessionServiceImpl.URL_KEY, null));
        Assert.assertEquals(TESTUSER, sharedPreferences.getString(SessionServiceImpl.USERNAME_KEY, null));

        Assert.assertEquals(TESTURL, impl.getUrl());
        Assert.assertEquals(TESTUSER, impl.getUsername());
        Assert.assertNull(impl.getCurrentSession());

    }

}
