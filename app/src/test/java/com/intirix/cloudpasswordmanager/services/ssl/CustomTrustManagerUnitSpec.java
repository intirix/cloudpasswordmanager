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
package com.intirix.cloudpasswordmanager.services.ssl;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.ssl.CustomTrustManager;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by jeff on 8/9/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class CustomTrustManagerUnitSpec {

    private CustomTrustManager impl;

    private X509TrustManager defaultTrustManager;

    private X509TrustManager pinnedTrustManager;

    private static final String AUTH_TYPE = "AUTH_TYPE";

    private X509Certificate[] certs;

    @Before
    public void setUp() throws KeyStoreException, NoSuchAlgorithmException {
        defaultTrustManager = EasyMock.createMock(X509TrustManager.class);
        impl = new CustomTrustManager() {
            @Override
            protected X509TrustManager getDefaultTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
                return defaultTrustManager;
            }
        };
    }

    @Test
    public void verifyDefaultTrustManager() throws KeyStoreException, NoSuchAlgorithmException {
        Assert.assertNotNull(new CustomTrustManager().getDefaultTrustManager());
    }

    @Test
    public void verifyNotPinnedPassesThroughToDefault() throws CertificateException {
        defaultTrustManager.checkServerTrusted(certs, AUTH_TYPE);
        EasyMock.expectLastCall();
        EasyMock.replay(defaultTrustManager);

        impl.checkServerTrusted(certs, AUTH_TYPE);
        EasyMock.verify(defaultTrustManager);
    }

    @Test
    public void verifyNotPinnedInvalidCertThrowsException() throws CertificateException {
        defaultTrustManager.checkServerTrusted(certs, AUTH_TYPE);
        CertificateException certificateException = new CertificateException("Invalid cert");
        EasyMock.expectLastCall().andThrow(certificateException);
        EasyMock.replay(defaultTrustManager);

        try {
            impl.checkServerTrusted(certs, AUTH_TYPE);
            Assert.fail("The server was trusted when it shouldn't have been");
        } catch (CertificateException e) {
            Assert.assertEquals(certificateException, e);
            EasyMock.verify(defaultTrustManager);
        }
    }

    @Test
    public void verifyOnlyPinnedManagerGetsCalled() throws CertificateException {
        pinnedTrustManager = EasyMock.createMock(X509TrustManager.class);
        impl.setPinnedTrustManager(pinnedTrustManager);
        pinnedTrustManager.checkServerTrusted(certs, AUTH_TYPE);
        EasyMock.expectLastCall();
        EasyMock.replay(defaultTrustManager, pinnedTrustManager);

        impl.checkServerTrusted(certs, AUTH_TYPE);
        EasyMock.verify(defaultTrustManager, pinnedTrustManager);
    }


    @Test
    public void verifyWrongPinnedCertCertThrowsException() throws CertificateException {
        pinnedTrustManager = EasyMock.createMock(X509TrustManager.class);
        impl.setPinnedTrustManager(pinnedTrustManager);
        pinnedTrustManager.checkServerTrusted(certs, AUTH_TYPE);
        CertificateException certificateException = new CertificateException("Invalid cert");
        EasyMock.expectLastCall().andThrow(certificateException);
        EasyMock.replay(defaultTrustManager, pinnedTrustManager);

        try {
            impl.checkServerTrusted(certs, AUTH_TYPE);
            Assert.fail("The server was trusted when it shouldn't have been");
        } catch (CertificateException e) {
            Assert.assertEquals(certificateException, e);
            EasyMock.verify(defaultTrustManager, pinnedTrustManager);
        }
    }

}
