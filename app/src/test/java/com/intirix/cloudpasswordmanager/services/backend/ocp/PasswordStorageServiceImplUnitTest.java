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
package com.intirix.cloudpasswordmanager.services.backend.ocp;

import android.content.Context;

import com.google.gson.JsonSyntaxException;
import com.ibm.icu.text.SimpleDateFormat;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.backend.ocp.beans.PasswordResponse;
import com.intirix.cloudpasswordmanager.services.session.MockSessionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.ParseException;

/**
 * Unit tests for the PasswordStorageServiceImpl class
 * These tests are not based on the behaviors of the class.
 * They are based on the implementation of the internals of
 * the class
 * Created by jeff on 6/28/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestPasswordApplication.class, sdk = 23)

public class PasswordStorageServiceImplUnitTest {

    private Context context;

    private MockSessionService sessionService;

    private PasswordStorageServiceImpl impl;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
        sessionService = new MockSessionService();

        impl = new PasswordStorageServiceImpl(context, sessionService, null);
    }

    /**
     * Test creation of the PasswordInfo bean without any properties
     */
    @Test
    public void testCreatePasswordInfoWithNoProperties() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(false);
        pr.setProperties(null);

        final PasswordInfo pi = impl.createPasswordInfo(pr);
        Assert.assertEquals(pr.getId(), pi.getId());
        Assert.assertEquals(pr.getUser_id(), pi.getUser_id());
        Assert.assertEquals(pr.getPass(), pi.getPass());
        Assert.assertEquals(pr.isNotes(), pi.isHasNotes());
    }

    /**
     * Test creation of the PasswordInfo bean without any properties
     */
    @Test
    public void testCreatePasswordInfoWithEmptyProperties() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(false);
        pr.setProperties("");

        final PasswordInfo pi = impl.createPasswordInfo(pr);
        Assert.assertEquals(pr.getId(), pi.getId());
        Assert.assertEquals(pr.getUser_id(), pi.getUser_id());
        Assert.assertEquals(pr.getPass(), pi.getPass());
        Assert.assertEquals(pr.isNotes(), pi.isHasNotes());
    }

    /**
     * Test creation of the PasswordInfo bean with properties
     */
    @Test
    public void testCreatePasswordInfoWithProperties() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(false);
        pr.setProperties("\"loginname\" : \"joebob2\", \"address\" : \"www.github.com/joe\", \"strength\" : \"2\", \"length\" : \"8\", \"lower\" : \"1\", \"upper\" : \"0\", \"number\" : \"0\", \"special\" : \"0\", \"category\" : \"1\", \"datechanged\" : \"2016-06-28\", \"notes\" : \"notes\"");

        final PasswordInfo pi = impl.createPasswordInfo(pr);
        Assert.assertEquals(pr.getId(), pi.getId());
        Assert.assertEquals(pr.getUser_id(), pi.getUser_id());
        Assert.assertEquals(pr.getPass(), pi.getPass());
        Assert.assertEquals(pr.isNotes(), pi.isHasNotes());

        Assert.assertEquals("joebob2", pi.getLoginName());
        Assert.assertEquals("www.github.com/joe", pi.getAddress());
        Assert.assertEquals(2, pi.getStrength());
        Assert.assertEquals(8, pi.getLength());
        Assert.assertTrue(pi.isHasLower());
        Assert.assertFalse(pi.isHasUpper());
        Assert.assertFalse(pi.isHasNumber());
        Assert.assertFalse(pi.isHasSpecial());
        Assert.assertEquals("1", pi.getCategory());
        Assert.assertEquals("2016-06-28", new SimpleDateFormat("yyyy-MM-dd").format(pi.getDateChanged().getTime()));
        Assert.assertEquals("notes", pi.getNotes());
    }

    @Test
    public void testCreatePasswordInfoWithNotes() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(true);
        pr.setProperties("\"loginname\" : \"joebob2\", \"address\" : \"www.github.com/joe\", \"strength\" : \"2\", \"length\" : \"8\", \"lower\" : \"1\", \"upper\" : \"0\", \"number\" : \"0\", \"special\" : \"0\", \"category\" : \"1\", \"datechanged\" : \"2016-06-28\", \"notes\" : \"my notes\"");

        final PasswordInfo pi = impl.createPasswordInfo(pr);
        Assert.assertEquals(pr.getId(), pi.getId());
        Assert.assertEquals(pr.getUser_id(), pi.getUser_id());
        Assert.assertEquals(pr.getPass(), pi.getPass());
        Assert.assertEquals(pr.isNotes(), pi.isHasNotes());

        Assert.assertEquals("joebob2", pi.getLoginName());
        Assert.assertEquals("www.github.com/joe", pi.getAddress());
        Assert.assertEquals(2, pi.getStrength());
        Assert.assertEquals(8, pi.getLength());
        Assert.assertTrue(pi.isHasLower());
        Assert.assertFalse(pi.isHasUpper());
        Assert.assertFalse(pi.isHasNumber());
        Assert.assertFalse(pi.isHasSpecial());
        Assert.assertEquals("1", pi.getCategory());
        Assert.assertEquals("2016-06-28", new SimpleDateFormat("yyyy-MM-dd").format(pi.getDateChanged().getTime()));
        Assert.assertEquals("my notes", pi.getNotes());
    }


    @Test
    public void testCreatePasswordInfoWithBackslashInNotes() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(true);
        pr.setProperties("\"loginname\" : \"joebob2\", \"address\" : \"www.github.com/joe\", \"strength\" : \"2\", \"length\" : \"8\", \"lower\" : \"1\", \"upper\" : \"0\", \"number\" : \"0\", \"special\" : \"0\", \"category\" : \"1\", \"datechanged\" : \"2016-06-28\", \"notes\" : \"A \\\\ B\"");

        final PasswordInfo pi = impl.createPasswordInfo(pr);
        Assert.assertEquals(pr.getId(), pi.getId());
        Assert.assertEquals(pr.getUser_id(), pi.getUser_id());
        Assert.assertEquals(pr.getPass(), pi.getPass());
        Assert.assertEquals(pr.isNotes(), pi.isHasNotes());

        Assert.assertEquals("joebob2", pi.getLoginName());
        Assert.assertEquals("www.github.com/joe", pi.getAddress());
        Assert.assertEquals(2, pi.getStrength());
        Assert.assertEquals(8, pi.getLength());
        Assert.assertTrue(pi.isHasLower());
        Assert.assertFalse(pi.isHasUpper());
        Assert.assertFalse(pi.isHasNumber());
        Assert.assertFalse(pi.isHasSpecial());
        Assert.assertEquals("1", pi.getCategory());
        Assert.assertEquals("2016-06-28", new SimpleDateFormat("yyyy-MM-dd").format(pi.getDateChanged().getTime()));
        Assert.assertEquals("A \\ B", pi.getNotes());
    }

    //@Test - new behavior, now we flag the item as a decryption failure
    public void testCreatePasswordInfoWithBadBackslashInNotes() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(true);
        pr.setProperties("\"loginname\" : \"joebob2\", \"address\" : \"www.github.com/joe\", \"strength\" : \"2\", \"length\" : \"8\", \"lower\" : \"1\", \"upper\" : \"0\", \"number\" : \"0\", \"special\" : \"0\", \"category\" : \"1\", \"datechanged\" : \"2016-06-28\", \"notes\" : \"A \\ B\"");

        try {
            final PasswordInfo pi = impl.createPasswordInfo(pr);
            Assert.fail("Expected exception");
        } catch (JsonSyntaxException e) {
            // pass
        }
    }

    @Test
    public void testCreatePasswordInfoApi21() throws ParseException {
        final PasswordResponse pr = new PasswordResponse();
        pr.setId("1");
        pr.setUser_id("user1");
        pr.setWebsite("website");
        pr.setPass("password");
        pr.setDeleted("0");
        pr.setNotes(true);
        pr.setProperties("{\"loginname\" : \"joebob2\", \"address\" : \"www.github.com/joe\", \"strength\" : \"2\", \"length\" : \"8\", \"lower\" : \"1\", \"upper\" : \"0\", \"number\" : \"0\", \"special\" : \"0\", \"category\" : \"1\", \"datechanged\" : \"2016-06-28\", \"notes\" : \"A \\\\ B\"}");

        final PasswordInfo pi = impl.createPasswordInfo(pr);
        Assert.assertEquals(pr.getId(), pi.getId());
        Assert.assertEquals(pr.getUser_id(), pi.getUser_id());
        Assert.assertEquals(pr.getPass(), pi.getPass());
        Assert.assertEquals(pr.isNotes(), pi.isHasNotes());

        Assert.assertEquals("joebob2", pi.getLoginName());
        Assert.assertEquals("www.github.com/joe", pi.getAddress());
        Assert.assertEquals(2, pi.getStrength());
        Assert.assertEquals(8, pi.getLength());
        Assert.assertTrue(pi.isHasLower());
        Assert.assertFalse(pi.isHasUpper());
        Assert.assertFalse(pi.isHasNumber());
        Assert.assertFalse(pi.isHasSpecial());
        Assert.assertEquals("1", pi.getCategory());
        Assert.assertEquals("2016-06-28", new SimpleDateFormat("yyyy-MM-dd").format(pi.getDateChanged().getTime()));
        Assert.assertEquals("A \\ B", pi.getNotes());
    }

}
