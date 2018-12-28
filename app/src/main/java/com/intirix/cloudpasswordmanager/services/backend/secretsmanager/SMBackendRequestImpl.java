package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.se.omapi.Session;
import android.util.Base64;
import android.util.Log;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.ErrorEvent;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.InfoEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordadd.PasswordAddedEvent;
import com.intirix.cloudpasswordmanager.pages.passworddetail.PasswordUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordsLoadedEvent;
import com.intirix.cloudpasswordmanager.services.SharedEncryptionService;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestAddPasswordInterface;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestBatchShareInterface;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestInterface;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestShareInterface;
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
import com.intirix.secretsmanager.clientv1.model.SecretUserData;
import com.intirix.secretsmanager.clientv1.model.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jeff on 7/14/17.
 */

public class SMBackendRequestImpl implements BackendRequestInterface, BackendRequestAddPasswordInterface, BackendRequestBatchShareInterface {

    private static final String TAG = SMBackendRequestImpl.class.getSimpleName();

    private ApiClient client;

    private SessionService sessionService;

    private KeyStorageService keyStorageService;

    private EventService eventService;

    private AuthenticationInterceptor interceptor;

    private SMSecretConversionService conversionService;

    private SharedEncryptionService encryptionService;

    private boolean loginRunning = false;

    private boolean crudRunning = false;

    private Context context;

