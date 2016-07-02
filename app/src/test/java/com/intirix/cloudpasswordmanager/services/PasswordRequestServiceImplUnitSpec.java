package com.intirix.cloudpasswordmanager.services;

import com.intirix.cloudpasswordmanager.events.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.events.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.events.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.events.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.beans.Category;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/29/16.
 */
public class PasswordRequestServiceImplUnitSpec {

    private MockEventService eventService;

    private MockSessionService sessionService;

    private MockPasswordStorageService passwordStorageService;

    private PasswordRequestServiceImpl impl;

    @Before
    public void setUp() {
        eventService = new MockEventService();
        sessionService = new MockSessionService();
        sessionService.start();
        passwordStorageService = new MockPasswordStorageService();

        impl = new PasswordRequestServiceImpl(sessionService, passwordStorageService, eventService);
    }

    @Test
    public void verifySuccessfulLoginSendsLoginEvent() {
        impl.login();
        passwordStorageService.getLastVersionCallback().onReturn("19");
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        Assert.assertEquals("19",sessionService.getCurrentSession().getPasswordServerAppVersion());
    }

    @Test
    public void verifyFailedLoginSendsFatalErrorMessage() {
        impl.login();
        passwordStorageService.getLastVersionCallback().onError("ERROR");
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);
        Assert.assertEquals("ERROR", eventService.getEvent(0, FatalErrorEvent.class).getMessage());
        Assert.assertNull(sessionService.getCurrentSession().getPasswordServerAppVersion());
    }

    @Test
    public void verifySuccessfulCategoryListResponseSendsCategoryListUpdatedEvent() {
        impl.listCategories();
        final List<Category> list = new ArrayList<>();
        Category c1 = new Category();
        c1.setCategory_name("TEST");
        list.add(c1);

        passwordStorageService.getLastCategoryListCallback().onReturn(list);
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, CategoryListUpdatedEvent.class);
        Assert.assertEquals(list, sessionService.getCurrentSession().getCategoryList());
    }

    @Test
    public void verifyFailedCategoryListResponseSendsFatalErrorEvent() {
        final List<Category> list = new ArrayList<>();
        Category c1 = new Category();
        c1.setCategory_name("TEST");
        list.add(c1);
        sessionService.getCurrentSession().setCategoryList(list);

        impl.listCategories();

        passwordStorageService.getLastCategoryListCallback().onError("ERROR");
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);
        Assert.assertEquals("ERROR", eventService.getEvent(0, FatalErrorEvent.class).getMessage());
        Assert.assertNull("Session did not terminate when fatal error occurred", sessionService.getCurrentSession());
    }

    @Test
    public void verifySuccessfulPasswordListResponseSendsPasswordListUpdatedEvent() {
        impl.listPasswords();
        final List<PasswordInfo> list = new ArrayList<>();
        PasswordInfo p1 = new PasswordInfo();
        p1.setId("1");
        list.add(p1);

        passwordStorageService.getLastPasswordListCallack().onReturn(list);
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, PasswordListUpdatedEvent.class);
        Assert.assertEquals(list, sessionService.getCurrentSession().getPasswordList());
    }

    @Test
    public void verifyFailedPasswordListResponseSendsFatalErrorEvent() {
        impl.listPasswords();
        final List<PasswordInfo> list = new ArrayList<>();
        PasswordInfo p1 = new PasswordInfo();
        p1.setId("1");
        list.add(p1);

        impl.listCategories();

        passwordStorageService.getLastPasswordListCallack().onError("ERROR");
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);
        Assert.assertEquals("ERROR", eventService.getEvent(0, FatalErrorEvent.class).getMessage());
        Assert.assertNull("Session did not terminate when fatal error occurred", sessionService.getCurrentSession());
    }

}
