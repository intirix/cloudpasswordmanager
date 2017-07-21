package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import android.util.JsonReader;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intirix.cloudpasswordmanager.pages.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.ocp.PasswordRestService;
import com.intirix.cloudpasswordmanager.services.session.AuthenticationInterceptor;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.settings.KeyStorageService;
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
import java.util.ArrayList;
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

    @Inject
    public SMSecretConversionServiceImpl(SessionService sessionService, EventService eventService, SMEncryptionService encryptionService, KeyStorageService keyStorageService) {
        this.sessionService = sessionService;
        this.eventService = eventService;
        this.encryptionService = encryptionService;
        this.keyStorageService = keyStorageService;
    }

    @Override
    public void processSecrets(SessionInfo session, Map<String, Secret> response) throws IOException {

        try {
            if (keyStorageService.isPrivateKeyStored()) {
                byte[] aesKey = encryptionService.keyExtend(sessionService.getUsername(), sessionService.getCurrentSession().getPassword());
                byte[] encryptedPrivateKey = encryptionService.decodeBase64(keyStorageService.getEncryptedPrivateKey());
                byte[] privateKey = encryptionService.decryptAES(aesKey, encryptedPrivateKey);
                String privateKeyPem = new String(privateKey,"ASCII");

                List<PasswordBean> passwordBeanList = new ArrayList<>();

                for (final Secret secret : response.values()) {
                    Map<String, Object> map = (Map<String, Object>) secret.getUsers();
                    Object obj = map.get(sessionService.getUsername());

                    byte[] encryptedKey = getEncryptedKey(obj);

                    byte[] secretData = decryptSecret(privateKeyPem, secret, encryptedKey);

                    String secretString = new String(secretData, "UTF-8");
                    System.out.println(secretString);
                    JsonElement topElement = new JsonParser().parse(secretString);

                    parseSecret(session, secret.getSid(), topElement.getAsJsonObject(), passwordBeanList);
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

    protected void parseSecret(SessionInfo session, String sid, JsonObject topElement, List<PasswordBean> passwordBeanList) {
        if (topElement.has("type") && "password".equals(topElement.get("type").getAsString())) {
            PasswordBean bean = new PasswordBean();

            passwordBeanList.add(bean);
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
