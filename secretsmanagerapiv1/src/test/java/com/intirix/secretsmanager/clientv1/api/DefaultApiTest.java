package com.intirix.secretsmanager.clientv1.api;

import com.intirix.secretsmanager.clientv1.ApiClient;
import com.intirix.secretsmanager.clientv1.model.Secret;
import com.intirix.secretsmanager.clientv1.model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for DefaultApi
 */
public class DefaultApiTest {

    private DefaultApi api;

    @Before
    public void setup() {
        api = new ApiClient().createService(DefaultApi.class);
    }

    
    /**
     * Create a new secret
     *
     * 
     */
    @Test
    public void addSecretTest() {
        Secret secret = null;
        // DefinitionsSecret response = api.addSecret(secret);

        // TODO: test validations
    }
    
    /**
     * Adds a user
     *
     * 
     */
    @Test
    public void addUserTest() {
        String username = null;
        User user = null;
        // DefinitionsUser response = api.addUser(username, user);

        // TODO: test validations
    }
    
    /**
     * Generate a new keypair for a user
     *
     * 
     */
    @Test
    public void generateKeyPairTest() {
        String username = null;
        String password = null;
        String generate = null;
        // String response = api.generateKeyPair(username, password, generate);

        // TODO: test validations
    }
    
    /**
     * Get a secret
     *
     * 
     */
    @Test
    public void getSecretTest() {
        String sid = null;
        // DefinitionsSecret response = api.getSecret(sid);

        // TODO: test validations
    }
    
    /**
     * Get info about a user
     *
     * 
     */
    @Test
    public void getUserTest() {
        String username = null;
        // DefinitionsUser response = api.getUser(username);

        // TODO: test validations
    }
    
    /**
     * Get a user&#39;s encrypted private key
     *
     * 
     */
    @Test
    public void getUserEncryptedPrivateKeyTest() {
        String username = null;
        // String response = api.getUserEncryptedPrivateKey(username);

        // TODO: test validations
    }
    
    /**
     * Get a user&#39;s public key
     *
     * 
     */
    @Test
    public void getUserPublicKeyTest() {
        String username = null;
        // String response = api.getUserPublicKey(username);

        // TODO: test validations
    }
    
    /**
     * Get all of a user&#39;s encrypted secrets
     *
     * 
     */
    @Test
    public void getUserSecretsTest() {
        String username = null;
        // List<DefinitionsSecret> response = api.getUserSecrets(username);

        // TODO: test validations
    }
    
    /**
     * List all the users
     *
     * 
     */
    @Test
    public void listUsersTest() {
        // List<DefinitionsUser> response = api.listUsers();

        // TODO: test validations
    }
    
    /**
     * Set a user&#39;s encrypted private key
     *
     * 
     */
    @Test
    public void setUserEncryptedPrivateKeyTest() {
        String username = null;
        String key = null;
        // DefinitionsUser response = api.setUserEncryptedPrivateKey(username, key);

        // TODO: test validations
    }
    
    /**
     * Set a user&#39;s public key
     *
     * 
     */
    @Test
    public void setUserPublicKeyTest() {
        String username = null;
        String key = null;
        // DefinitionsUser response = api.setUserPublicKey(username, key);

        // TODO: test validations
    }
    
    /**
     * Update a secret
     *
     * 
     */
    @Test
    public void updateSecretTest() {
        String sid = null;
        Secret secret = null;
        // DefinitionsSecret response = api.updateSecret(sid, secret);

        // TODO: test validations
    }
    
    /**
     * Updates a user
     *
     * 
     */
    @Test
    public void updateUserTest() {
        String username = null;
        User user = null;
        // DefinitionsUser response = api.updateUser(username, user);

        // TODO: test validations
    }
    
}
