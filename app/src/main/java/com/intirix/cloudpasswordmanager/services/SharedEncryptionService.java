package com.intirix.cloudpasswordmanager.services;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.crypto.io.CipherInputStream;
import org.spongycastle.crypto.io.CipherOutputStream;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

public class SharedEncryptionService {
    public static final int AES_BLOCK_SIZE = 16;

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
     * Get a stored keypair
     * @param keyName
     * @return
     * @throws Exception
     */
    @Nullable
    public KeyPair getKeyPair(String keyName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(keyName)) {
            // Get public key
            PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
            // Get private key
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
            // Return a key pair
            return new KeyPair(publicKey, privateKey);
        }
        return null;
    }

    /**
     * Get a secret key from the keystore
     * @param keyName
     * @return
     * @throws Exception
     */
    public SecretKey getSecretKey(String keyName) throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(keyName)) {
            return (SecretKey)keyStore.getKey(keyName,null);
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.P)
    public SecretKey generateKey(String keyName, boolean invalidatedByBiometricEnrollment) throws Exception {
        Log.d(TAG,"Generating key "+keyName+" in AndroidKeyStore");
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore");
        keyStore.load(null);
        keyGenerator.init(new
                KeyGenParameterSpec.Builder(keyName,
                KeyProperties.PURPOSE_ENCRYPT |
                        KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build());
        return keyGenerator.generateKey();
    }

    /**
     * Generate NIST P-256 EC Key pair for signing and verification
     * @param keyName
     * @param invalidatedByBiometricEnrollment
     * @return
     * @throws Exception
     */
    @TargetApi(Build.VERSION_CODES.P)
    public KeyPair generateKeyPair(String keyName, boolean invalidatedByBiometricEnrollment) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512)
                // Require the user to authenticate with a biometric to authorize every use of the key
                .setUserAuthenticationRequired(true)
                // Generated keys will be invalidated if the biometric templates are added more to user device
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);

        keyPairGenerator.initialize(builder.build());

        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Delete a key
     * @param keyName
     * @throws Exception
     */
    public void deleteKey(String keyName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(keyName)) {
            Log.d(TAG,"Deleting key "+keyName);
            keyStore.deleteEntry(keyName);
        } else {
            Log.d(TAG,"Skipped deleting unknown key "+keyName);
        }
    }

    public boolean doesKeyExist(String keyName) {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (keyStore.containsAlias(keyName)) {
                Log.d(TAG,"Keystore contains "+keyName);
                return true;
            } else {
                Log.d(TAG,"Keystore does not contain "+keyName+", here is the list of valid aliases");
                Enumeration<String> e = keyStore.aliases();
                while (e.hasMoreElements()) {
                    String s = e.nextElement();
                    Log.d(TAG,"  "+s);
                }
                return false;
            }
        } catch (Exception e) {
            Log.w(TAG,"Failed to load keystore",e);
            return false;
        }
    }

    /**
     * Get the signature for a stored keypair
     * @param keyName
     * @return
     * @throws Exception
     */
    public Signature getSignatureForKey(String keyName) throws Exception {
        KeyPair keyPair = getKeyPair(keyName);

        if (keyPair != null) {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(keyPair.getPrivate());
            return signature;
        }
        return null;
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

    public byte[] decodeBase64(byte[] input) throws IOException {
        return Base64.decode(input,Base64.NO_WRAP);
    }

    public String encodeBase64(byte[] input) throws IOException {
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    public String encodeHex(byte[] input) {
        return Hex.toHexString(input);
    }

    /**
     * Encrypt a stream of data
     * @param keyBytes
     * @param os
     * @return
     */
    public CipherOutputStream encryptStream(byte[] keyBytes, OutputStream os) throws IOException {
        AESFastEngine aesEngine = new AESFastEngine();
        PaddedBufferedBlockCipher cipher =
                new PaddedBufferedBlockCipher(new CBCBlockCipher(aesEngine), new PKCS7Padding());
        byte[] ivData = generateKey(AES_BLOCK_SIZE);

        KeyParameter keyParam = new KeyParameter(keyBytes);
        CipherParameters params = new ParametersWithIV(keyParam, ivData);

        cipher.init(true, params);
        os.write(ivData);
        return new CipherOutputStream(os, cipher);
    }

    /**
     * Decrypt a stream of data
     * @param keyBytes
     * @param is
     * @return
     */
    public CipherInputStream decryptStream(byte[] keyBytes, InputStream is) throws IOException {
        AESFastEngine aesEngine = new AESFastEngine();
        PaddedBufferedBlockCipher cipher =
                new PaddedBufferedBlockCipher(new CBCBlockCipher(aesEngine), new PKCS7Padding());
        byte[] iv = new byte[AES_BLOCK_SIZE];
        if (is.read(iv)!=AES_BLOCK_SIZE) {
            throw new IOException("Failed to read IV");
        }
        KeyParameter keyParam = new KeyParameter(keyBytes);
        CipherParameters params = new ParametersWithIV(keyParam, iv);

        cipher.init(false, params);
        return new CipherInputStream(is, cipher);

    }

    public byte[] encryptWithCipher(Cipher cipher, byte[] input) throws BadPaddingException, IllegalBlockSizeException {
        return cipher.doFinal(input);
    }

    public byte[] encryptWithCipher(Cipher cipher, Serializable obj) throws BadPaddingException, IllegalBlockSizeException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(obj);
        oos.close();
        return cipher.doFinal(buffer.toByteArray());
    }

    public byte[] encryptWithKey(String key, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        return encryptWithKey(getSecretKey(key),input);
    }


    public byte[] encryptWithKey(SecretKey key, byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] ivData = generateKey(AES_BLOCK_SIZE);
        IvParameterSpec ivspec = new IvParameterSpec(ivData);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        }

        return encryptWithCipherAndIV(input, ivData, cipher);

    }

    /**
     * Encrypt data with the Cipher, but also prepend the IV to the output
     * @param input
     * @param ivData
     * @param cipher
     * @return
     * @throws IOException
     */
    private byte[] encryptWithCipherAndIV(byte[] input, byte[] ivData, Cipher cipher) throws IOException {
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

    /**
     * Encrypt data with the raw AES256 key
     * @param keyBytes
     * @param input
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws IOException
     */
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

        return encryptWithCipherAndIV(input, ivData, cipher);
    }

    public byte[] decryptAESWithKey(String key, byte[] input) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        return decryptAESWithKey(getSecretKey(key),input);
    }


    /**
     * Decrypt data with an AES256 SecretKey object
     *
     * @param key
     * @param input
     * @return
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public byte[] decryptAESWithKey(SecretKey key, byte[] input) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] ivData = Arrays.copyOf(input, AES_BLOCK_SIZE);
        byte[] encrypted = Arrays.copyOfRange(input, AES_BLOCK_SIZE, input.length);

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
