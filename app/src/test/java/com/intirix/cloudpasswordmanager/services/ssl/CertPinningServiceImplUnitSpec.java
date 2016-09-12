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
import com.intirix.cloudpasswordmanager.services.ui.MockEventService;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Created by jeff on 8/9/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class CertPinningServiceImplUnitSpec {

    private CertPinningServiceImpl impl;

    private MockEventService eventService;

    private CustomTrustManager customTrustManager;

    private CustomHostnameVerifier customHostnameVerifier;

    private MockSavingTrustManager savingTrustManager;

    private X509TrustManager keystoreTrustManager;

    private boolean hostnameMatches;

    private static X509Certificate randomCert;

    @BeforeClass
    public static void createRandomCert() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        KeyPair pair = generateRSAKeyPair();
        randomCert = generateV3Certificate(pair);

    }

    @Before
    public void setUp() throws KeyStoreException, NoSuchAlgorithmException {
        savingTrustManager = new MockSavingTrustManager(null);
        eventService = new MockEventService();
        customTrustManager = new CustomTrustManager();
        customHostnameVerifier = new CustomHostnameVerifier(OkHostnameVerifier.INSTANCE);
        hostnameMatches = true;
        keystoreTrustManager = EasyMock.createMock(X509TrustManager.class);
        impl = new CertPinningServiceImpl(RuntimeEnvironment.application, customTrustManager, customHostnameVerifier, eventService) {
            @Override
            protected SavingTrustManager downloadCert(String url) {
                return savingTrustManager;
            }

            @Override
            protected boolean doesCertMatchHost(String host, X509Certificate cert) {
                return hostnameMatches;
            }
/*
            @Override
            protected X509TrustManager createKeystore(X509Certificate cert) {
                return keystoreTrustManager;
            }
            */
        };
    }

    @Test
    public void verifyPinningDisabledOnFirstStart() {
        impl.init();
        Assert.assertFalse(impl.isEnabled());
    }

    @Test
    public void verifyPinValid() {
        savingTrustManager.setChain(new X509Certificate[]{randomCert});
        savingTrustManager.setValid(true);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());
    }

    @Test
    public void verifyPinValidAfterReload() {
        savingTrustManager.setChain(new X509Certificate[]{randomCert});
        savingTrustManager.setValid(true);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());

        // reload
        impl.init();
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());

    }

    @Test
    public void verifyPinInvalid() {
        savingTrustManager.setChain(new X509Certificate[]{randomCert});
        savingTrustManager.setValid(false);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertFalse(impl.isValid());
        Assert.assertFalse("Hostname verifier should be disabled", customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());
    }

    @Test
    public void verifyPinInvalidAfterReload() {
        savingTrustManager.setChain(new X509Certificate[]{randomCert});
        savingTrustManager.setValid(false);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertFalse(impl.isValid());
        Assert.assertFalse(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());

        impl.init();
        Assert.assertTrue(impl.isEnabled());
        Assert.assertFalse(impl.isValid());
        Assert.assertFalse(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());
    }

    @Test
    public void verifyDisable() {
        savingTrustManager.setChain(new X509Certificate[]{randomCert});
        savingTrustManager.setValid(true);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());


        impl.disable();
        Assert.assertFalse(impl.isEnabled());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNull("Disabling pinning should remove the pinned trust manager", customTrustManager.getPinnedTrustManager());

    }


    @Test
    public void verifyDisabledAfterReload() {
        savingTrustManager.setChain(new X509Certificate[]{randomCert});
        savingTrustManager.setValid(true);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinSuccessfulEvent.class);
        Assert.assertTrue(impl.isEnabled());
        Assert.assertTrue(impl.isValid());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNotNull(customTrustManager.getPinnedTrustManager());


        impl.disable();
        Assert.assertFalse(impl.isEnabled());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNull("Disabling pinning should remove the pinned trust manager", customTrustManager.getPinnedTrustManager());

        impl.init();
        Assert.assertFalse(impl.isEnabled());
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertNull("Disabling pinning should remove the pinned trust manager", customTrustManager.getPinnedTrustManager());
    }

    @Test
    public void verifyFailedPin() {
        savingTrustManager.setChain(null); // this should force an NPE
        savingTrustManager.setValid(true);

        impl.init();
        Assert.assertFalse(impl.isPinRequestRunning());
        impl.pin("https://www.google.com");
        Assert.assertTrue(impl.isPinRequestRunning());
        Robolectric.flushForegroundThreadScheduler();
        Assert.assertFalse(impl.isPinRequestRunning());
        eventService.assertEventType(0, PinFailedEvent.class);
        Assert.assertTrue(customHostnameVerifier.isEnabled());
        Assert.assertEquals("An unknown error has occurred", eventService.getEvent(0, PinFailedEvent.class).getMessage());

    }






    public static X509Certificate generateV3Certificate(KeyPair pair) throws InvalidKeyException,
            NoSuchProviderException, SignatureException {

        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=Test Certificate"));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        certGen.setSubjectDN(new X500Principal("CN=Test Certificate"));
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
                | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(
                KeyPurposeId.id_kp_serverAuth));

        certGen.addExtension(X509Extensions.SubjectAlternativeName, false, new GeneralNames(
                new GeneralName(GeneralName.rfc822Name, "test@test.test")));

        return certGen.generateX509Certificate(pair.getPrivate(), "BC");
    }

    private static KeyPair generateRSAKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(1024, new SecureRandom());
        return kpGen.generateKeyPair();
    }

}
