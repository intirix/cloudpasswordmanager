package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.settings.MockKeyStorageService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.NoSuchPaddingException;

/**
 * Created by jeff on 7/16/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SMAuthenticationInterceptorUnitSpec {

    private SMAuthenticationInterceptor impl;

    private MockKeyStorageService keyStorageService;

    private MockSessionService sessionService;

    @Before
    public void setUp() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        keyStorageService = new MockKeyStorageService();
        sessionService = new MockSessionService();
        impl = new SMAuthenticationInterceptor(sessionService, keyStorageService, new SMEncryptionService());
    }

    @Test
    public void verifyPassthroughWhenPrivateKeyDoesNotExist() throws IOException {
        sessionService.setUsername("admin");
        sessionService.start();
        sessionService.getCurrentSession().setPassword("password");
        Assert.assertEquals("Basic YWRtaW46cGFzc3dvcmQ=", impl.getAuthHeader());
    }

    @Test
    public void verifySignatureAuthWhenPrivateKeyExists() throws IOException {
        keyStorageService.loadKey("/mock_rsa_key.enc");
        sessionService.setUsername("admin");
        sessionService.start();
        sessionService.getCurrentSession().setPassword("password");
        Assert.assertTrue(impl.getAuthHeader().length()>512);
    }
}
