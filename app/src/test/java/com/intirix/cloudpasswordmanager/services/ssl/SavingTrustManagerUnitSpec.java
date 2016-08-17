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
import com.intirix.cloudpasswordmanager.services.ssl.SavingTrustManager;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by jeff on 8/9/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class SavingTrustManagerUnitSpec {

    private SavingTrustManager impl;

    private X509TrustManager child;

    private X509Certificate[] chain;

    private X509Certificate cert;

    private static final String AUTH_TYPE = "AUTH_TYPE";

    @Before
    public void setUp() {
        child = EasyMock.createMock(X509TrustManager.class);
        impl = new SavingTrustManager(child);

        cert = EasyMock.createMock(X509Certificate.class);
        chain = new X509Certificate[]{cert};

    }

    @Test
    public void verifyChainIsSavedForValidCert() throws CertificateException {
        child.checkServerTrusted(chain, AUTH_TYPE);
        EasyMock.expectLastCall();
        EasyMock.replay(child);

        impl.checkServerTrusted(chain, AUTH_TYPE);
        Assert.assertTrue(impl.isValid());
        Assert.assertArrayEquals(chain, impl.getChain());
        EasyMock.verify(child);
    }

    @Test
    public void verifyChainIsSavedForInvalidCert() throws CertificateException {
        CertificateException certificateException = new CertificateException("Invalid cert");
        child.checkServerTrusted(chain, AUTH_TYPE);
        EasyMock.expectLastCall().andThrow(certificateException);
        EasyMock.replay(child);

        impl.checkServerTrusted(chain, AUTH_TYPE);
        Assert.assertArrayEquals(chain, impl.getChain());
        Assert.assertFalse(impl.isValid());
        EasyMock.verify(child);
    }

}
