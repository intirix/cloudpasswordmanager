package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.settings.MockKeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;
import com.intirix.cloudpasswordmanager.services.ui.MockEventService;
import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jeff on 7/19/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class SMSecretConversionServiceImplUnitSpec {

    private MockEventService eventService;

    private SMSecretConversionServiceImpl impl;

    private Interceptor interceptor;

    private String responseJson;

    private DefaultApi api;

    private MockSessionService sessionService;

    private SMEncryptionService encryptionService;

    private MockKeyStorageService keyStorageService;

    @Before
    public void setUp() throws IOException {
        sessionService = new MockSessionService();
        encryptionService = new SMEncryptionService();
        eventService = new MockEventService();

        keyStorageService = new MockKeyStorageService();

        org.apache.commons.io.output.ByteArrayOutputStream buffer = new org.apache.commons.io.output.ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream("/mock_rsa_key.enc"),buffer);
        keyStorageService.saveEncryptedPrivateKey(buffer.toString("ASCII"));


        sessionService.setUsername("admin");
        sessionService.start();
        sessionService.getCurrentSession().setPassword("password");

        impl = new SMSecretConversionServiceImpl(sessionService, eventService, encryptionService, keyStorageService);

        interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return new Response.Builder()
                        .request(chain.request())
                        .code(200)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .header("Content-Type", "application/json")
                        .body(ResponseBody.create(MediaType.parse("application/json"),responseJson))
                        .build();
            }
        };

        ApiClient client = new ApiClient();
        client.addAuthorization("mock",interceptor);

        api = client.createService(DefaultApi.class);

    }

    private void loadMock(String path) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
        IOUtils.copy(getClass().getResourceAsStream(path),buffer);
        responseJson = buffer.toString("UTF-8");
    }

    @Test
    public void verifyEmptyResponseStillSendsSuccessEvents() throws IOException {
        impl.processSecrets(sessionService.getCurrentSession(), Collections.<String, Secret>emptyMap());
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(PasswordListUpdatedEvent.class);
        eventService.assertEventType(CategoryListUpdatedEvent.class);
    }

    @Test
    public void verifyDecryption() throws IOException {
        loadMock("/mock_secret_empty.json");

        impl.processSecrets(sessionService.getCurrentSession(), api.getUserSecrets("admin").execute().body());
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(PasswordListUpdatedEvent.class);
        eventService.assertEventType(CategoryListUpdatedEvent.class);
        Assert.assertEquals(1,sessionService.getCurrentSession().getPasswordBeanList().size());
    }

}
