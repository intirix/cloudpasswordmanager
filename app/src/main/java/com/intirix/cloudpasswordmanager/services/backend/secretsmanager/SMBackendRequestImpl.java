package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.Log;

import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestInterface;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.backend.ocp.OCPBackendRequestImpl;
import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;
import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private boolean loginRunning = false;

    @Inject
    public SMBackendRequestImpl(SessionService sessionService, KeyStorageService keyStorageService, EventService eventService) {
        this.sessionService = sessionService;
        this.keyStorageService = keyStorageService;
        this.eventService = eventService;
        this.interceptor = new AuthenticationInterceptor(sessionService);
        client = new ApiClient();
    }

    protected DefaultApi getApi() {
        client.getAdapterBuilder().baseUrl(sessionService.getUrl());
        if (!client.getApiAuthorizations().containsKey("custom")) {
            client.addAuthorization("custom", interceptor);
        }
        DefaultApi api = client.createService(DefaultApi.class);
        return api;
    }

    @Override
    public void login() {
        if (keyStorageService.isPrivateKeyStored()) {
            eventService.postEvent(new LoginSuccessfulEvent());
            downloadSecrets();
        } else {
            downloadEncryptedPrivateKey();
        }
    }

    private void downloadEncryptedPrivateKey() {
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
                        keyStorageService.saveEncryptedPrivateKey(key);
                        eventService.postEvent(new LoginSuccessfulEvent());
                        downloadSecrets();
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

    private void downloadSecrets() {
        final SessionInfo session = sessionService.getCurrentSession();
        Log.i(TAG, "Downloading the secrets");
        Call<Map<String,Secret>> call = getApi().getUserSecrets(sessionService.getUsername());
        call.enqueue(new Callback<Map<String,Secret>>() {
            @Override
            public void onResponse(Call<Map<String,Secret>> call, Response<Map<String,Secret>> response) {
                if (response.body()!=null) {
                    Log.d(TAG, "Downloaded " + response.body().size() + " secrets");
                }
                session.setPasswordList(Collections.<PasswordInfo>emptyList());
                session.setPasswordBeanList(Collections.<PasswordBean>emptyList());
                session.setCategoryList(Collections.<Category>emptyList());
                eventService.postEvent(new CategoryListUpdatedEvent());
                eventService.postEvent(new PasswordListUpdatedEvent());
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
    public void listCategories() {

    }

    @Override
    public void listPasswords() {
        downloadSecrets();
    }
}
