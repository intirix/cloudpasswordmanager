package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.JsonReader;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.ocp.PasswordRestService;
import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
import com.intirix.cloudpasswordmanager.services.ui.ColorService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;
import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.api.DefaultApi;
import com.intirix.secretsmanager.clientv1.model.Secret;
import com.intirix.secretsmanager.clientv1.model.SecretUserData;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jeff on 7/19/17.
 */

public class SMSecretConversionServiceImpl implements SMSecretConversionService {

    private static final String TAG = SMSecretConversionServiceImpl.class.getSimpleName();

    private EventService eventService;

    private SMEncryptionService encryptionService;

    private SessionService sessionService;

    private KeyStorageService keyStorageService;

    private ColorService colorService;

    @Inject
    public SMSecretConversionServiceImpl(SessionService sessionService, EventService eventService, SMEncryptionService encryptionService, KeyStorageService keyStorageService, ColorService colorService) {
        this.sessionService = sessionService;
        this.eventService = eventService;
        this.encryptionService = encryptionService;
        this.keyStorageService = keyStorageService;
        this.colorService = colorService;
    }

    @Override
    public void processSecrets(SessionInfo session, Map<String, Secret> response) throws IOException {
        final long t1 = System.currentTimeMillis();
        long loop = 0;

        try {
            if (keyStorageService.isPrivateKeyStored()) {
                byte[] aesKey = encryptionService.keyExtend(sessionService.getUsername(), session.getPassword());
                byte[] encryptedPrivateKey = encryptionService.decodeBase64(keyStorageService.getEncryptedPrivateKey());
                byte[] privateKey = encryptionService.decryptAES(aesKey, encryptedPrivateKey);
                String privateKeyPem = new String(privateKey,"ASCII");

                List<PasswordBean> passwordBeanList = new ArrayList<>();

                for (final String sid: response.keySet()) {
                    final Secret secret = response.get(sid);
                    final long t2 = System.currentTimeMillis();
                    Map<String, Object> map = (Map<String, Object>) secret.getUsers();
                    Object obj = map.get(sessionService.getUsername());

                    byte[] encryptedKey = getEncryptedKey(obj);

                    byte[] secretData = decryptSecret(privateKeyPem, secret, encryptedKey);

                    String secretString = new String(secretData, "UTF-8");
                    JsonElement topElement = new JsonParser().parse(secretString);

                    SecretType parsedType = parseSecret(session, sid, topElement.getAsJsonObject(), passwordBeanList);

                    // if we parsed the password categories, then backfill to the already parsed passwords
                    if (SecretType.PASSWORD_CATEGORY.equals(parsedType)) {
                        addCategoryInfoToAllPasswords(session, passwordBeanList);
                        eventService.postEvent(new CategoryListUpdatedEvent());
                    }
                    final long t3 = System.currentTimeMillis();
                    final long dt_decrypt = t3-t2;
                    final long dt_elapsed = t3-t1;

                    loop += dt_decrypt;
                    if (loop>1000) {
                        session.setPasswordBeanList(new ArrayList<PasswordBean>(passwordBeanList));

                        eventService.postEvent(new PasswordListUpdatedEvent());
                        loop = 0;
                    }

                    Log.d(TAG, "Decrypted secret, decryption="+dt_decrypt+"ms, elapsed="+dt_elapsed+"ms");
                }

                session.setPasswordBeanList(passwordBeanList);

                eventService.postEvent(new CategoryListUpdatedEvent());
                eventService.postEvent(new PasswordListUpdatedEvent());
            } else {
                eventService.postEvent(new FatalErrorEvent("Decryption key not available"));
            }
        } catch (Exception e){
            Log.w(TAG,"Failed to decrypt secrets", e);
            throw new IOException(e);
        }

    }

    protected SecretType parseSecret(SessionInfo session, String sid, JsonObject topElement, List<PasswordBean> passwordBeanList) throws ParseException{
        if (topElement.has("type")) {
            String type = topElement.get("type").getAsString();
            if ("password".equals(type)) {
                PasswordBean passwordBean = parseSinglePassword(sid, topElement, passwordBeanList);
                addCategoryInfoToSinglePassword(session, passwordBean);

                return SecretType.PASSWORD;
            } else if ("passwordCategories".equals(type)) {
                if (session.getCategoryList()==null) {
                    session.setCategoryList(new ArrayList<Category>());
                }

                final List<Category> categories = new ArrayList<>(session.getCategoryList());

                if (topElement.has("categories") && topElement.get("categories").isJsonObject()) {
                    JsonObject categoriesObject = topElement.get("categories").getAsJsonObject();

                    for (Map.Entry<String, JsonElement> entry: categoriesObject.entrySet()) {
                        parseSingleCategory(categories, entry);
                    }
                }

                session.setCategoryList(categories);


                return SecretType.PASSWORD_CATEGORY;
            }
            Log.d(TAG, "Unknown type: "+type);
        } else {
            Log.d(TAG, "Missing type");
        }

        return null;
    }

