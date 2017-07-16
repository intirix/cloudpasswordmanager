package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.Base64;


import org.spongycastle.crypto.generators.SCrypt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

    Cipher cipher;

    public SMEncryptionService() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
    }


    public byte[] keyExtend(String user, String password) {
        Charset ch = Charset.forName("ASCII");
        return SCrypt.generate(password.getBytes(ch),user.getBytes(ch),16384,8,1,32);
    }

    public byte[] decodeBase64(String input) throws IOException {
        return Base64.decode(input.getBytes("ASCII"),Base64.NO_WRAP);
    }

    public byte[] decryptAES(byte[] keyBytes, byte[] input) throws IOException, InvalidKeyException {
        byte[] ivData = Arrays.copyOf(input, AES_BLOCK_SIZE);
        byte[] encrypted = Arrays.copyOfRange(input, AES_BLOCK_SIZE, input.length);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

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
}
