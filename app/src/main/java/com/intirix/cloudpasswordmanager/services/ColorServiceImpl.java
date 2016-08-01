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

import android.graphics.Color;

import javax.inject.Inject;

/**
 * Created by jeff on 7/16/16.
 */
public class ColorServiceImpl implements ColorService {

    @Inject
    public ColorServiceImpl() {
    }

    /**
     * Give us the opportunity to prevent the deaded mock error:
     * Method parseColor in android.graphics.Color not mocked
     * @param hexrgb
     * @return
     */
    @Override
    public int parseColor(String hexrgb) {
        return Color.parseColor(hexrgb);
    }

    @Override
    public int getTextColorForBackground(int bg) {
        // http://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1.0 - ( 0.299 * Color.red(bg) + 0.587 * Color.green(bg) + 0.114 * Color.blue(bg) )/255;

        if (a<0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

}
