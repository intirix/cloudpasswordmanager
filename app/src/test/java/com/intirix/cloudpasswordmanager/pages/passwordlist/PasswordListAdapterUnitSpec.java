package com.intirix.cloudpasswordmanager.pages.passwordlist;

import com.intirix.cloudpasswordmanager.BaseTestCase;
import com.intirix.cloudpasswordmanager.BuildConfig;
import com.intirix.cloudpasswordmanager.TestPasswordApplication;
import com.intirix.cloudpasswordmanager.services.backend.beans.PasswordBean;
import com.intirix.cloudpasswordmanager.services.session.SessionService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.android.controller.ActivityController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 7/26/17.
 */

@RunWith(RobolectricTestRunner.class)


public class PasswordListAdapterUnitSpec extends BaseTestCase {

    @Test
    public void verifyPassingNullToUpdateListClearsList() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // the list should be empty because we haven't added anything
        Assert.assertEquals(0,activity.adapter.getItemCount());

        List<PasswordBean> list = new ArrayList<>();

        PasswordBean bean1 = new PasswordBean();
        bean1.setId("A");
        list.add(bean1);

        activity.adapter.updateList(list);
        // the list should now contain the bean
        Assert.assertEquals(1,activity.adapter.getItemCount());

        activity.adapter.updateList(null);
        // the list should be empty
        Assert.assertEquals(0,activity.adapter.getItemCount());


        controller.pause().stop().destroy();


    }

    @Test
    public void verifyDoubleCallUpdateList() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // the list should be empty because we haven't added anything
        Assert.assertEquals(0,activity.adapter.getItemCount());

        List<PasswordBean> list = new ArrayList<>();

        PasswordBean bean1 = new PasswordBean();
        bean1.setId("A");
        list.add(bean1);

        activity.adapter.updateList(list);
        // the list should now contain the bean
        Assert.assertEquals(1,activity.adapter.getItemCount());

        bean1 = new PasswordBean();
        bean1.setId("A");
        list.clear();
        list.add(bean1);

        activity.adapter.updateList(list);
        // the list should still only contain a single copy of the bean
        Assert.assertEquals(1,activity.adapter.getItemCount());


        controller.pause().stop().destroy();

    }


    @Test
    public void verifyRemoveItemFromList() {
        SessionService sessionService = serviceRef.sessionService();
        sessionService.start();

        ActivityController<PasswordListActivity> controller = Robolectric.buildActivity(PasswordListActivity.class).create().start().resume();
        PasswordListActivity activity = controller.get();

        // the list should be empty because we haven't added anything
        Assert.assertEquals(0,activity.adapter.getItemCount());

        List<PasswordBean> list = new ArrayList<>();

        PasswordBean bean1 = new PasswordBean();
        bean1.setId("A");
        list.add(bean1);

        PasswordBean bean2 = new PasswordBean();
        bean2.setId("B");
        list.add(bean2);

        activity.adapter.updateList(list);
        // the list should now contain the bean
        Assert.assertEquals(2,activity.adapter.getItemCount());

        list.remove(1);

        activity.adapter.updateList(list);
        Assert.assertEquals(1,activity.adapter.getItemCount());


        controller.pause().stop().destroy();

    }

}
