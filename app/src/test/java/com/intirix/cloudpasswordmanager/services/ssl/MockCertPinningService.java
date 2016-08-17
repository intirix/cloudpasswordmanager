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

import com.intirix.cloudpasswordmanager.services.ssl.CertPinningService;

/**
 * Created by jeff on 8/11/16.
 */
public class MockCertPinningService implements CertPinningService {
    private boolean enabled;

    private boolean valid;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void disable() {

    }

    @Override
    public void pin(String url) {

    }

    @Override
    public boolean isPinRequestRunning() {
        return false;
    }
}
