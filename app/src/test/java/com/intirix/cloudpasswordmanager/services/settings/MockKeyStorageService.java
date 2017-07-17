package com.intirix.cloudpasswordmanager.services.settings;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;

/**
 * Created by jeff on 7/16/17.
 */

public class MockKeyStorageService implements KeyStorageService {

    private String privateKey;


    @Override
    public boolean isPrivateKeyStored() {
        return privateKey!=null;
    }

    @Override
    public void saveEncryptedPrivateKey(String key) throws IOException {
        privateKey = key;

    }

    @Override
    public String getEncryptedPrivateKey() throws IOException {
        return privateKey;
    }

    @Override
    public void clearEncryptedPrivateKey() throws IOException {
        privateKey = null;
    }

    public void loadKey(String path) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream(path),buffer);
        privateKey = buffer.toString("ASCII");

    }
}
