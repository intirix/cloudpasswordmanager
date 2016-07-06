package com.intirix.cloudpasswordmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

/**
 * Created by jeff on 7/5/16.
 */
public class ActivityLifecycleTestUtil {

    /**
     * Simulate restarting the activity
     * @param controller
     * @param <T>
     * @return
     */
    public static <T extends Activity>ActivityController<T> recreateActivity(ActivityController<T> controller) {
        Intent origIntent = controller.getIntent();

        final Bundle state = new Bundle();
        controller.saveInstanceState(state);
        controller.pause();
        controller.stop();
        controller.destroy();

        // recreate the activity from the bundle
        controller = (ActivityController<T>) Robolectric.buildActivity(controller.get().getClass());
        if (origIntent!=null) {
            controller.withIntent(origIntent);
        }

        controller.create(state).start().restoreInstanceState(state).resume().visible();
        return controller;
    }

}
