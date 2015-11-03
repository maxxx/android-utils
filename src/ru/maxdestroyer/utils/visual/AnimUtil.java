/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.visual;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Maxim S. on 03.11.2015.
 */
public class AnimUtil {

    public static Animation getFadeAnimSet(long offset, long duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(offset);
        fadeOut.setDuration(duration);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        return animation;
    }

    public Animation getFadeIn(long duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        return animation;
    }

    public Animation getFadeOut(long duration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(duration);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeOut);
        return animation;
    }
}
