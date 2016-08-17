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

import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.pages.passwordlist.CategoryListUpdatedEvent;
import com.intirix.cloudpasswordmanager.events.FatalErrorEvent;
import com.intirix.cloudpasswordmanager.pages.login.LoginSuccessfulEvent;
import com.intirix.cloudpasswordmanager.pages.passwordlist.PasswordListUpdatedEvent;
import com.intirix.cloudpasswordmanager.services.beans.Category;
import com.intirix.cloudpasswordmanager.services.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 6/29/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class)
public class PasswordRequestServiceImplUnitSpec {

    private MockEventService eventService;

    private MockSessionService sessionService;

    private MockPasswordStorageService passwordStorageService;

    private PasswordRequestServiceImpl impl;

    private ColorService colorService;

    @Before
    public void setUp() {
        eventService = new MockEventService();
        sessionService = new MockSessionService();
        sessionService.start();
        passwordStorageService = new MockPasswordStorageService();
        colorService = EasyMock.createMock(ColorService.class);

        impl = new PasswordRequestServiceImpl(RuntimeEnvironment.application, sessionService, passwordStorageService, eventService, colorService);
    }

    @Test
    public void verifySuccessfulLoginSendsLoginEvent() {
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertTrue(impl.isLoginRunning());
        passwordStorageService.getLastVersionCallback().onReturn("19");
        Assert.assertFalse(impl.isLoginRunning());
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, LoginSuccessfulEvent.class);
        Assert.assertEquals("19",sessionService.getCurrentSession().getPasswordServerAppVersion());
    }

    @Test
    public void verifyFailedLoginSendsFatalErrorMessage() {
        Assert.assertFalse(impl.isLoginRunning());
        impl.login();
        Assert.assertTrue(impl.isLoginRunning());
        passwordStorageService.getLastVersionCallback().onError("ERROR");
        Assert.assertFalse(impl.isLoginRunning());
        eventService.assertNumberOfPosts(1);
        eventService.assertEventType(0, FatalErrorEvent.class);
        Assert.assertEquals("ERROR", eventService.getEvent(0, FatalErrorEvent.class).getMessage());
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
        Assert.assertNull(sessionService.getCurrentSession().getPasswordList());
        Assert.assertNull(sessionService.getCurrentSession().getPasswordBeanList());
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
        Assert.assertNull(sessionService.getCurrentSession().getCategoryList());
        Assert.assertNull(sessionService.getCurrentSession().getPasswordBeanList());
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

    @Test
    public void verifySuccessfulPasswordListAndCategoryResponseUpdatesPasswordBeanList() {
        EasyMock.expect(colorService.parseColor("#FFFFFF")).andReturn(0xFFFFFFFF);
        EasyMock.expect(colorService.getTextColorForBackground(0xFFFFFFFF)).andReturn(0xFF000000);
        EasyMock.replay(colorService);


        impl.listPasswords();
        final List<PasswordInfo> list1 = new ArrayList<>();
        PasswordInfo p1 = new PasswordInfo();
        p1.setId("1");
        p1.setWebsite("www.github.com");
        p1.setCategory("2");
        list1.add(p1);

        impl.listCategories();
        final List<Category> list2 = new ArrayList<>();
        Category c1 = new Category();
        c1.setId("2");
        c1.setCategory_name("TEST");
        c1.setCategory_colour("FFFFFF");
        list2.add(c1);

        passwordStorageService.getLastPasswordListCallack().onReturn(list1);
        passwordStorageService.getLastCategoryListCallback().onReturn(list2);
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(0, PasswordListUpdatedEvent.class);
        eventService.assertEventType(1, CategoryListUpdatedEvent.class);

        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordBeanList());

        List<PasswordBean> beans = sessionService.getCurrentSession().getPasswordBeanList();
        PasswordBean bean1 = beans.get(0);
        Assert.assertEquals("1", bean1.getId());
        Assert.assertEquals("2", bean1.getCategory());
        Assert.assertEquals("TEST", bean1.getCategoryName());
        Assert.assertEquals(0xFFFFFFFF, bean1.getCategoryBackground());
        Assert.assertEquals(0xFF000000, bean1.getCategoryForeground());

        EasyMock.verify(colorService);
    }

    @Test
    public void verifyPasswordWithDeletedCategoryHasNoCategory() {
        EasyMock.replay(colorService);


        impl.listPasswords();
        final List<PasswordInfo> list1 = new ArrayList<>();
        PasswordInfo p1 = new PasswordInfo();
        p1.setId("1");
        p1.setWebsite("www.github.com");
        p1.setCategory("2");
        list1.add(p1);

        impl.listCategories();
        final List<Category> list2 = new ArrayList<>();
        Category c1 = new Category();
        c1.setId("3");
        c1.setCategory_name("TEST");
        c1.setCategory_colour("FFFFFF");
        list2.add(c1);

        passwordStorageService.getLastPasswordListCallack().onReturn(list1);
        passwordStorageService.getLastCategoryListCallback().onReturn(list2);
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(0, PasswordListUpdatedEvent.class);
        eventService.assertEventType(1, CategoryListUpdatedEvent.class);

        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordBeanList());

        List<PasswordBean> beans = sessionService.getCurrentSession().getPasswordBeanList();
        PasswordBean bean1 = beans.get(0);
        Assert.assertEquals("1", bean1.getId());
        Assert.assertNull("", bean1.getCategory());
        Assert.assertEquals("", bean1.getCategoryName());

        EasyMock.verify(colorService);
    }

    @Test
    public void verifyPasswordWithoutWebsiteGetsUrl() {
        EasyMock.replay(colorService);


        impl.listPasswords();
        final List<PasswordInfo> list1 = new ArrayList<>();
        PasswordInfo p1 = new PasswordInfo();
        p1.setId("1");
        p1.setCategory("2");
        p1.setAddress("www.github.com/wiki");
        list1.add(p1);

        impl.listCategories();
        final List<Category> list2 = new ArrayList<>();

        passwordStorageService.getLastPasswordListCallack().onReturn(list1);
        passwordStorageService.getLastCategoryListCallback().onReturn(list2);
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(0, PasswordListUpdatedEvent.class);
        eventService.assertEventType(1, CategoryListUpdatedEvent.class);

        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordBeanList());

        List<PasswordBean> beans = sessionService.getCurrentSession().getPasswordBeanList();
        PasswordBean bean1 = beans.get(0);
        Assert.assertEquals("1", bean1.getId());
        Assert.assertEquals("www.github.com/wiki", bean1.getWebsite());

        EasyMock.verify(colorService);
    }


    @Test
    public void verifyPasswordWithoutWebsiteOrUrlGetsPlaceholder() {
        EasyMock.replay(colorService);


        impl.listPasswords();
        final List<PasswordInfo> list1 = new ArrayList<>();
        PasswordInfo p1 = new PasswordInfo();
        p1.setId("1");
        p1.setCategory("2");
        list1.add(p1);

        impl.listCategories();
        final List<Category> list2 = new ArrayList<>();

        passwordStorageService.getLastPasswordListCallack().onReturn(list1);
        passwordStorageService.getLastCategoryListCallback().onReturn(list2);
        eventService.assertNumberOfPosts(2);
        eventService.assertEventType(0, PasswordListUpdatedEvent.class);
        eventService.assertEventType(1, CategoryListUpdatedEvent.class);

        Assert.assertNotNull(sessionService.getCurrentSession().getPasswordBeanList());

        List<PasswordBean> beans = sessionService.getCurrentSession().getPasswordBeanList();
        PasswordBean bean1 = beans.get(0);
        Assert.assertEquals("1", bean1.getId());
        Assert.assertEquals("(No Website)", bean1.getWebsite());

        EasyMock.verify(colorService);
    }

}
