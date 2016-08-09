package com.intirix.cloudpasswordmanager.services;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * Created by jeff on 8/9/16.
 */
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
