package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.ocp.MockCall;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.MockEventService;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jeff on 7/14/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SMBackendRequestImplUnitSpec {

    private SMBackendRequestImpl impl;

    private MockSessionService sessionService;

    private MockEventService eventService;

    private KeyStorageService keyStorageService;

    private DefaultApi api;

    private String username;

    @Before
    public void setUp() {
        username = "myuser";
        sessionService = new MockSessionService();
        sessionService.setUsername(username);
        sessionService.start();

        eventService = new MockEventService();
        keyStorageService = EasyMock.createMock(KeyStorageService.class);
        api = EasyMock.createMock(DefaultApi.class);

        impl = new SMBackendRequestImpl(sessionService, keyStorageService, eventService) {
            @Override
            protected DefaultApi getApi() {
                return api;
            }
        };
    }

    @Test
    public void verifyLoginWithoutLocalKeyDownloadsKey() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(false).anyTimes();
        keyStorageService.saveEncryptedPrivateKey("TEST123");
        EasyMock.expectLastCall();


        Call<String> privateKeyCall = new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success("TEST123"));
            }
        };

        Call<List<Secret>> secretsCall = new MockCall<List<Secret>>() {
            @Override
            public void enqueue(Callback<List<Secret>> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success(Collections.<Secret>emptyList()));
            }
        };

        EasyMock.expect(api.getUserEncryptedPrivateKey(username)).andReturn(privateKeyCall);
        EasyMock.expect(api.getUserSecrets(username)).andReturn(secretsCall);

        EasyMock.replay(api,keyStorageService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        eventService.assertEventType(1, PasswordListUpdatedEvent.class);


        EasyMock.verify(api,keyStorageService);
    }

    @Test
    public void verifyFailureToDownloadKeySendsError() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(false).anyTimes();


        Call<String> privateKeyCall = new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onFailure(null,new IOException("Failed"));
            }
        };

        EasyMock.expect(api.getUserEncryptedPrivateKey(username)).andReturn(privateKeyCall);

        EasyMock.replay(api,keyStorageService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);


        EasyMock.verify(api,keyStorageService);
    }

    @Test
    public void verifyFailureToSaveDownloadedKeySendsError() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(false).anyTimes();
        keyStorageService.saveEncryptedPrivateKey("TEST123");
        EasyMock.expectLastCall().andThrow(new IOException("Failed to save key"));


        Call<String> privateKeyCall = new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success("TEST123"));
            }
        };

        EasyMock.expect(api.getUserEncryptedPrivateKey(username)).andReturn(privateKeyCall);

        EasyMock.replay(api,keyStorageService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);


        EasyMock.verify(api,keyStorageService);
    }


    @Test
    public void verifyLoginWithLocalKeyDoesNotDownloadKey() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(true).anyTimes();
        EasyMock.expect(keyStorageService.getEncryptedPrivateKey()).andReturn("TEST123").anyTimes();
        EasyMock.expectLastCall();


        Call<List<Secret>> secretsCall = new MockCall<List<Secret>>() {
            @Override
            public void enqueue(Callback<List<Secret>> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success(Collections.<Secret>emptyList()));
            }
        };

        EasyMock.expect(api.getUserSecrets(username)).andReturn(secretsCall);

        EasyMock.replay(api,keyStorageService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        eventService.assertEventType(1, PasswordListUpdatedEvent.class);


        EasyMock.verify(api,keyStorageService);
    }

    @Test
    public void verifyFailedLoginSendsError() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(true).anyTimes();
        EasyMock.expect(keyStorageService.getEncryptedPrivateKey()).andReturn("TEST123").anyTimes();
        EasyMock.expectLastCall();


        Call<List<Secret>> secretsCall = new MockCall<List<Secret>>() {
            @Override
            public void enqueue(Callback<List<Secret>> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onFailure(null, new IOException("Access denied"));
            }
        };

        EasyMock.expect(api.getUserSecrets(username)).andReturn(secretsCall);

        EasyMock.replay(api,keyStorageService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);


        EasyMock.verify(api,keyStorageService);
    }

}