    @Inject
    public SMBackendRequestImpl(Context context, SessionService sessionService, KeyStorageService keyStorageService, EventService eventService, SharedEncryptionService encryptionService, SMSecretConversionService conversionService) {
        this.context = context;
        this.sessionService = sessionService;
        this.keyStorageService = keyStorageService;
        this.eventService = eventService;
        this.interceptor = new SMAuthenticationInterceptor(sessionService, keyStorageService, encryptionService);
        this.encryptionService = encryptionService;
        this.conversionService = conversionService;
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
        loginRunning = true;

        if (keyStorageService.isPrivateKeyStored()) {
            Log.d(TAG,"Private key already stored");
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        Log.d(TAG,"Performing scrypt");
                        byte[] aesKey = encryptionService.keyExtendUsingScrypt(sessionService.getUsername(), session.getPassword());
                        byte[] encryptedPrivateKey = encryptionService.decodeBase64(keyStorageService.getEncryptedPrivateKey());
                        Log.d(TAG, "Decrypting private key");
                        encryptionService.decryptAES(aesKey, encryptedPrivateKey);
                    } catch (Exception e) {
                        Log.e(TAG,"Failed to decrypt key", e);
                        loginRunning = false;
                        eventService.postEvent(new FatalErrorEvent(e.getMessage()));
                        return null;
                    } finally {
                        loginRunning = false;
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
                loginRunning = false;
                eventService.postEvent(new FatalErrorEvent(e.getMessage()));
            }
        }
    }

    @Override
    public boolean supportsUrl() {
        return true;
    }

    @Override
    public boolean supportsUsername() {
        return true;
    }

    @Override
    public boolean supportsPassword() {
        return true;
    }

    @Override
    public boolean supportsCustomKey() {
        return true;
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

                new AsyncTask<Void,Void,Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Log.d(TAG, "Processing secrets on "+Thread.currentThread().getName()+" thread");
                            conversionService.processSecrets(session, response.body());
                            eventService.postEvent(new PasswordsLoadedEvent());
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
        return crudRunning;
    }

    @Override
    public void addPassword(final PasswordBean bean) {
        crudRunning = true;
        try {
            Call<String> call = getApi().getUserPublicKey(sessionService.getUsername());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    final String publicKey = response.body();
                    try {
                        Secret secret = conversionService.createSecretFromPasswordBean(sessionService.getCurrentSession(), publicKey, bean);
                        getApi().addSecret(secret).enqueue(new Callback<Secret>() {
                            @Override
                            public void onResponse(Call<Secret> call, Response<Secret> response) {
                                crudRunning = false;
                                if (response.isSuccessful()) {
                                    Log.i(TAG, "addPassword() successfully added secret");
                                    eventService.postEvent(new PasswordAddedEvent());
                                } else {
                                    Log.w(TAG, "addPassword() got code="+response.code()+" trying to add secret");
                                    eventService.postEvent(new ErrorEvent("Response: " + response.code()));
                                }
                            }

                            @Override
                            public void onFailure(Call<Secret> call, Throwable t) {
                                crudRunning = false;
                                Log.w(TAG, "addPassword() add secret failed", t);
                                eventService.postEvent(new ErrorEvent(t.getMessage()));
                            }
                        });
                    } catch (Exception e) {
                        crudRunning = false;
                        eventService.postEvent(new ErrorEvent(e.getMessage()));
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.w(TAG, "addPassword() download public key failed", t);
                    crudRunning = false;
                    eventService.postEvent(new ErrorEvent(t.getMessage()));
                }
            });


        } catch (Exception e) {
            crudRunning = false;
            eventService.postEvent(new ErrorEvent(e.getMessage()));
        }
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

    @Override
    public void listUsers() {
        try {
            getApi().listUsers().enqueue(new Callback<Map<String, User>>() {
                @Override
                public void onResponse(Call<Map<String, User>> call, Response<Map<String, User>> response) {
                    if (response.isSuccessful()) {
                        final List<String> users = new ArrayList<>();
                        users.addAll(response.body().keySet());
                        Collections.sort(users);
                        sessionService.getCurrentSession().setServerUsers(users);
                    } else {
                        eventService.postEvent(new ErrorEvent("Code: "+response.code()));
                    }
                }

                @Override
                public void onFailure(Call<Map<String, User>> call, Throwable t) {
                    Log.e(TAG, "Failed to get the users", t);
                    eventService.postEvent(new ErrorEvent(t.getMessage()));
                }
            });
        } catch (MalformedURLException e) {
            Log.e(TAG, "Failed to get the users", e);
            eventService.postEvent(new ErrorEvent(e.getMessage()));
        }
    }

    /**
     * Use AsyncTask instead of RetroFit's background system since we need to make a bunch of requests
     * @param bean
     * @param usersToAdd
     * @param usersToRemove
     */
    @Override
    public void updateSharingForPassword(final PasswordBean bean, final Set<String> usersToAdd, final Set<String> usersToRemove) {
        crudRunning = true;
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    doUpdateSharingForPassword(bean, usersToAdd, usersToRemove);
                } catch (Exception e) {
                    crudRunning = false;
                    Log.e(TAG,e.getMessage(),e);
                    eventService.postEvent(new ErrorEvent(e.getMessage()));
                } finally {
                    crudRunning = false;
                }
                return null;
            }
        }.execute();
    }

    private void doUpdateSharingForPassword(final PasswordBean bean, final Set<String> usersToAdd, final Set<String> usersToRemove) throws Exception {
        SessionInfo session = sessionService.getCurrentSession();

        eventService.postEvent(new InfoEvent(context.getString(R.string.password_detail_info_download_message)));
        Response<Secret> resp1 = getApi().getSecret(bean.getId()).execute();
        Log.d(TAG,"Downloaded secret, code="+resp1.code());
        Secret secret = resp1.body();
        eventService.postEvent(new InfoEvent(context.getString(R.string.password_detail_info_decrypt_message)));
        byte[] secretKeyPair = conversionService.getKeyForSecret(session,secret);
        Log.d(TAG,"Performing shares");

        int successfulUpdates = 0;
        for (final String user: usersToAdd) {
            eventService.postEvent(new InfoEvent(context.getString(R.string.info_download_pubkey_message, user)));
            Response<String> respPubKey = getApi().getUserPublicKey(user).execute();
            Log.d(TAG,"Downloaded public key, code="+respPubKey.code());
            if (respPubKey.isSuccessful()) {
                eventService.postEvent(new InfoEvent(context.getString(R.string.info_encrypt_message, user)));
                SecretUserData userData = conversionService.createUserData(session, secret, user, respPubKey.body(), secretKeyPair);

                eventService.postEvent(new InfoEvent(context.getString(R.string.info_update_secret_message, user)));
                Response<Secret> resp2 = getApi().shareSecret(bean.getId(), user, userData).execute();
                if (resp2.isSuccessful()) {
                    Log.d(TAG, "Successfully granted access");
                    successfulUpdates++;
                } else {
                    Log.w(TAG, "Failed to grant access");
                    eventService.postEvent(new ErrorEvent("Code: "+respPubKey.code()));
                }
            } else if (respPubKey.code()==404) {
                Log.w(TAG,"User has not public key");
                eventService.postEvent(new ErrorEvent(context.getString(R.string.error_user_has_not_key,user)));
            } else {
                Log.w(TAG,"Failed to download public key: "+respPubKey.code());
                eventService.postEvent(new ErrorEvent("Code: "+respPubKey.code()));
            }
        }
        Log.d(TAG,"Performing unshares");

        for (final String user: usersToRemove) {
            Log.d(TAG,"Unsharing secret from "+user);
            Response<Secret> resp3 = getApi().unshareSecret(bean.getId(), user).execute();
            if (resp3.isSuccessful()) {
                Log.d(TAG, "Successfully revoked access");
                successfulUpdates++;
            } else {
                Log.w(TAG, "Failed to revoke access");
                eventService.postEvent(new ErrorEvent("Code: "+resp3.code()));
            }
        }

        if (successfulUpdates>0) {
            Log.d(TAG, "Performed "+successfulUpdates+" updates");
            eventService.postEvent(new InfoEvent(context.getString(R.string.password_detail_info_download_message)));
            Response<Secret> resp2 = getApi().getSecret(bean.getId()).execute();
            conversionService.updateSecret(session, resp2.body());
        } else {
            Log.w(TAG, "No updates performed");
        }

    }
}
