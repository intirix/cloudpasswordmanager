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
package com.intirix.cloudpasswordmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;

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
    public static <T extends Activity> ActivityController<T> recreateActivity(ActivityController<T> controller) {
        Intent origIntent = controller.getIntent();

        final Bundle state = new Bundle();
        controller.saveInstanceState(state);
        controller.pause();
        controller.stop();
        controller.destroy();

        // recreate the activity from the bundle
        if (origIntent==null) {
            controller = (ActivityController<T>) Robolectric.buildActivity(controller.get().getClass());
        } else {
            controller = (ActivityController<T>) Robolectric.buildActivity(controller.get().getClass(),origIntent);
        }

        controller.create(state).start().restoreInstanceState(state).resume().visible();
        return controller;
    }

}
