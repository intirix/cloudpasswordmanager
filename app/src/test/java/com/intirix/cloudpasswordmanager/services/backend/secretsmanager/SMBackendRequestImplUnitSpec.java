package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.Base64;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.SharedEncryptionService;
import com.intirix.cloudpasswordmanager.services.backend.ocp.MockCall;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.MockEventService;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jeff on 7/14/17.
 */

@RunWith(RobolectricTestRunner.class)


public class SMBackendRequestImplUnitSpec {

    private SMBackendRequestImpl impl;

    private MockSessionService sessionService;

    private MockEventService eventService;

    private KeyStorageService keyStorageService;

    private DefaultApi api;

    private String username;

    String encryptedPrivateKey;

    SMSecretConversionService conversionService;


    @Before
    public void setUp() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream("/mock_rsa_key.enc"),buffer);
        encryptedPrivateKey = buffer.toString("ASCII");

        username = "admin";
        sessionService = new MockSessionService();
        sessionService.setUsername(username);
        sessionService.start();

        eventService = new MockEventService();
        keyStorageService = EasyMock.createMock(KeyStorageService.class);
        api = EasyMock.createMock(DefaultApi.class);
        SharedEncryptionService encryptionService = new SharedEncryptionService();

        conversionService = EasyMock.createMock(SMSecretConversionService.class);
        impl = new SMBackendRequestImpl(RuntimeEnvironment.application, sessionService, keyStorageService, eventService, encryptionService, conversionService) {
            @Override
            protected DefaultApi getApi() {
                return api;
            }
        };
    }

    @Test
    public void verifyUrlFormats() throws MalformedURLException {
        Assert.assertEquals("https://api.example.com/prod/v1/",impl.getUrl("https://api.example.com"));
        Assert.assertEquals("https://api.example.com/prod/v1/",impl.getUrl("https://api.example.com/"));
        Assert.assertEquals("https://api.example.com/prod/v1/",impl.getUrl("https://api.example.com/prod"));
        Assert.assertEquals("https://api.example.com/prod/v1/",impl.getUrl("https://api.example.com/prod/v1"));
        Assert.assertEquals("https://api.example.com/prod/v1/",impl.getUrl("https://api.example.com/prod/v1/"));
        Assert.assertEquals("https://api.example.com/test/v1/",impl.getUrl("https://api.example.com/test"));
        Assert.assertEquals("https://api.example.com/test/v1/",impl.getUrl("https://api.example.com/test/v1"));
        Assert.assertEquals("https://api.example.com/test/v1/",impl.getUrl("https://api.example.com/test/v1/"));
    }

    @Test
    public void verifyLoginWithoutLocalKeyDownloadsKey() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(false).anyTimes();
        keyStorageService.saveEncryptedPrivateKey(encryptedPrivateKey);
        EasyMock.expectLastCall();

        Call<String> privateKeyCall = new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success(encryptedPrivateKey));
                Assert.assertFalse(impl.isLoginRunning());
            }
        };

        Call<Map<String,Secret>> secretsCall = new MockCall<Map<String,Secret>>() {
            @Override
            public void enqueue(Callback<Map<String,Secret>> callback) {
                Assert.assertFalse(impl.isLoginRunning());
                callback.onResponse(null, Response.success(Collections.<String,Secret>emptyMap()));
            }
        };

        EasyMock.expect(api.getUserEncryptedPrivateKey(username)).andReturn(privateKeyCall);

        EasyMock.replay(api,keyStorageService,conversionService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        eventService.assertNumberOfPosts(1);

        EasyMock.verify(api,keyStorageService);
    }

    @Test
    public void verifyWrongPasswordWithLocalKeyFails() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(true).anyTimes();
        keyStorageService.getEncryptedPrivateKey();
        EasyMock.expectLastCall().andReturn(encryptedPrivateKey);

        sessionService.getCurrentSession().setPassword("wrongPassword");

        EasyMock.replay(api,keyStorageService,conversionService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertEventType(0, FatalErrorEvent.class);
        eventService.assertNumberOfPosts(1);

        Assert.assertNull(sessionService.getCurrentSession().getPasswordBeanList());
        Assert.assertNull(sessionService.getCurrentSession().getCategoryList());

        EasyMock.verify(api,keyStorageService,conversionService);
    }

        @Test
    public void verifyDownloadDoubleEncodedKeyWorks() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(false).anyTimes();
        keyStorageService.saveEncryptedPrivateKey(encryptedPrivateKey);
        EasyMock.expectLastCall();

        final String doubleEncoded = Base64.encodeToString(encryptedPrivateKey.getBytes("ASCII"),Base64.NO_WRAP);

        Call<String> privateKeyCall = new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success(doubleEncoded));
                Assert.assertFalse(impl.isLoginRunning());
            }
        };

        Call<Map<String,Secret>> secretsCall = new MockCall<Map<String,Secret>>() {
            @Override
            public void enqueue(Callback<Map<String,Secret>> callback) {
                Assert.assertFalse(impl.isLoginRunning());
                callback.onResponse(null, Response.success(Collections.<String,Secret>emptyMap()));
            }
        };

        EasyMock.expect(api.getUserEncryptedPrivateKey(username)).andReturn(privateKeyCall);

        EasyMock.replay(api,keyStorageService,conversionService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        eventService.assertNumberOfPosts(1);

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
        keyStorageService.saveEncryptedPrivateKey(encryptedPrivateKey);
        EasyMock.expectLastCall().andThrow(new IOException("Failed to save key"));


        Call<String> privateKeyCall = new MockCall<String>() {
            @Override
            public void enqueue(Callback<String> callback) {
                Assert.assertTrue(impl.isLoginRunning());
                callback.onResponse(null, Response.success(encryptedPrivateKey));
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
        sessionService.getCurrentSession().setPassword("password");
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(true).anyTimes();
        EasyMock.expect(keyStorageService.getEncryptedPrivateKey()).andReturn(encryptedPrivateKey).anyTimes();
        EasyMock.expectLastCall();

        Call<Map<String,Secret>> secretsCall = new MockCall<Map<String,Secret>>() {
            @Override
            public void enqueue(Callback<Map<String,Secret>> callback) {
                Assert.assertFalse(impl.isLoginRunning());
                callback.onResponse(null, Response.success(Collections.<String,Secret>emptyMap()));
            }
        };

        EasyMock.replay(api,keyStorageService,conversionService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        eventService.assertNumberOfPosts(1);


        EasyMock.verify(api,keyStorageService);
    }

    @Test
    public void verifyFailedLoginSendsError() throws IOException {
        EasyMock.expect(keyStorageService.isPrivateKeyStored()).andReturn(true).anyTimes();
        EasyMock.expect(keyStorageService.getEncryptedPrivateKey()).andReturn(encryptedPrivateKey).anyTimes();
        EasyMock.expectLastCall();

        sessionService.getCurrentSession().setPassword("password");


        Call<Map<String,Secret>> secretsCall = new MockCall<Map<String,Secret>>() {
            @Override
            public void enqueue(Callback<Map<String,Secret>> callback) {
                Assert.assertFalse(impl.isLoginRunning());
                callback.onFailure(null, new IOException("Access denied"));
            }
        };

        EasyMock.expect(api.getUserSecrets(username)).andReturn(secretsCall);

        EasyMock.replay(api,keyStorageService);
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        impl.listPasswords();
        Assert.assertFalse(impl.isLoginRunning());

        eventService.assertNumberOfPosts(2);
        // the login is considered successful when the private key is successfully decrypted
        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        eventService.assertEventType(1, FatalErrorEvent.class);


        EasyMock.verify(api,keyStorageService);
    }

}
