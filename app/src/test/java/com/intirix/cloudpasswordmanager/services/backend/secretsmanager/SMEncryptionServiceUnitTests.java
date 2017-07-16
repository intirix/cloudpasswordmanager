package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by jeff on 7/16/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SMEncryptionServiceUnitTests {

    SMEncryptionService impl;

    String encryptedPrivateKey;

    String publicKey;

    @Before
    public void setUp() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        impl = new SMEncryptionService();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream("/mock_rsa_key.enc"),buffer);
        encryptedPrivateKey = buffer.toString("ASCII");

        buffer.reset();
        IOUtils.copy(getClass().getResourceAsStream("/mock_rsa_pub.pem"),buffer);
        publicKey = buffer.toString("ASCII");

    }

    @Test
    public void verifyKeyExtenderWorks() {
        Assert.assertEquals(32,impl.keyExtend("test","password").length);
    }

    @Test
    public void verifySignatureWorks() throws IOException, InvalidKeyException, InvalidKeySpecException, SignatureException {
        byte[] privateKey = impl.decryptAES(impl.keyExtend("admin","password"),impl.decodeBase64(encryptedPrivateKey));
        String pem = new String(privateKey, "ASCII");
        Assert.assertTrue(pem.startsWith("-----BEGIN PRIVATE KEY-----"));

        String message = "The quick brown fox jumped over the lazy dog";

        byte[] signature = impl.signRSA(pem,message.getBytes("UTF8"));
        Assert.assertNotNull(signature);
        Assert.assertEquals(256,signature.length);
        Assert.assertTrue(impl.verifySignatureRSA(publicKey,message.getBytes("UTF-8"),signature));
    }

    @Test
    public void verifyWrongPasswordFails() throws IOException, InvalidKeyException, InvalidKeySpecException, SignatureException {
        try {
            byte[] privateKey = impl.decryptAES(impl.keyExtend("admin", "password2"), impl.decodeBase64(encryptedPrivateKey));
            String pem = new String(privateKey, "ASCII");
            Assert.assertFalse(pem.startsWith("-----BEGIN PRIVATE KEY-----"));
        } catch (IOException e) {
            Assert.assertEquals(BadPaddingException.class, e.getCause().getClass());
        }
    }
}
