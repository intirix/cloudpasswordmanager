package com.intirix.cloudpasswordmanager.services.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

/**
 * Created by jeff on 7/15/17.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class KeyStorageServiceImplUnitSpec {
    private SharedPreferences deviceSpecific;

    private KeyStorageServiceImpl impl;

    @Before
    public void setUp() {
        deviceSpecific = RuntimeEnvironment.application.getSharedPreferences("device.xml", Context.MODE_PRIVATE);
        impl = new KeyStorageServiceImpl(RuntimeEnvironment.application, null);

    }

    @Test
    public void verifyNoKeyStoredOnInitialRun() {
        Assert.assertFalse(impl.isPrivateKeyStored());
    }

    @Test
    public void verifyKeyStorage() throws IOException {
        Assert.assertFalse(impl.isPrivateKeyStored());
        impl.saveEncryptedPrivateKey("MYKEY");
        Assert.assertTrue(impl.isPrivateKeyStored());
        Assert.assertEquals("MYKEY",impl.getEncryptedPrivateKey());
        impl.clearEncryptedPrivateKey();
        Assert.assertFalse(impl.isPrivateKeyStored());
        Assert.assertNull(impl.getEncryptedPrivateKey());
    }
}
