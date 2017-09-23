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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

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
    public void verifyRandomKey() {
        Assert.assertEquals(32, impl.generateKey(32).length);
    }

    @Test
    public void verifyEncryptionDecryption() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        byte[] privateKey = impl.decryptAES(impl.keyExtend("admin","password"),impl.decodeBase64(encryptedPrivateKey));
        String pem = new String(privateKey, "ASCII");

        byte[] data = impl.generateKey(64);
        Assert.assertArrayEquals(data,impl.decryptRSA(pem,impl.encryptRSA(publicKey, data)));
    }

    @Test
    public void testRandomHmac() throws InvalidKeyException, NoSuchAlgorithmException {
        final byte[] message = impl.generateKey(64);
        final byte[] key = impl.generateKey(32);
        String hmac = impl.generateHmac(key,message);
        System.out.println(hmac);
    }

    @Test
    public void testHmac() throws IOException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final byte[] key = impl.decodeBase64("V3L6BUZNihTykJ1AkaIVmdZm/TN9jrfkDDMMr6RNSDw=");
        final byte[] message = "The quick brown fox jumped over the lazy dog".getBytes("UTF-8");
        String hmac = impl.generateHmac(key,message);
        Assert.assertEquals("fd5f45316ffedc8eee564459ff6bacb77f9ee2c05d1e3b7615a724cb7716a071", hmac);
        Assert.assertTrue(impl.verifyHmac(key, message, "fd5f45316ffedc8eee564459ff6bacb77f9ee2c05d1e3b7615a724cb7716a071"));
    }

    @Test
    public void testConvertOldFormat() throws IOException, InvalidKeyException, InvalidKeySpecException, SignatureException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        String a ="OegyqyJf636xtzo9uvTNMcJrLWawJ7NCgM0GGN/6JdFAzhZudwPlqJqLZWmGGBZyTWw9RoGmRXv0fCQZrZ80MP8KOCbzNqomRpms6EXZ4JEjW3fgnCQM7VSbkHF2zNp/bgctxPLOpRYZKYruOOIhU1jJPKM8a7outbE82mxx5KDHIHidefaiRS+0fKuPYMb8Pd5qgVapYZfNDapsVZ7pI4SbDMhcvHOQfCBz8RfHXOhtu0GrGKfWsScmyH0M6L8cNUgMe8lIBUOeRGGfLd0vFZ3vTHLkojZAU5T9sOHHjkpcfk9T5gyB56k1MAqrzfUDDB9tamwbcLloc7QPbB6FgQ==";

        byte[] privateKey = impl.decryptAES(impl.keyExtend("admin","password"),impl.decodeBase64(encryptedPrivateKey));
        String pem = new String(privateKey, "ASCII");

        byte[] bytes = impl.decryptRSA(pem,impl.decodeBase64(a));
        System.out.println(bytes.length);

        String b = "581jeoLL2qVAQn/e/lcuQPlOQBend+Rp/AkjLRjBIvk=";
        byte[] hmac = impl.decodeBase64(b);
        byte[] both = new byte[bytes.length+hmac.length];
        System.arraycopy(bytes,0,both,0,32);
        System.arraycopy(hmac,0,both,32,32);
        System.out.println(impl.encodeBase64(impl.encryptRSA(publicKey, both)));
        System.out.println(both.length);
    }

    @Test
    public void verifySignatureWorks() throws IOException, InvalidKeyException, InvalidKeySpecException, SignatureException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
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
    public void verifyWrongPasswordFails() throws IOException, InvalidKeyException, InvalidKeySpecException, SignatureException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            byte[] privateKey = impl.decryptAES(impl.keyExtend("admin", "password2"), impl.decodeBase64(encryptedPrivateKey));
            String pem = new String(privateKey, "ASCII");
            Assert.assertFalse(pem.startsWith("-----BEGIN PRIVATE KEY-----"));
        } catch (IOException e) {
            Assert.assertEquals(BadPaddingException.class, e.getCause().getClass());
        }
    }
}
