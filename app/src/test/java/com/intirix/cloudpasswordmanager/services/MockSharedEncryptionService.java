package com.intirix.cloudpasswordmanager.services;

import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MockSharedEncryptionService extends SharedEncryptionService {
    private final Map<String,SecretKey> secretKeyStore = new HashMap<>();

    @Override
    public SecretKey getSecretKey(String keyName) throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        return secretKeyStore.get(keyName);
    }

    @Override
    public SecretKey generateKey(String keyName, boolean invalidatedByBiometricEnrollment) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, secureRandom);
        SecretKey key = keyGenerator.generateKey();

        secretKeyStore.put(keyName,key);
        return key;
    }

    @Override
    public void deleteKey(String keyName) throws Exception {
        secretKeyStore.remove(keyName);
    }

    @Override
    public boolean doesKeyExist(String keyName) {
        return secretKeyStore.containsKey(keyName);
    }
}
