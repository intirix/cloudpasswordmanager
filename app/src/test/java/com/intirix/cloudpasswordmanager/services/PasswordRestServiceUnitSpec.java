package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.beans.Category;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.List;

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
        application = TestPasswordApplication.class)
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
        sessionService.setPassword(TESTPASS);

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

}
