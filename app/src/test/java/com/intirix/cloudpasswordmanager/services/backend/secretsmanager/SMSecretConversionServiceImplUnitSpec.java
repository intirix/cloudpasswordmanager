package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;
import com.intirix.cloudpasswordmanager.services.settings.MockKeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.ColorService;
import com.intirix.cloudpasswordmanager.services.ui.ColorServiceImpl;
import com.intirix.cloudpasswordmanager.services.ui.EventService;
import com.intirix.cloudpasswordmanager.services.ui.MockEventService;
import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jeff on 7/19/17.
 */

@RunWith(RobolectricTestRunner.class)
public class SMSecretConversionServiceImplUnitSpec {

    private MockEventService eventService;

    private SMSecretConversionServiceImpl impl;

    private Interceptor interceptor;

    private String responseJson;

    //private DefaultApi api;

    private MockSessionService sessionService;

    private SMEncryptionService encryptionService;

    private MockKeyStorageService keyStorageService;

    private ColorService colorService;

    private String publicKey;

    @Before
    public void setUp() throws IOException {
        sessionService = new MockSessionService();
        encryptionService = new SMEncryptionService();
        eventService = new MockEventService();

        keyStorageService = new MockKeyStorageService();

        colorService = new ColorServiceImpl();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream("/mock_rsa_key.enc"),buffer);
        keyStorageService.saveEncryptedPrivateKey(buffer.toString("ASCII"));

        buffer.reset();
        IOUtils.copy(getClass().getResourceAsStream("/mock_rsa_pub.pem"),buffer);
        publicKey = buffer.toString("ASCII");


        sessionService.setUsername("admin");
        sessionService.start();
        sessionService.getCurrentSession().setPassword("password");

        impl = new SMSecretConversionServiceImpl(sessionService, eventService, encryptionService, keyStorageService, colorService);

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

        //ApiClient client = new ApiClient();
        //client.addAuthorization("mock",interceptor);

        //api = client.createService(DefaultApi.class);

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

    /*
    @Test
    public void verifyDecryption() throws IOException {
        loadMock("/mock_secret_empty.json");

        impl.processSecrets(sessionService.getCurrentSession(), api.getUserSecrets("admin").execute().body());
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(PasswordListUpdatedEvent.class);
        eventService.assertEventType(CategoryListUpdatedEvent.class);
        Assert.assertEquals(1,sessionService.getCurrentSession().getPasswordBeanList().size());
    }
    */

    @Test
    public void verifyEmptyObject() throws IOException, ParseException {
        List<PasswordBean> passwordBeanList = new ArrayList<>();


        Assert.assertNull(impl.parseSecret(sessionService.getCurrentSession(), "1", new JsonParser().parse("{}").getAsJsonObject(), passwordBeanList));

        Assert.assertEquals(0, passwordBeanList.size());
    }

