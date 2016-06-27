package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
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

}
