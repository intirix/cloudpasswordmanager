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
