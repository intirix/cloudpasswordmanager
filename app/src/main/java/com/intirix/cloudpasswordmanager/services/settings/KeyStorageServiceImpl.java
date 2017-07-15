package com.intirix.cloudpasswordmanager.services.settings;

import android.content.Context;

import com.intirix.cloudpasswordmanager.services.session.SessionService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

/**
 * Created by jeff on 7/14/17.
 */

public class KeyStorageServiceImpl implements KeyStorageService {

    private Context context;

    private SessionService sessionService;

    @Inject
    public KeyStorageServiceImpl(Context context, SessionService sessionService) {
        this.context = context;
        this.sessionService = sessionService;
    }


        @Override
    public boolean isPrivateKeyStored() {
        return false;
    }

    @Override
    public void saveEncryptedPrivateKey(String key) throws IOException, UnsupportedEncodingException {

    }

    @Override
    public String getEncryptedPrivateKey() throws IOException, UnsupportedEncodingException {
        return null;
    }
}
