package com.intirix.cloudpasswordmanager.services.settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by jeff on 7/14/17.
 */

public interface KeyStorageService {

    public boolean isPrivateKeyStored();

    public void saveEncryptedPrivateKey(String key) throws IOException;

    public String getEncryptedPrivateKey() throws IOException;

    public void clearEncryptedPrivateKey() throws IOException;
}
