package com.intirix.secretsmanager.clientv1.api;

import com.intirix.secretsmanager.clientv1.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import com.intirix.secretsmanager.clientv1.model.Secret;
import com.intirix.secretsmanager.clientv1.model.SecretUserData;
import com.intirix.secretsmanager.clientv1.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DefaultApi {
  /**
   * Create a new secret
   * 
   * @param secret  (required)
   * @return Call&lt;Secret&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("secrets")
  Call<Secret> addSecret(
    @retrofit2.http.Body Secret secret
  );

  /**
   * Adds a user
   * 
   * @param username  (required)
   * @param user The user that is getting added (required)
   * @return Call&lt;User&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @POST("users/{username}")
  Call<User> addUser(
    @retrofit2.http.Path("username") String username, @retrofit2.http.Body User user
  );

  /**
   * Generate a new keypair for a user
   * 
   * @param username  (required)
   * @param password  (required)
   * @param generate  (required)
   * @return Call&lt;String&gt;
   */
  @Headers({
    "Content-Type:text/plain"
  })
  @POST("users/{username}/keys?generate={generate}")
  Call<String> generateKeyPair(
    @retrofit2.http.Path("username") String username, @retrofit2.http.Body String password, @retrofit2.http.Query("generate") String generate
  );

  /**
   * Get a secret
   * 
   * @param sid  (required)
   * @return Call&lt;Secret&gt;
   */
  @GET("secrets/{sid}")
  Call<Secret> getSecret(
    @retrofit2.http.Path("sid") String sid
  );

  /**
   * Get info about a user
   * 
   * @param username  (required)
   * @return Call&lt;User&gt;
   */
  @GET("users/{username}")
  Call<User> getUser(
    @retrofit2.http.Path("username") String username
  );

  /**
   * Get a user&#39;s encrypted private key
   * 
   * @param username  (required)
   * @return Call&lt;String&gt;
   */
  @GET("users/{username}/keys/private/encrypted")
  Call<String> getUserEncryptedPrivateKey(
    @retrofit2.http.Path("username") String username
  );

  /**
   * Get a user&#39;s public key
   * 
   * @param username  (required)
   * @return Call&lt;String&gt;
   */
  @GET("users/{username}/keys/public")
  Call<String> getUserPublicKey(
    @retrofit2.http.Path("username") String username
  );

  /**
   * Get all of a user&#39;s encrypted secrets
   * 
   * @param username  (required)
   * @return Call&lt;Map&lt;String, Secret&gt;&gt;
   */
  @GET("users/{username}/secrets")
  Call<Map<String, Secret>> getUserSecrets(
    @retrofit2.http.Path("username") String username
  );

  /**
   * List all the users
   * 
   * @return Call&lt;Map&lt;String, User&gt;&gt;
   */
  @GET("users")
  Call<Map<String, User>> listUsers();
    

  /**
   * Unshare a secret
   * 
   * @param sid  (required)
   * @param username  (required)
   * @return Call&lt;Secret&gt;
   */
  @DELETE("secrets/{sid}/users/{username}")
  Call<Secret> unshareSecret(
    @retrofit2.http.Path("sid") String sid, @retrofit2.http.Path("username") String username
  );

  /**
   * Set a user&#39;s encrypted private key
   * 
   * @param username  (required)
   * @param key  (required)
   * @return Call&lt;User&gt;
   */
  @Headers({
    "Content-Type:application/octet-stream"
  })
  @PUT("users/{username}/keys/private/encrypted")
  Call<User> setUserEncryptedPrivateKey(
    @retrofit2.http.Path("username") String username, @retrofit2.http.Body String key
  );

  /**
   * Set a user&#39;s public key
   * 
   * @param username  (required)
   * @param key  (required)
   * @return Call&lt;User&gt;
   */
  @Headers({
    "Content-Type:application/x-pem-file"
  })
  @PUT("users/{username}/keys/public")
  Call<User> setUserPublicKey(
    @retrofit2.http.Path("username") String username, @retrofit2.http.Body String key
  );

  /**
   * Share a secret
   * 
   * @param sid  (required)
   * @param username  (required)
   * @param userdata  (required)
   * @return Call&lt;Secret&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("secrets/{sid}/users/{username}")
  Call<Secret> shareSecret(
    @retrofit2.http.Path("sid") String sid, @retrofit2.http.Path("username") String username, @retrofit2.http.Body SecretUserData userdata
  );

  /**
   * Update a secret
   * 
   * @param sid  (required)
   * @param secret  (required)
   * @return Call&lt;Secret&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("secrets/{sid}")
  Call<Secret> updateSecret(
    @retrofit2.http.Path("sid") String sid, @retrofit2.http.Body Secret secret
  );

  /**
   * Updates a user
   * 
   * @param username  (required)
   * @param user The user that is getting updated (required)
   * @return Call&lt;User&gt;
   */
  @Headers({
    "Content-Type:application/json"
  })
  @PUT("users/{username}")
  Call<User> updateUser(
    @retrofit2.http.Path("username") String username, @retrofit2.http.Body User user
  );

}
