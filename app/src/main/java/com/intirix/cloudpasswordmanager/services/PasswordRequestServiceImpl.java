package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.events.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.events.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.events.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.events.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.beans.Category;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.beans.SessionInfo;
import com.intirix.cloudpasswordmanager.services.callbacks.CategoryListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.PasswordListCallback;
import com.intirix.cloudpasswordmanager.services.callbacks.VersionCallback;

import java.util.List;

import javax.inject.Inject;

/**
 * Adapter for the PasswordStorageService that uses an EventBus to notify the UI
 * Created by jeff on 6/29/16.
 */
public class PasswordRequestServiceImpl implements PasswordRequestService {

    private PasswordStorageService passwordStorageService;

    private SessionService sessionService;

    private EventService eventService;

    @Inject
    public PasswordRequestServiceImpl(SessionService sessionService, PasswordStorageService passwordStorageService, EventService eventService) {
        this.sessionService = sessionService;
        this.passwordStorageService = passwordStorageService;
        this.eventService = eventService;
    }

    @Override
    public void login() {
        final SessionInfo session = sessionService.getCurrentSession();
        passwordStorageService.getServerVersion(new VersionCallback() {
            @Override
            public void onReturn(String version) {
                session.setPasswordServerAppVersion(version);
                eventService.postEvent(new LoginSuccessfulEvent());
            }

            @Override
            public void onError(String message) {
                eventService.postEvent(new FatalErrorEvent(message));
            }
        });
    }

    @Override
    public void listCategories() {
        final SessionInfo session = sessionService.getCurrentSession();
        passwordStorageService.listCategories(new CategoryListCallback() {
            @Override
            public void onReturn(List<Category> categories) {
                session.setCategoryList(categories);
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
                eventService.postEvent(new PasswordListUpdatedEvent());
            }

            @Override
            public void onError(String message) {
                sessionService.end();
                eventService.postEvent(new FatalErrorEvent(message));
            }
        });
    }
}
