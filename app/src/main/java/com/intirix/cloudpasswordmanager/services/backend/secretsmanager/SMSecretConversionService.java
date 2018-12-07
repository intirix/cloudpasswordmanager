package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.secretsmanager.clientv1.model.Secret;

import java.io.IOException;
import java.util.Map;

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
     * Create a new secret object from the password bean
     * @param bean
     * @return
     * @throws IOException
     */
    public Secret createSecretFromPasswordBean(SessionInfo session, String publicKeyPem, PasswordBean bean) throws IOException;
}
