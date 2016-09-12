/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.services.backend;

import android.content.Context;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordResponse;
import com.intirix.cloudpasswordmanager.services.backend.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.backend.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.backend.callbacks.VersionCallback;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Specification for all the behaviors of the PasswordStorageServiceImpl class
 * Created by jeff on 6/20/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
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

        impl = new PasswordStorageServiceImpl(context, sessionService, null) {
            @Override
            protected PasswordRestService getRestService() {
                return restService;
            }
        };
    }

    @Test
    public void successfulVersionRequestPassesVersionToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

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
        EasyMock.expect(restService.getVersion()).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }

    @Test
    public void failedVersionRequestPassesExceptionToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

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
        EasyMock.expect(restService.getVersion()).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }


    @Test
    public void authenticationFailurePassesStringsXmlErrorToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

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
        EasyMock.expect(restService.getVersion()).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }

    @Test
    public void serverErrorPassesMessageToCallback() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        // expect an exception to be sent to the callback
        final String MESSAGE = "Internal server error";
        VersionCallback callback = EasyMock.createMock(VersionCallback.class);
        callback.onError("500: "+MESSAGE);
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
        EasyMock.expect(restService.getVersion()).andReturn(call);
        EasyMock.replay(callback, restService);

        // invoke the service to test it
        impl.getServerVersion(callback);
        EasyMock.verify(callback, restService);
    }

    @Test
    public void verifyEmptyCategoryResponseWorks() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        final List<Category> empty = Collections.emptyList();
        final AtomicInteger counter = new AtomicInteger(0);

        CategoryListCallback cb = new CategoryListCallback() {
            @Override
            public void onReturn(List<Category> categories) {
                Assert.assertEquals(0, categories.size());
                counter.incrementAndGet();
            }

            @Override
            public void onError(String message) {
                Assert.fail(message);
            }
        };

        Call<List<Category>> call = new MockCall<List<Category>>() {
            @Override
            public void enqueue(Callback callback) {
                callback.onResponse(this, Response.success(empty));
            }
        };

        EasyMock.expect(restService.listCategories()).andReturn(call);
        EasyMock.replay(restService);

        impl.listCategories(cb);
        EasyMock.verify(restService);
        Assert.assertEquals(1,counter.get());

    }

    /**
     * The Owncloud Password app API returns values for other users.  The values are nulled out
     * so there is no information disclosure, but it does mean the UI has to filter out all
     * the values that are null
     */
    @Test
    public void verifyOtherUserCategorysAreFiltered() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        final Category c1 = new Category();
        final Category c2 = new Category();
        c2.setId("2");
        c2.setUser_id(TESTUSER);
        c2.setCategory_name("My category");
        c2.setCategory_colour("aaaaaa");

        final List<Category> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);

        final AtomicInteger counter = new AtomicInteger(0);

        CategoryListCallback cb = new CategoryListCallback() {
            @Override
            public void onReturn(List<Category> categories) {
                Assert.assertEquals(1, categories.size());
                Assert.assertEquals(c2, categories.get(0));
                counter.incrementAndGet();
            }

            @Override
            public void onError(String message) {
                Assert.fail(message);
            }
        };

        Call<List<Category>> call = new MockCall<List<Category>>() {
            @Override
            public void enqueue(Callback callback) {
                callback.onResponse(this, Response.success(list));
            }
        };

        EasyMock.expect(restService.listCategories()).andReturn(call);
        EasyMock.replay(restService);

        impl.listCategories(cb);
        EasyMock.verify(restService);
        Assert.assertEquals(1,counter.get());

    }


    @Test
    public void verifyEmptyPasswordResponseWorks() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        final List<PasswordResponse> empty = Collections.emptyList();
        final AtomicInteger counter = new AtomicInteger(0);

        PasswordListCallback cb = new PasswordListCallback() {
            @Override
            public void onReturn(List<PasswordInfo> passwords) {
                Assert.assertEquals(0, passwords.size());
                counter.incrementAndGet();
            }

            @Override
            public void onError(String message) {
                Assert.fail(message);
            }
        };

        Call<List<PasswordResponse>> call = new MockCall<List<PasswordResponse>>() {
            @Override
            public void enqueue(Callback callback) {
                callback.onResponse(this, Response.success(empty));
            }
        };

        EasyMock.expect(restService.listPasswords()).andReturn(call);
        EasyMock.replay(restService);

        impl.listPasswords(cb);
        EasyMock.verify(restService);
        Assert.assertEquals(1,counter.get());

    }

    @Test
    public void verifyDeletedPasswordsAreRemoved() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        final List<PasswordResponse> list = new ArrayList<>();

        final PasswordResponse pr1 = new PasswordResponse();
        pr1.setId("1");
        pr1.setUser_id(TESTUSER);
        pr1.setProperties("{}");
        pr1.setDeleted("1");
        final PasswordResponse pr2 = new PasswordResponse();
        pr2.setId("2");
        pr2.setUser_id(TESTUSER);
        pr2.setProperties("{}");
        pr2.setDeleted("0");

        final PasswordInfo pi2 = new PasswordInfo();
        pi2.setId("2");
        pi2.setUser_id(TESTUSER);

        list.add(pr1);
        list.add(pr2);

        final AtomicInteger counter = new AtomicInteger(0);

        PasswordListCallback cb = new PasswordListCallback() {
            @Override
            public void onReturn(List<PasswordInfo> passwords) {
                Assert.assertEquals(1, passwords.size());
                Assert.assertEquals(pi2, passwords.get(0));
                counter.incrementAndGet();
            }

            @Override
            public void onError(String message) {
                Assert.fail(message);
            }
        };

        Call<List<PasswordResponse>> call = new MockCall<List<PasswordResponse>>() {
            @Override
            public void enqueue(Callback callback) {
                callback.onResponse(this, Response.success(list));
            }
        };

        EasyMock.expect(restService.listPasswords()).andReturn(call);
        EasyMock.replay(restService);

        impl.listPasswords(cb);
        EasyMock.verify(restService);
        Assert.assertEquals(1,counter.get());

    }


    @Test
    public void verifyOtherUserPasswordsAreRemoved() {
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        final List<PasswordResponse> list = new ArrayList<>();

        final PasswordResponse pr1 = new PasswordResponse();
        pr1.setId("1");
        pr1.setUser_id("not-me");
        pr1.setDeleted("0");
        final PasswordResponse pr2 = new PasswordResponse();
        pr2.setId("2");
        pr2.setUser_id(TESTUSER);
        pr2.setDeleted("0");
        pr2.setProperties("{}");

        final PasswordInfo pi2 = new PasswordInfo();
        pi2.setId("2");
        pi2.setUser_id(TESTUSER);

        list.add(pr1);
        list.add(pr2);

        final AtomicInteger counter = new AtomicInteger(0);

        PasswordListCallback cb = new PasswordListCallback() {
            @Override
            public void onReturn(List<PasswordInfo> passwords) {
                Assert.assertEquals(1, passwords.size());
                Assert.assertEquals(pi2, passwords.get(0));
                counter.incrementAndGet();
            }

            @Override
            public void onError(String message) {
                Assert.fail(message);
            }
        };

        Call<List<PasswordResponse>> call = new MockCall<List<PasswordResponse>>() {
            @Override
            public void enqueue(Callback callback) {
                callback.onResponse(this, Response.success(list));
            }
        };

        EasyMock.expect(restService.listPasswords()).andReturn(call);
        EasyMock.replay(restService);

        impl.listPasswords(cb);
        EasyMock.verify(restService);
        Assert.assertEquals(1,counter.get());

    }




    @Test
    public void verifyOtherUserPasswordsAreRemovedDuringTranslation() throws ParseException {
        List<PasswordResponse> resp = new ArrayList<>();

        PasswordResponse resp1 = new PasswordResponse();
        resp1.setUser_id("other_user");
        resp.add(resp1);

        List<PasswordInfo> result = impl.translatePasswordList(resp);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void verifyDeletedPasswordsAreRemovedDuringTranslation() throws ParseException {
        List<PasswordResponse> resp = new ArrayList<>();

        PasswordResponse resp1 = new PasswordResponse();
        resp1.setUser_id("me");
        resp1.setDeleted("1");
        resp.add(resp1);

        List<PasswordInfo> result = impl.translatePasswordList(resp);
        Assert.assertEquals(0, result.size());
    }


    @Test
    public void verifySharedPasswordsAreKeptDuringTranslation() throws ParseException {
        List<PasswordResponse> resp = new ArrayList<>();

        PasswordResponse resp1 = new PasswordResponse();
        resp1.setUser_id("other");
        resp1.setDeleted("0");
        resp1.setProperties("{}");
        resp.add(resp1);

        List<PasswordInfo> result = impl.translatePasswordList(resp);
        Assert.assertEquals(1, result.size());
    }


}
