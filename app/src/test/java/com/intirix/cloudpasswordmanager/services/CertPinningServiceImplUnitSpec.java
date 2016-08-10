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
package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by jeff on 8/9/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class CertPinningServiceImplUnitSpec {

    private CertPinningServiceImpl impl;

    private MockEventService eventService;

    private CustomTrustManager customTrustManager;

    private MockSavingTrustManager savingTrustManager;

    private X509TrustManager keystoreTrustManager;

    @Before
    public void setUp() throws KeyStoreException, NoSuchAlgorithmException {
        savingTrustManager = new MockSavingTrustManager(null);
        eventService = new MockEventService();
        customTrustManager = new CustomTrustManager();
        keystoreTrustManager = EasyMock.createMock(X509TrustManager.class);
        impl = new CertPinningServiceImpl(RuntimeEnvironment.application, customTrustManager, eventService) {
            @Override
            protected SavingTrustManager downloadCert(String url) {
                return savingTrustManager;
            }

            @Override
            protected X509TrustManager createKeystore(X509Certificate cert) {
                return keystoreTrustManager;
            }
        };
    }

    @Test
    public void verifyPinningDisabledOnFirstStart() {
        impl.init();
        Assert.assertFalse(impl.isEnabled());
    }

    @Test
    public void verifyPinValid() {
        savingTrustManager.setChain(new X509Certificate[]{null});
        savingTrustManager.setValid(true);

        impl.init();
        impl.pin("https://www.google.com");
        Robolectric.flushBackgroundThreadScheduler();
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());
    }

    @Test
    public void verifyPinInvalid() {
        savingTrustManager.setChain(new X509Certificate[]{null});
        savingTrustManager.setValid(false);

        impl.init();
        impl.pin("https://www.google.com");
        Robolectric.flushBackgroundThreadScheduler();
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertFalse(impl.isValid());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());
    }

    @Test
    public void verifyDisable() {
        savingTrustManager.setChain(new X509Certificate[]{null});
        savingTrustManager.setValid(true);

        impl.init();
        impl.pin("https://www.google.com");
        Robolectric.flushBackgroundThreadScheduler();
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());


        impl.disable();
        Assert.assertFalse(impl.isEnabled());
        Assert.assertNull("Disabling pinning should remove the pinned trust manager", customTrustManager.getPinnedTrustManager());

    }

    @Test
    public void verifyFailedPin() {
        savingTrustManager.setChain(null); // this should force an NPE
        savingTrustManager.setValid(true);

        impl.init();
        impl.pin("https://www.google.com");
        Robolectric.flushBackgroundThreadScheduler();
        eventService.assertEventType(0, PinFailedEvent.class);
        Assert.assertEquals("An unknown error has occurred", eventService.getEvent(0, PinFailedEvent.class).getMessage());

    }

}
