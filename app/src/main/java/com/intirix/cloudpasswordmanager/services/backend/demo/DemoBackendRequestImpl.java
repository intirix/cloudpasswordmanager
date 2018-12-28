package com.intirix.cloudpasswordmanager.services.backend.demo;

import android.content.Context;

import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordsLoadedEvent;
import com.intirix.cloudpasswordmanager.services.backend.BackendRequestInterface;
import com.intirix.cloudpasswordmanager.services.backend.beans.Category;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.backend.ocp.PasswordStorageService;
import com.intirix.cloudpasswordmanager.services.session.SessionInfo;
import com.intirix.cloudpasswordmanager.services.session.SessionService;
import com.intirix.cloudpasswordmanager.services.ui.ColorService;
import com.intirix.cloudpasswordmanager.services.ui.EventService;

import java.util.Collections;

import javax.inject.Inject;

public class DemoBackendRequestImpl implements BackendRequestInterface {

    private SessionService sessionService;

    private EventService eventService;

    @Inject
    public DemoBackendRequestImpl(SessionService sessionService, EventService eventService) {
        this.sessionService = sessionService;
        this.eventService = eventService;
    }


    @Override
    public void login() {
        eventService.postEvent(new LoginSuccessfulEvent());
    }

    @Override
    public boolean supportsUrl() {
        return false;
    }

    @Override
    public boolean supportsUsername() {
        return false;
    }

    @Override
    public boolean supportsPassword() {
        return false;
    }

    @Override
    public boolean supportsCustomKey() {
        return false;
    }

    @Override
    public boolean isLoginRunning() {
        return false;
    }

    @Override
    public boolean isCrudRunning() {
        return false;
    }

    @Override
    public void listCategories() {
        final SessionInfo session = sessionService.getCurrentSession();
        session.setCategoryList(Collections.<Category>emptyList());
        eventService.postEvent(new CategoryListUpdatedEvent());
    }

    @Override
    public void listPasswords() {
        final SessionInfo session = sessionService.getCurrentSession();
        session.setPasswordBeanList(Collections.<PasswordBean>emptyList());
        eventService.postEvent(new PasswordListUpdatedEvent());
        eventService.postEvent(new PasswordsLoadedEvent());
    }

    @Override
    public void listUsers() {

    }
}