    protected void addCategoryInfoToSinglePassword(SessionInfo session, PasswordBean passwordBean) {
        Category cat = session.getCategoryById(passwordBean.getCategory());
        if (cat!=null) {
            passwordBean.setCategoryName(cat.getCategory_name());
            passwordBean.setCategoryBackground(colorService.parseColor('#'+cat.getCategory_colour()));
            passwordBean.setCategoryForeground(colorService.getTextColorForBackground(passwordBean.getCategoryBackground()));
        }
    }

    protected void addCategoryInfoToAllPasswords(SessionInfo session, List<PasswordBean> passwordList) {
        // get a map of categories, because it is easier to work with and faster
        final Map<String, Category> categoryMap = new HashMap<>();
        if (session.getCategoryList()!=null) {
            for (final Category cat : session.getCategoryList()) {
                categoryMap.put(cat.getId(), cat);
            }
        }

        if (passwordList!=null) {
            for (final PasswordBean passwordBean: passwordList) {
                if (passwordBean.getCategory()!=null && categoryMap.containsKey(passwordBean.getCategory())) {
                    final Category cat = categoryMap.get(passwordBean.getCategory());

                    passwordBean.setCategoryName(cat.getCategory_name());
                    passwordBean.setCategoryBackground(colorService.parseColor('#'+cat.getCategory_colour()));
                    passwordBean.setCategoryForeground(colorService.getTextColorForBackground(passwordBean.getCategoryBackground()));
                }
            }
        }

    }

    private PasswordBean parseSinglePassword(String sid, JsonObject topElement, List<PasswordBean> passwordBeanList) throws ParseException {
        PasswordBean bean = new PasswordBean();

        if (topElement.has("address") && topElement.get("address").isJsonPrimitive()) {
            bean.setAddress(topElement.get("address").getAsString());
        }

        if (topElement.has("website") && topElement.get("website").isJsonPrimitive()) {
            bean.setWebsite(topElement.get("website").getAsString());
        }

        if (topElement.has("loginName") && topElement.get("loginName").isJsonPrimitive()) {
            bean.setLoginName(topElement.get("loginName").getAsString());
        }

        if (topElement.has("notes") && topElement.get("notes").isJsonPrimitive()) {
            bean.setNotes(topElement.get("notes").getAsString());
        }

        if (topElement.has("password") && topElement.get("password").isJsonPrimitive()) {
            bean.setPass(topElement.get("password").getAsString());
            bean.setLength(bean.getPass().length());
        }

        if (topElement.has("dateChanged") && topElement.get("dateChanged").isJsonPrimitive()) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            c.setTime(sdf.parse(topElement.get("dateChanged").getAsString()));
            bean.setDateChanged(c);
        }

        bean.setUser_id(sessionService.getUsername());
        bean.setId(sid);
        bean.setDecrypted(true);

        passwordBeanList.add(bean);

        return bean;
    }

    private void parseSingleCategory(List<Category> categories, Map.Entry<String, JsonElement> entry) {
        // make sure it is an object
        if (entry.getValue().isJsonObject()) {
            final Category category = new Category();

            category.setId(entry.getKey());
            category.setUser_id(sessionService.getUsername());

            JsonObject categoryObject = entry.getValue().getAsJsonObject();

            if (categoryObject.has("label") && categoryObject.get("label").isJsonPrimitive()) {
                category.setCategory_name(categoryObject.get("label").getAsString());
            }

            if (categoryObject.has("backgroundColor") && categoryObject.get("backgroundColor").isJsonPrimitive()) {
                category.setCategory_colour(categoryObject.get("backgroundColor").getAsString().replace("#",""));
            }

            if (category.getCategory_colour()!=null && category.getCategory_colour()!=null) {
                categories.add(category);
            }
        }
    }

    private byte[] decryptSecret(String privateKeyPem, Secret secret, byte[] encryptedKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ShortBufferException, NoSuchProviderException {
        byte[] secretKey = encryptionService.decryptRSA(privateKeyPem, encryptedKey);

        byte[] encryptedSecret = encryptionService.decodeBase64(secret.getEncryptedSecret());

        return encryptionService.decryptAES(secretKey, encryptedSecret);
    }

    private byte[] getEncryptedKey(Object obj) throws IOException {
        byte[] encryptedKey;

        if (obj instanceof SecretUserData) {
            SecretUserData sud = (SecretUserData) obj;
            encryptedKey = encryptionService.decodeBase64(sud.getEncryptedKey());
        } else {
            Map<String, Object> sud = (Map<String, Object>) obj;
            encryptedKey = encryptionService.decodeBase64(sud.get("encryptedKey").toString());
        }
        return encryptedKey;
    }
}
