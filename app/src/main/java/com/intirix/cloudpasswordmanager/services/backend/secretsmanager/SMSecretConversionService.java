package com.intirix.cloudpasswordmanager.services.backend.secretsmanager;

import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.secretsmanager.clientv1.model.Secret;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jeff on 7/19/17.
 */

public interface SMSecretConversionService {

    public void processSecrets(SessionInfo session, Map<String,Secret> response) throws IOException;
}