    @Test
    public void verifyPasswordFields() throws IOException, ParseException {
        List<PasswordBean> passwordBeanList = new ArrayList<>();


        Assert.assertEquals(SecretType.PASSWORD, impl.parseSecret(sessionService.getCurrentSession(), "1", parseMockJson("/mock_sm_password.json"), passwordBeanList ));

        Assert.assertEquals(1,passwordBeanList.size());

        PasswordBean bean = passwordBeanList.get(0);

        Assert.assertTrue(bean.isDecrypted());
        Assert.assertEquals("https://www.gmail.com", bean.getAddress());
        Assert.assertEquals("1", bean.getId());
        Assert.assertEquals("myuser", bean.getLoginName());
        Assert.assertEquals("my notes", bean.getNotes());
        Assert.assertEquals("myPassword", bean.getPass());
        Assert.assertEquals("admin", bean.getUser_id());
        Assert.assertEquals("www.google.com", bean.getWebsite());
        Assert.assertEquals(2017, bean.getDateChanged().get(Calendar.YEAR));
        Assert.assertEquals(1, bean.getDateChanged().get(Calendar.MONTH));
        Assert.assertEquals(3, bean.getDateChanged().get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(10, bean.getLength());

    }

    @Test
    public void verifyInvalidTypesDontThrowExceptions() throws IOException, ParseException {
        List<PasswordBean> passwordBeanList = new ArrayList<>();


        Assert.assertEquals(SecretType.PASSWORD, impl.parseSecret(sessionService.getCurrentSession(), "1", parseMockJson("/mock_sm_password_bad_types.json"), passwordBeanList ));

        Assert.assertEquals(1,passwordBeanList.size());

        PasswordBean bean = passwordBeanList.get(0);

        Assert.assertNull(bean.getAddress());
        Assert.assertEquals("1", bean.getId());
        Assert.assertNull(bean.getLoginName());
        Assert.assertNull(bean.getNotes());
        Assert.assertNull(bean.getPass());
        Assert.assertEquals("admin", bean.getUser_id());
        Assert.assertNull(bean.getWebsite());
        Assert.assertNull(bean.getDateChanged());
        Assert.assertEquals(0, bean.getLength());

    }

    @Test
    public void verifyCategoryFields() throws IOException, ParseException {
        List<PasswordBean> passwordBeanList = new ArrayList<>();


        Assert.assertEquals(SecretType.PASSWORD_CATEGORY, impl.parseSecret(sessionService.getCurrentSession(), "1", parseMockJson("/mock_sm_categories.json"), passwordBeanList ));

        Assert.assertEquals(0,passwordBeanList.size());

        Assert.assertEquals(2,sessionService.getCurrentSession().getCategoryList().size());

        Category category1 = sessionService.getCurrentSession().getCategoryById("1");
        Assert.assertNotNull(category1);
        Assert.assertEquals("Finance", category1.getCategory_name());
        Assert.assertEquals("00FF00", category1.getCategory_colour());
        Assert.assertEquals("admin", category1.getUser_id());

        Category category2 = sessionService.getCurrentSession().getCategoryById("2");
        Assert.assertNotNull(category2);
        Assert.assertEquals("Communication", category2.getCategory_name());
        Assert.assertEquals("FF0000", category2.getCategory_colour());
        Assert.assertEquals("admin", category2.getUser_id());
    }

    @Test
    public void verifyAddCategoryToPasswordWithoutCategoryDoesNotFail() {
        PasswordBean pw = new PasswordBean();

        pw.setCategory(null);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Email","FFF000"));
        categories.add(new Category("2", "Games","000FFF"));
        sessionService.getCurrentSession().setCategoryList(categories);

        impl.addCategoryInfoToSinglePassword(sessionService.getCurrentSession(), pw);

        Assert.assertNull(pw.getCategoryName());
        Assert.assertEquals(0x00000000, pw.getCategoryBackground());
        Assert.assertEquals(0x00000000, pw.getCategoryForeground());
        Assert.assertNull(pw.getCategory());
    }

    @Test
    public void verifyAddMissingCategoryToPasswordWithCategoryDoesNotFail() {
        PasswordBean pw = new PasswordBean();

        pw.setCategory("1");

        impl.addCategoryInfoToSinglePassword(sessionService.getCurrentSession(), pw);

        Assert.assertNull(pw.getCategoryName());
        Assert.assertEquals(0x00000000, pw.getCategoryBackground());
        Assert.assertEquals(0x00000000, pw.getCategoryForeground());
        Assert.assertEquals("1", pw.getCategory());
    }

    @Test
    public void verifyAddCategoryToSinglePassword() {
        PasswordBean pw = new PasswordBean();

        pw.setCategory("2");

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Email","FFF000"));
        categories.add(new Category("2", "Games","000FFF"));
        sessionService.getCurrentSession().setCategoryList(categories);

        impl.addCategoryInfoToSinglePassword(sessionService.getCurrentSession(), pw);

        Assert.assertEquals("Games", pw.getCategoryName());
        Assert.assertEquals(0xFF000FFF, pw.getCategoryBackground());
        Assert.assertEquals(0xFFFFFFFF, pw.getCategoryForeground());
        Assert.assertEquals("2", pw.getCategory());
    }

    @Test
    public void verifyAddCategoryToAllPasswordsWithoutCategoryDoesNotFail() {
        List<PasswordBean> passwordBeanList = new ArrayList<>();
        PasswordBean pw1 = new PasswordBean();
        passwordBeanList.add(pw1);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Email","FFF000"));
        categories.add(new Category("2", "Games","000FFF"));
        sessionService.getCurrentSession().setCategoryList(categories);

        impl.addCategoryInfoToAllPasswords(sessionService.getCurrentSession(), passwordBeanList);

        Assert.assertNull(pw1.getCategoryName());
        Assert.assertEquals(0x00000000, pw1.getCategoryBackground());
        Assert.assertEquals(0x00000000, pw1.getCategoryForeground());
        Assert.assertNull(pw1.getCategory());
    }

    @Test
    public void verifyAddMissingCategoryToAllPasswordsWithCategoryDoesNotFail() {
        List<PasswordBean> passwordBeanList = new ArrayList<>();
        PasswordBean pw1 = new PasswordBean();
        pw1.setCategory("1");
        passwordBeanList.add(pw1);

        impl.addCategoryInfoToAllPasswords(sessionService.getCurrentSession(), passwordBeanList);

        Assert.assertNull(pw1.getCategoryName());
        Assert.assertEquals(0x00000000, pw1.getCategoryBackground());
        Assert.assertEquals(0x00000000, pw1.getCategoryForeground());
        Assert.assertEquals("1", pw1.getCategory());
    }

    @Test
    public void verifyAddCategoriesToPasswords() {
        List<PasswordBean> passwordBeanList = new ArrayList<>();
        PasswordBean pw1 = new PasswordBean();
        pw1.setCategory("2");
        passwordBeanList.add(pw1);

        PasswordBean pw2 = new PasswordBean();
        pw2.setCategory("1");
        passwordBeanList.add(pw2);

        PasswordBean pw3 = new PasswordBean();
        pw3.setCategory(null);
        passwordBeanList.add(pw3);

        PasswordBean pw4 = new PasswordBean();
        pw4.setCategory("4");
        passwordBeanList.add(pw4);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Email","FFF000"));
        categories.add(new Category("2", "Games","000FFF"));
        sessionService.getCurrentSession().setCategoryList(categories);

        impl.addCategoryInfoToAllPasswords(sessionService.getCurrentSession(), passwordBeanList);

        Assert.assertEquals("Games", pw1.getCategoryName());
        Assert.assertEquals(0xFF000FFF, pw1.getCategoryBackground());
        Assert.assertEquals(0xFFFFFFFF, pw1.getCategoryForeground());
        Assert.assertEquals("2", pw1.getCategory());

        Assert.assertEquals("Email", pw2.getCategoryName());
        Assert.assertEquals(0xFFFFF000, pw2.getCategoryBackground());
        Assert.assertEquals(0xFF000000, pw2.getCategoryForeground());
        Assert.assertEquals("1", pw2.getCategory());

        Assert.assertNull(pw3.getCategoryName());
        Assert.assertEquals(0x00000000, pw3.getCategoryBackground());
        Assert.assertEquals(0x00000000, pw3.getCategoryForeground());
        Assert.assertNull(pw3.getCategory());

        Assert.assertNull(pw4.getCategoryName());
        Assert.assertEquals(0x00000000, pw4.getCategoryBackground());
        Assert.assertEquals(0x00000000, pw4.getCategoryForeground());
        Assert.assertEquals("4", pw4.getCategory());

    }

    @Test
    public void verifyPasswordMixedCategoryMissingUser() throws IOException, ParseException {
        List<PasswordBean> passwordBeanList = new ArrayList<>();


        Assert.assertEquals(SecretType.PASSWORD, impl.parseSecret(sessionService.getCurrentSession(), "1", parseMockJson("/mock_sm_password_mixed_categories.json"), passwordBeanList ));

        Assert.assertEquals(1,passwordBeanList.size());

        PasswordBean bean = passwordBeanList.get(0);

        Assert.assertEquals("12",bean.getCategory());

    }

    @Test
    public void verifyPasswordMixedCategoryFoundUser() throws IOException, ParseException {
        List<PasswordBean> passwordBeanList = new ArrayList<>();


        sessionService.setUsername("myuser");
        sessionService.getCurrentSession().setUsername("myuser");
        Assert.assertEquals(SecretType.PASSWORD, impl.parseSecret(sessionService.getCurrentSession(), "1", parseMockJson("/mock_sm_password_mixed_categories.json"), passwordBeanList ));

        Assert.assertEquals(1,passwordBeanList.size());

        PasswordBean bean = passwordBeanList.get(0);

        Assert.assertEquals("13",bean.getCategory());

    }

    @Test
    public void verifyCreateSecretFromPasswordBean() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, ShortBufferException, NoSuchPaddingException, BadPaddingException, ParseException, InvalidKeySpecException, IllegalBlockSizeException {
        byte[] aesKey = encryptionService.keyExtend(sessionService.getUsername(), sessionService.getCurrentSession().getPassword());
        byte[] encryptedPrivateKey = encryptionService.decodeBase64(keyStorageService.getEncryptedPrivateKey());
        byte[] privateKey = encryptionService.decryptAES(aesKey, encryptedPrivateKey);
        String privateKeyPem = new String(privateKey,"ASCII");


        PasswordBean bean = new PasswordBean();
        bean.setWebsite("test.com");
        Secret secret = impl.createSecretFromPasswordBean(sessionService.getCurrentSession(),publicKey, bean);

        List<PasswordBean> list = new ArrayList<>(1);

        SecretType t = impl.parseSecret(sessionService.getCurrentSession(), privateKeyPem,list,"1", secret);
        Assert.assertEquals(SecretType.PASSWORD, t);

        // parseSecret should have added an item to the list
        Assert.assertEquals(1, list.size());
        PasswordBean other = list.get(0);

        // verify the fields were encrypted/decrypted correctly
        Assert.assertEquals(bean.getWebsite(), other.getWebsite());

        // make sure the object was actually recreated
        Assert.assertNotSame(bean, other);
    }


    private JsonObject parseMockJson(String filename) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IOUtils.copy(getClass().getResourceAsStream(filename),buffer);

        return new JsonParser().parse(buffer.toString("UTF-8")).getAsJsonObject();
    }
}
