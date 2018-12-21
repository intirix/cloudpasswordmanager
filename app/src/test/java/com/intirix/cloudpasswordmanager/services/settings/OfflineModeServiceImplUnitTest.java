package com.intirix.cloudpasswordmanager.services.settings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.intirix.cloudpasswordmanager.services.SharedEncryptionService;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.ui.MockEventService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class OfflineModeServiceImplUnitTest {
    OfflineModeServiceImpl obj;
    SharedPreferences preferences;
    MockSessionService sessionService;
    MockEventService eventService;

    @Before
    public void setUp() {
        preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sessionService = new MockSessionService();
        sessionService.setUsername("username");
        sessionService.start();
        sessionService.getCurrentSession().setPassword("password");
        SharedEncryptionService encryptionService = new SharedEncryptionService();
        obj = new OfflineModeServiceImpl(RuntimeEnvironment.application,sessionService, encryptionService, eventService);
    }

    @Test
    public void verifyDefaultIsDisabled() {
        Assert.assertFalse(obj.isOfflineModelEnabled());
    }

    @Test
    public void verifyTrue() {
        preferences.edit().putBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,true).commit();
        Assert.assertTrue(obj.isOfflineModelEnabled());
    }

    @Test
    public void verifyFalse() {
        preferences.edit().putBoolean(OfflineModeServiceImpl.PREF_OFFLINE_MODE_SETTING,false).commit();
        Assert.assertFalse(obj.isOfflineModelEnabled());
    }


    @Test
    public void verifyTrueMethod() {
        obj.enable();
        Assert.assertTrue(obj.isOfflineModelEnabled());
    }

    @Test
    public void verifyFalseMethod() {
        obj.disable();
        Assert.assertFalse(obj.isOfflineModelEnabled());
    }

    @Test
    public void testSave() {
        PasswordBean bean1 = new PasswordBean();
        bean1.setWebsite("test.com");
        List<PasswordBean> list = new ArrayList<>();
        list.add(bean1);
        sessionService.getCurrentSession().setPasswordBeanList(list);


        obj.updateOfflineModeCache(true);
    }

    @Test
    public void verifyEnableWillCreateCacheFile() {
        Assert.assertFalse(obj.doesCacheFileExist("passwords"));
        obj.enable();
        Robolectric.flushBackgroundThreadScheduler();
        Assert.assertTrue(obj.doesCacheFileExist("passwords"));
    }

    @Test
    public void verifyDisableWillDeleteCacheFile() {
        Assert.assertFalse(obj.doesCacheFileExist("passwords"));
        obj.enable();
        Robolectric.flushBackgroundThreadScheduler();
        Assert.assertTrue(obj.doesCacheFileExist("passwords"));
        obj.disable();
        Robolectric.flushBackgroundThreadScheduler();
        Assert.assertFalse(obj.doesCacheFileExist("passwords"));
    }

}
