package com.intirix.cloudpasswordmanager.services;

import android.content.Context;

import com.ibm.icu.text.SimpleDateFormat;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.beans.PasswordInfo;
import com.intirix.cloudpasswordmanager.services.beans.PasswordResponse;

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
        application = TestPasswordApplication.class)

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


}