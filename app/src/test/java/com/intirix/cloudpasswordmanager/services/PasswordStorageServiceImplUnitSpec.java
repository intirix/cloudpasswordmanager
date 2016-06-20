package com.intirix.cloudpasswordmanager.services;

import android.content.Context;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jeff on 6/20/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordStorageServiceImplUnitSpec {

    public static final String TESTUSER = "testuser";
    public static final String TESTPASS = "testpass";
    public static final String AUTHORIZATION = "Basic dGVzdHVzZXI6dGVzdHBhc3M=";
    private Context context;

    private MockSessionService sessionService;

    private PasswordRestService restService;

    private PasswordStorageServiceImpl impl;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
        sessionService = new MockSessionService();
        restService = EasyMock.createMock(PasswordRestService.class);

        impl = new PasswordStorageServiceImpl(context, sessionService) {
            @Override
            protected PasswordRestService getRestService() {
                return restService;
            }
        };
    }

    @Test
    public void successfulVersionRequestPassesVersionToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.setPassword(TESTPASS);

        // expect the version to be sent to the callback
        final String VERSION = "19.0";
        VersionCallback callback = EasyMock.createMock(VersionCallback.class);
        callback.onReturn(VERSION);
        EasyMock.expectLastCall();

        Call<String> call = new MockCall<String>() {
            @Override
            public void enqueue(Callback cb) {
                cb.onResponse(null, Response.success(VERSION));
            }
        };

        // verify that the Auth header is sent correctly
        EasyMock.expect(restService.getVersion(AUTHORIZATION)).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }

    @Test
    public void failedVersionRequestPassesExceptionToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.setPassword(TESTPASS);

        // expect an exception to be sent to the callback
        final String MESSAGE = "Message inside of the exception";
        VersionCallback callback = EasyMock.createMock(VersionCallback.class);
        callback.onError("Failed: java.lang.Exception: "+MESSAGE);
        EasyMock.expectLastCall();

        Call<String> call = new MockCall<String>() {
            @Override
            public void enqueue(Callback cb) {
                cb.onFailure(null, new Exception(MESSAGE));
            }
        };

        // verify that the Auth header is sent correctly
        EasyMock.expect(restService.getVersion(AUTHORIZATION)).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }


    @Test
    public void authenticationFailurePassesStringsXmlErrorToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.setPassword(TESTPASS);

        // expect an exception to be sent to the callback
        final String MESSAGE = context.getString(R.string.error_invalid_username_password).toString();
        VersionCallback callback = EasyMock.createMock(VersionCallback.class);
        callback.onError(MESSAGE);
        EasyMock.expectLastCall();

        Call<String> call = new MockCall<String>() {
            @Override
            public void enqueue(Callback cb) {
                cb.onResponse(null, Response.error(401, ResponseBody.create(MediaType.parse("text/plain"), "Unauthorized")));
            }
        };

        // verify that the Auth header is sent correctly
        EasyMock.expect(restService.getVersion(AUTHORIZATION)).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }

    @Test
    public void serverErrorPassesMessageToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.setPassword(TESTPASS);

        // expect an exception to be sent to the callback
        final String MESSAGE = "Internal server error";
        VersionCallback callback = EasyMock.createMock(VersionCallback.class);
        callback.onError(MESSAGE);
        EasyMock.expectLastCall();

        Call<String> call = new MockCall<String>() {
            @Override
            public void enqueue(Callback cb) {
                cb.onResponse(null, Response.error(ResponseBody.create(MediaType.parse("text/plain"),""),new okhttp3.Response.Builder() //
                        .code(500)
                        .message(MESSAGE)
                        .protocol(Protocol.HTTP_1_1)
                        .request(new Request.Builder().url("http://localhost/").build())
                        .build()));
            }
        };

        // verify that the Auth header is sent correctly
        EasyMock.expect(restService.getVersion(AUTHORIZATION)).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }

}
