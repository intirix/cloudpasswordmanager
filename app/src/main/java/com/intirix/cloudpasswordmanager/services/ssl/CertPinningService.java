/*
 * Copyright (C) 2016 Jeff Mercer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intirix.cloudpasswordmanager.services.ssl;

/**
 * Created by jeff on 8/8/16.
 */
public interface CertPinningService {

    /**
     * Initialize the cert pinning service
     */
    public void init();

    /**
     * Returns true if the certificate is pinned
     * @return
     */
    public boolean isEnabled();

    /**
     * Is the pinned cert a valid CA-signed cert
     * @return
     */
    public boolean isValid();

    /**
     * Disable the certificate pinning
     */
    public void disable();

    /**
     * Pin the certificate that is currently used on the url
     * @param url
     */
    public void pin(String url);

    /**
     * Is the Pin request running
     * @return
     */
    public boolean isPinRequestRunning();
}
