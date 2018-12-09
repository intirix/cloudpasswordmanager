package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.secretsmanager.clientv1.model.Secret;
import com.intirix.secretsmanager.clientv1.model.SecretUserData;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

/**
 * Created by jeff on 7/19/17.
 */

public interface SMSecretConversionService {

    /**
     * Process all the secrets into the session
     * @param session
     * @param response
     * @throws IOException
     */
    public void processSecrets(SessionInfo session, Map<String,Secret> response) throws IOException;

    /**
     * Update the session with the secret
     * @param session
     * @param secret
     * @throws IOException
     */
    public void updateSecret(SessionInfo session, Secret secret) throws IOException;

    /**
     * Create a new secret object from the password bean
     * @param bean
     * @return
     * @throws IOException
     */
    public Secret createSecretFromPasswordBean(SessionInfo session, String publicKeyPem, PasswordBean bean) throws IOException;

    /**
     * Get the AES256 key for the secret
     * @param session
     * @param secret
     * @return
     * @throws IOException
     */
    public byte[] getKeyForSecret(SessionInfo session, Secret secret) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, InvalidKeySpecException, ShortBufferException, BadPaddingException;

    /**
     * Create the user data for a user
     * @param session
     * @param secret
     * @param user
     * @param publicKeyPem
     * @param secretKeyPair
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     */
    public SecretUserData createUserData(SessionInfo session, Secret secret, String user, String publicKeyPem, byte[] secretKeyPair) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException;
}
