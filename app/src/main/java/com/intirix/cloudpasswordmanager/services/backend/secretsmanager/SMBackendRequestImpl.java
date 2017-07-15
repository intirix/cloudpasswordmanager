package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestInterface;
import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;
import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jeff on 7/14/17.
 */

public class SMBackendRequestImpl implements BackendRequestInterface {

    private ApiClient client;

    private SessionService sessionService;

    private KeyStorageService keyStorageService;

    private EventService eventService;

    private AuthenticationInterceptor interceptor;

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
        api.getUserSecrets(sessionService.getUsername());
        return api;
    }

    @Override
    public void login() {
        if (keyStorageService.isPrivateKeyStored()) {
            downloadSecrets();
        } else {
            downloadEncryptedPrivateKey();
        }
    }

    private void downloadEncryptedPrivateKey() {
        Call<String> call = getApi().getUserEncryptedPrivateKey(sessionService.getUsername());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    keyStorageService.saveEncryptedPrivateKey(response.body());
                    downloadSecrets();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void downloadSecrets() {
        Call<List<Secret>> call = getApi().getUserSecrets(sessionService.getUsername());
        call.enqueue(new Callback<List<Secret>>() {
            @Override
            public void onResponse(Call<List<Secret>> call, Response<List<Secret>> response) {
                eventService.postEvent(new PasswordListUpdatedEvent());
            }

            @Override
            public void onFailure(Call<List<Secret>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean isLoginRunning() {
        return false;
    }

    @Override
    public void listCategories() {

    }

    @Override
    public void listPasswords() {

    }
}
