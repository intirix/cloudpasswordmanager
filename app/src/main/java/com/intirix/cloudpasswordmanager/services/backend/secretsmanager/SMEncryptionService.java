package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.Base64;


import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;

import java.io.IOException;
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
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jeff on 7/15/17.
 */

public class SMEncryptionService {
    private static final int AES_BLOCK_SIZE = 16;

    private KeyFactory rsaKeyFactory;

    public SMEncryptionService() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        rsaKeyFactory = KeyFactory.getInstance("RSA");

        // verify that the algorithms are available up front
        Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        Signature.getInstance("SHA256withRSA");

    }


    public byte[] keyExtend(String user, String password) {
        Charset ch = Charset.forName("ASCII");
        return SCrypt.generate(password.getBytes(ch),user.getBytes(ch),16384,8,1,32);
    }

    public byte[] decodeBase64(String input) throws IOException {
        return Base64.decode(input.getBytes("ASCII"),Base64.NO_WRAP);
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

}
