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
package com.intirix.cloudpasswordmanager.services;

import android.content.Context;

import com.intirix.cloudpasswordmanager.R;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.events.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.beans.Category;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Adapter for the PasswordStorageService that uses an EventBus to notify the UI
 * Created by jeff on 6/29/16.
 */
public class PasswordRequestServiceImpl implements PasswordRequestService {

    private PasswordStorageService passwordStorageService;

    private SessionService sessionService;

    private EventService eventService;

    private ColorService colorService;

    private Context context;

    private boolean loginRunning = false;

    @Inject
    public PasswordRequestServiceImpl(Context context, SessionService sessionService, PasswordStorageService passwordStorageService, EventService eventService, ColorService colorService) {
        this.sessionService = sessionService;
        this.passwordStorageService = passwordStorageService;
        this.eventService = eventService;
        this.colorService = colorService;
        this.context = context;
    }

    @Override
    public void login() {
        final SessionInfo session = sessionService.getCurrentSession();
        loginRunning = true;
        passwordStorageService.getServerVersion(new VersionCallback() {
            @Override
            public void onReturn(String version) {
                session.setPasswordServerAppVersion(version);
                loginRunning = false;
                eventService.postEvent(new LoginSuccessfulEvent());
            }

            @Override
            public void onError(String message) {
                loginRunning = false;
                eventService.postEvent(new FatalErrorEvent(message));
            }
        });
    }

    @Override
    public boolean isLoginRunning() {
        return loginRunning;
    }

    @Override
    public void listCategories() {
        final SessionInfo session = sessionService.getCurrentSession();
        passwordStorageService.listCategories(new CategoryListCallback() {
            @Override
            public void onReturn(List<Category> categories) {
                session.setCategoryList(categories);
                updatePasswordBeanList(session);
                eventService.postEvent(new CategoryListUpdatedEvent());
            }

            @Override
            public void onError(String message) {
                sessionService.end();
                eventService.postEvent(new FatalErrorEvent(message));
            }
        });
    }

    @Override
    public void listPasswords() {
        final SessionInfo session = sessionService.getCurrentSession();
        passwordStorageService.listPasswords(new PasswordListCallback() {
            @Override
            public void onReturn(List<PasswordInfo> passwords) {
                session.setPasswordList(passwords);
                updatePasswordBeanList(session);
                eventService.postEvent(new PasswordListUpdatedEvent());
            }

            @Override
            public void onError(String message) {
                sessionService.end();
                eventService.postEvent(new FatalErrorEvent(message));
            }
        });
    }

    private void updatePasswordBeanList(SessionInfo session) {
        if (session.getCategoryList()!=null && session.getPasswordList()!=null) {
            List<PasswordBean> beans = new ArrayList<>(session.getPasswordList().size());

            // put all the categories in a map to make it easier to lookup
            final Map<String, Category> categoryMap = new HashMap<>();
            for (final Category category: session.getCategoryList()) {
                categoryMap.put(category.getId(), category);
            }

            for (final PasswordInfo passwordInfo: session.getPasswordList()) {
                final PasswordBean bean = new PasswordBean();

                bean.setId(passwordInfo.getId());
                bean.setUser_id(passwordInfo.getUser_id());
                bean.setAddress(passwordInfo.getAddress());
                bean.setDateChanged(passwordInfo.getDateChanged());
                bean.setHasLower(passwordInfo.isHasLower());
                bean.setHasNotes(passwordInfo.isHasNotes());
                bean.setHasSpecial(passwordInfo.isHasSpecial());
                bean.setHasNumber(passwordInfo.isHasNumber());
                bean.setHasUpper(passwordInfo.isHasUpper());
                bean.setLength(passwordInfo.getLength());
                bean.setStrength(passwordInfo.getStrength());
                bean.setLoginName(passwordInfo.getLoginName());
                bean.setNotes(passwordInfo.getNotes());
                bean.setPass(passwordInfo.getPass());
                bean.setDateChanged(passwordInfo.getDateChanged());
                bean.setWebsite(passwordInfo.getWebsite());

                if (categoryMap.containsKey(passwordInfo.getCategory())) {
                    bean.setCategory(passwordInfo.getCategory());
                    final Category category = categoryMap.get(bean.getCategory());
                    bean.setCategoryName(category.getCategory_name());
                    bean.setCategoryBackground(colorService.parseColor('#'+category.getCategory_colour()));
                    bean.setCategoryForeground(colorService.getTextColorForBackground(bean.getCategoryBackground()));
                } else {
                    bean.setCategoryName("");
                }

                if (bean.getWebsite()==null||bean.getWebsite().length()==0) {
                    if (bean.getAddress()==null||bean.getAddress().length()==0) {
                        bean.setWebsite(context.getString(R.string.example_website_empty));
                        bean.setWebsite("(No Website)");
                    } else {
                        bean.setWebsite(bean.getAddress());
                    }
                }

                beans.add(bean);
            }


            session.setPasswordBeanList(beans);
        }
    }
}
