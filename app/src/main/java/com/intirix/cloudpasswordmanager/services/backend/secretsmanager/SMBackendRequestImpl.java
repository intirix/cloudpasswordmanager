package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestInterface;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;
import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jeff on 7/14/17.
 */

public class SMBackendRequestImpl implements BackendRequestInterface {

    private static final String TAG = SMBackendRequestImpl.class.getSimpleName();

    private ApiClient client;

    private SessionService sessionService;

    private KeyStorageService keyStorageService;

    private EventService eventService;

    private AuthenticationInterceptor interceptor;

    private SMSecretConversionService conversionService;

    private SMEncryptionService encryptionService;

    private boolean loginRunning = false;

    @Inject
    public SMBackendRequestImpl(SessionService sessionService, KeyStorageService keyStorageService, EventService eventService, SMEncryptionService encryptionService, SMSecretConversionService conversionService) {
        this.sessionService = sessionService;
        this.keyStorageService = keyStorageService;
        this.eventService = eventService;
        this.interceptor = new SMAuthenticationInterceptor(sessionService, keyStorageService, encryptionService);
        this.encryptionService = encryptionService;
        this.conversionService = conversionService;
    }

    @Override
    public boolean backendSupportsSharingPasswords() {
        return true;
    }

    @Override
    public boolean backendSupportsAddingPasswords() {
        return true;
    }

    protected DefaultApi getApi() throws MalformedURLException {
        // lazy load so that Robolectric won't throw errors loading the library
        if (client==null) {
            client = new ApiClient();
        }
        client.getAdapterBuilder().baseUrl(getUrl(sessionService.getUrl()));
        if (!client.getApiAuthorizations().containsKey("custom")) {
            client.addAuthorization("custom", interceptor);
        }
        client.getOkBuilder().connectTimeout(30, TimeUnit.SECONDS);
        client.getOkBuilder().readTimeout(30, TimeUnit.SECONDS);
        client.getOkBuilder().writeTimeout(30, TimeUnit.SECONDS);
        DefaultApi api = client.createService(DefaultApi.class);
        return api;
    }

    protected String getUrl(String url) throws MalformedURLException {
        URL u = new URL(url);
        if (u.getPath().length()==0) {
            return url+"/prod/v1/";
        }

        if (u.getPath().length()==1) {
            return url+"prod/v1/";
        }

        if (u.getPath().endsWith("/v1")) {
            return url+"/";
        }
        if (!u.getPath().endsWith("/v1/")) {
            return url+"/v1/";
        }
        return url;
    }

    @Override
    public void login() {
        final SessionInfo session = sessionService.getCurrentSession();
        if (keyStorageService.isPrivateKeyStored()) {

            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        byte[] aesKey = encryptionService.keyExtend(sessionService.getUsername(), session.getPassword());
                        byte[] encryptedPrivateKey = encryptionService.decodeBase64(keyStorageService.getEncryptedPrivateKey());
                        encryptionService.decryptAES(aesKey, encryptedPrivateKey);
                    } catch (Exception e) {
                        Log.e(TAG,"Failed to decrypt key", e);
                        eventService.postEvent(new FatalErrorEvent(e.getMessage()));
                        return null;
                    }

                    eventService.postEvent(new LoginSuccessfulEvent());
                    //downloadSecrets();
                    return null;
                }
            }.execute();
        } else {
            try {
                downloadEncryptedPrivateKey();
            } catch (MalformedURLException e) {
                Log.e(TAG,"Failed to download key", e);
                eventService.postEvent(new FatalErrorEvent(e.getMessage()));
            }
        }
    }

    private void downloadEncryptedPrivateKey() throws MalformedURLException {
        Log.i(TAG, "Downloading the private key");
        Call<String> call = getApi().getUserEncryptedPrivateKey(sessionService.getUsername());
        loginRunning = true;
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loginRunning = false;
                try {
                    String key = response.body();
                    if (response.code()!=200) {
                        eventService.postEvent(new FatalErrorEvent("Response: " + response.code()));
                    } else if (key==null) {
                        eventService.postEvent(new FatalErrorEvent("No key available"));
                    } else {

                        // Swagger codegen+Retrofit doesn't handle non-json responses very well
                        // The server returns Base64-encoded text
                        // The generated Retrofit code will Base64 encode the server response,
                        // leaving key double-base64-encoded
                        // To be safe and future safe, we double decode, then re-encode
                        // In the future, if this gets fixed, then the second decode() will throw
                        // an exception.  We ignore the exception, then encode it once.
                        byte[] keyBytes = key.getBytes("ASCII");
                        try {
                            keyBytes = Base64.decode(keyBytes, Base64.NO_WRAP);
                            keyBytes = Base64.decode(keyBytes, Base64.NO_WRAP);
                        } catch (Exception e) {
                        }

                        key = Base64.encodeToString(keyBytes, Base64.NO_WRAP);

                        keyStorageService.saveEncryptedPrivateKey(key);
                        eventService.postEvent(new LoginSuccessfulEvent());
                        //downloadSecrets();
                    }
                } catch (IOException e) {
                    Log.w(TAG, "downloadEncryptedPrivateKey() save failed", e);
                    eventService.postEvent(new FatalErrorEvent(e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.w(TAG, "downloadEncryptedPrivateKey() failed", t);
                loginRunning = false;
                eventService.postEvent(new FatalErrorEvent(t.getMessage()));

            }
        });
    }

    private void downloadSecrets() throws MalformedURLException {
        final SessionInfo session = sessionService.getCurrentSession();
        Log.i(TAG, "Downloading the secrets, kicked off from "+Thread.currentThread().getName());
        final Call<Map<String,Secret>> call = getApi().getUserSecrets(sessionService.getUsername());
        call.enqueue(new Callback<Map<String,Secret>>() {
            @Override
            public void onResponse(Call<Map<String,Secret>> call, final Response<Map<String,Secret>> response) {
                if (response.body()!=null) {
                    Log.d(TAG, "Downloaded " + response.body().size() + " secrets on "+Thread.currentThread().getName());
                }
                session.setPasswordBeanList(Collections.<PasswordBean>emptyList());
                session.setCategoryList(Collections.<Category>emptyList());

                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Log.d(TAG, "Processing secrets on "+Thread.currentThread().getName()+" thread");
                            conversionService.processSecrets(session, response.body());
                        } catch (IOException e) {
                            Log.w(TAG, "downloadSecrets() error", e);
                            eventService.postEvent(new FatalErrorEvent(e.getMessage()));
                        }
                        return null;
                    }
                }.execute();
            }

            @Override
            public void onFailure(Call<Map<String,Secret>> call, Throwable t) {
                Log.w(TAG, "downloadSecrets() failed", t);
                eventService.postEvent(new FatalErrorEvent(t.getMessage()));
            }
        });
    }

    @Override
    public boolean isLoginRunning() {
        return loginRunning;
    }

    @Override
    public boolean isCrudRunning() {
        return false;
    }

    @Override
    public void addPassword(PasswordBean bean) {

    }

    @Override
    public void listCategories() {

    }

    @Override
    public void listPasswords() {
        try {
            downloadSecrets();
        } catch (MalformedURLException e) {
            Log.e(TAG,"Failed to download passwords", e);
            eventService.postEvent(new FatalErrorEvent(e.getMessage()));
        }
    }
}
