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

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.backend.PasswordRestService;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordResponse;
import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Specification for the REST service impl that gets generated my Retrofit
 * Created by jeff on 6/26/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)
public class PasswordRestServiceUnitSpec {

    public static final String TESTUSER = "testuser";
    public static final String TESTPASS = "testpass";
    public static final String AUTHORIZATION = "Basic dGVzdHVzZXI6dGVzdHBhc3M=";

    private PasswordRestService impl;

    private Interceptor interceptor;

    private String responseJson;

    private MockSessionService sessionService;

    @Before
    public void setUp() {

        sessionService = new MockSessionService();
        sessionService.setUsername(TESTUSER);
        sessionService.start();
        sessionService.getCurrentSession().setPassword(TESTPASS);

        interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Assert.assertEquals(AUTHORIZATION, chain.request().header("Authorization"));
                return new Response.Builder()
                        .request(chain.request())
                        .code(200)
                        .protocol(Protocol.HTTP_1_1)
                        .header("Content-Type", "application/json")
                        .body(ResponseBody.create(MediaType.parse("application/json"),responseJson))
                        .build();
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(sessionService))
                .addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://cloud.example.com/owncloud/index.php/apps/passwords/api/0.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        impl = retrofit.create(PasswordRestService.class);

    }

    @Test
    public void testSimpleVersionResponse() throws IOException {
        responseJson = "\"19\"";

        Assert.assertEquals("19", impl.getVersion().execute().body());
    }

    @Test
    public void testListCategoriesWithZero() throws IOException {
        responseJson = "[]";

        Assert.assertEquals(0, impl.listCategories().execute().body().size());
    }

    @Test
    public void testListCategoriesWithOne() throws IOException {
        responseJson = "[{\"id\":1,\"user_id\":\"pwuser1\",\"category_name\":\"test\",\"category_colour\":\"eeeeee\"}]";

        List<Category> categoryList = impl.listCategories().execute().body();
        Assert.assertEquals(1, categoryList.size());

        Category cat = categoryList.get(0);
        Assert.assertEquals("1", cat.getId());
        Assert.assertEquals("pwuser1", cat.getUser_id());
        Assert.assertEquals("test", cat.getCategory_name());
        Assert.assertEquals("eeeeee", cat.getCategory_colour());

    }

    @Test
    public void testListPasswordsWithZero() throws IOException {
        responseJson = "[]";

        List<PasswordResponse> passwordList = impl.listPasswords().execute().body();
        Assert.assertEquals(0, passwordList.size());
    }

    @Test
    public void testListPasswordsWithOne() throws IOException {
        setMockResponseJson("/password-list-example1.json");
        List<PasswordResponse> passwordList = impl.listPasswords().execute().body();

        Assert.assertEquals(1, passwordList.size());
        PasswordResponse pr = passwordList.get(0);

        Assert.assertEquals("5", pr.getId());
        Assert.assertEquals(TESTUSER, pr.getUser_id());
        Assert.assertEquals("www.github.com", pr.getWebsite());
        Assert.assertEquals("password", pr.getPass());
        Assert.assertEquals(221, pr.getProperties().length());
        Assert.assertFalse(pr.isNotes());
        Assert.assertEquals("0", pr.getDeleted());
    }

    @Test
    public void testListPasswordsWithNotes() throws IOException {
        setMockResponseJson("/password-list-example2.json");
        List<PasswordResponse> passwordList = impl.listPasswords().execute().body();

        Assert.assertEquals(1, passwordList.size());
        PasswordResponse pr = passwordList.get(0);

        Assert.assertEquals("5", pr.getId());
        Assert.assertEquals(TESTUSER, pr.getUser_id());
        Assert.assertEquals("www.github.com", pr.getWebsite());
        Assert.assertEquals("password", pr.getPass());
        Assert.assertEquals(224, pr.getProperties().length());
        Assert.assertTrue(pr.isNotes());
        Assert.assertEquals("0", pr.getDeleted());
    }

    @Test
    public void testListPasswordsWithNulls() throws IOException {
        setMockResponseJson("/password-list-example-with-nulls.json");
        List<PasswordResponse> passwordList = impl.listPasswords().execute().body();

        Assert.assertEquals(2, passwordList.size());
        PasswordResponse pr = passwordList.get(1);

        Assert.assertEquals("5", pr.getId());
        Assert.assertEquals(TESTUSER, pr.getUser_id());
        Assert.assertEquals("www.github.com", pr.getWebsite());
        Assert.assertEquals("password", pr.getPass());
        Assert.assertEquals(221, pr.getProperties().length());
        Assert.assertFalse(pr.isNotes());
        Assert.assertEquals("0", pr.getDeleted());
    }

    @Test
    public void testListPasswordsWithShared() throws IOException {
        setMockResponseJson("/password-list-example-with-shared.json");
        List<PasswordResponse> passwordList = impl.listPasswords().execute().body();

        Assert.assertEquals(2, passwordList.size());
        PasswordResponse pr = passwordList.get(0);

        Assert.assertEquals("1", pr.getId());
        Assert.assertEquals("pwuser2", pr.getUser_id());
        Assert.assertEquals("www.gmail.com", pr.getWebsite());
        Assert.assertEquals("password", pr.getPass());
        Assert.assertFalse(pr.isNotes());
        Assert.assertEquals("0", pr.getDeleted());
    }


    /**
     * Get the response from a file
     * @param path
     */
    private void setMockResponseJson(String path) {
        InputStream is = getClass().getResourceAsStream(path);

        Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
        responseJson = s.hasNext() ? s.next() : "";
    }

}
