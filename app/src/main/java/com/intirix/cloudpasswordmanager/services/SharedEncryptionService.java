package com.intirix.cloudpasswordmanager.services;

import android.util.Base64;
import android.util.Log;

import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.crypto.io.CipherInputStream;
import org.spongycastle.crypto.io.CipherOutputStream;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

public class SharedEncryptionService {
    private static final int AES_BLOCK_SIZE = 16;

    private static final String TAG = SharedEncryptionService.class.getSimpleName();

    private KeyFactory rsaKeyFactory;

    private SecureRandom random = new SecureRandom();

    private byte[] hmacComparisonKey = generateKey(32);

    @Inject
    public SharedEncryptionService() {


        try {
            rsaKeyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            Log.e(SharedEncryptionService.class.getSimpleName(),"Missing encryption",e);
        }
    }

    /**
     * Generate a random key
     * @param bytes
     * @return
     */
    public byte[] generateKey(int bytes) {
        byte[] key = new byte[bytes];
        random.nextBytes(key);
        return key;
    }

    public byte[] keyExtendUsingScrypt(String user, String password) {
        final long t1 = System.currentTimeMillis();
        try {
            Charset ch = Charset.forName("ASCII");
            return SCrypt.generate(password.getBytes(ch), user.getBytes(ch), 16384, 8, 1, 32);
        } finally {
            final long t2 = System.currentTimeMillis();
            final long dt = t2 - t1;
            Log.d(TAG,"Time to perform scrypt: "+dt+"ms");
        }
    }

    public byte[] decodeBase64(String input) throws IOException {
        return Base64.decode(input.getBytes("ASCII"),Base64.NO_WRAP);
    }

    public String encodeBase64(byte[] input) throws IOException {
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    /**
     * Encrypt a stream of data
     * @param keyBytes
     * @param os
     * @return
     */
    public CipherOutputStream encryptStream(byte[] keyBytes, OutputStream os) {
        AESFastEngine aesEngine = new AESFastEngine();
        PaddedBufferedBlockCipher cipher =
                new PaddedBufferedBlockCipher(aesEngine, new PKCS7Padding());
        cipher.init(true, new KeyParameter(keyBytes));
        return new CipherOutputStream(os, cipher);
    }

    /**
     * Decrypt a stream of data
     * @param keyBytes
     * @param is
     * @return
     */
    public CipherInputStream decryptStream(byte[] keyBytes, InputStream is) {
        AESFastEngine aesEngine = new AESFastEngine();
        PaddedBufferedBlockCipher cipher =
                new PaddedBufferedBlockCipher(aesEngine, new PKCS7Padding());
        cipher.init(false, new KeyParameter(keyBytes));
        return new CipherInputStream(is, cipher);

    }

    public byte[] encryptAES(byte[] keyBytes, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] ivData = generateKey(AES_BLOCK_SIZE);
        IvParameterSpec ivspec = new IvParameterSpec(ivData);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        }

        byte[] encrypted;

        try {
            encrypted = cipher.doFinal(input);
        } catch (BadPaddingException e) {
            throw new IOException(e);
        } catch (IllegalBlockSizeException e) {
            throw new IOException(e);
        }

        byte[] output = new byte[ivData.length+encrypted.length];
        System.arraycopy(ivData, 0, output, 0, ivData.length);
        System.arraycopy(encrypted, 0, output, ivData.length, encrypted.length);

        return output;
    }

    public byte[] decryptAES(byte[] keyBytes, byte[] input) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] ivData = Arrays.copyOf(input, AES_BLOCK_SIZE);
        byte[] encrypted = Arrays.copyOfRange(input, AES_BLOCK_SIZE, input.length);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        IvParameterSpec ivspec = new IvParameterSpec(ivData);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        }

        try {
            return cipher.doFinal(encrypted);
        } catch (BadPaddingException e) {
            throw new IOException(e);
        } catch (IllegalBlockSizeException e) {
            throw new IOException(e);
        }

        /*
        byte[] plainText = new byte[encrypted.length*2];

        int ptLength = 0;
        try {
            ptLength = cipher.update(encrypted, 0, encrypted.length, plainText, 0);
            ptLength += cipher.doFinal(plainText, ptLength);
        } catch (ShortBufferException e) {
            throw new IOException(e);
        } catch (BadPaddingException e) {
            throw new IOException(e);
        } catch (IllegalBlockSizeException e) {
            throw new IOException(e);
        }


        return Arrays.copyOf(plainText,ptLength);
*/
    }

    public byte[] signRSA(String keyPem,byte[] input) throws SignatureException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(getPrivateKey(keyPem));
        signature.update(input);
        return signature.sign();
    }

    public boolean verifySignatureRSA(String pubKeyPem, byte[] plainText, byte[] sig) throws IOException, InvalidKeySpecException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(getPublicKey(pubKeyPem));
        signature.update(plainText);
        return signature.verify(sig);
    }

    public byte[] decryptRSA(String keyPem, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        final Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding");


        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(keyPem));

        return cipher.doFinal(input);
/*
        byte[] plainText = new byte[input.length*2];
        int ptLength = cipher.update(input, 0, input.length, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);

        return Arrays.copyOf(plainText,ptLength);
        */
    }

    public byte[] encryptRSA(String pem, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(pem));
        return cipher.doFinal(input);
    }

    private PrivateKey getPrivateKey(String pem) throws InvalidKeySpecException, IOException {
        PemReader pr = new PemReader(new StringReader(pem));
        PemObject po = pr.readPemObject();
        if (!"PRIVATE KEY".equals(po.getType())) {
            throw new InvalidKeySpecException("Wrong key type: "+po.getType());
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(po.getContent());

        return rsaKeyFactory.generatePrivate(spec);
    }

    private PublicKey getPublicKey(String pem) throws IOException, InvalidKeySpecException {
        PemReader pr = new PemReader(new StringReader(pem));
        PemObject po = pr.readPemObject();
        if (!"PUBLIC KEY".equals(po.getType())) {
            throw new InvalidKeySpecException("Wrong key type: "+po.getType());
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(po.getContent());

        return rsaKeyFactory.generatePublic(spec);
    }

    public String generateHmac(byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hmac = sha256_HMAC.doFinal(message);
        return Hex.toHexString(hmac);
    }

    /**
     * Securely verify that the HMAC is correct
     * @param key
     * @param message
     * @param mac
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public boolean verifyHmac(byte[] key, byte[] message, String mac) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String mac2 = generateHmac(key, message);

        String mac1b = generateHmac(hmacComparisonKey, mac.getBytes("UTF-8"));
        String mac2b = generateHmac(hmacComparisonKey, mac2.getBytes("UTF-8"));


        return mac1b.equals(mac2b);
    }

}
