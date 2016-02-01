/*
 * Copyright (C) 2016 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Maxim S. on 01.02.2016.
 */
public class ImageUtils {

    public static int dpToPix(Context c, float dips) {
        return (int) (dips * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static float pixToDp(Context c, float px) {
        Resources resources = c.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void saveBitmap(Bitmap result, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            result.compress(Bitmap.CompressFormat.JPEG, 85, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadBitmap(String imgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            return BitmapFactory.decodeFile(imgPath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void makeScreenshot(Activity c, String path, View view) {
        // Get device dimmensions
        int h = Util.getScreenHeight(c);
        int w = Util.getScreenWidth(c);
        // Display display = c.getWindowManager().getDefaultDisplay();
        // Point size = new Point();
        // display.getSize(size);

        // Get root view
        // View view = ???.getRootView();

        // Create the bitmap to use to draw the screenshot
        final Bitmap bitmap = Bitmap
                .createBitmap(w, h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);

        // Get current theme to know which background to use
        final Activity activity = c;
        final Resources.Theme theme = activity.getTheme();
        final TypedArray ta = theme
                .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        final int res = ta.getResourceId(0, 0);
        final Drawable background = activity.getResources().getDrawable(res);

        // Draw background
        background.draw(canvas);

        // Draw views
        view.setDrawingCacheEnabled(true);
        view.draw(canvas);
        view.setDrawingCacheEnabled(false);

        // Save the screenshot to the file system
        FileOutputStream fos = null;
        final File sddir = new File(path);
        if (!sddir.exists())
            sddir.mkdirs();
        File f = new File(path, System.currentTimeMillis() + ".jpg");
        try {
            fos = new FileOutputStream(f);
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos))
                Util.log("Compress/Write failed");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Util.log("MakeScreenshot: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void createThumbnail(final String imgFileName, final String previewFileName) {
        try {
            final int THUMBNAIL_SIZE = 64;

            FileInputStream fis = new FileInputStream(imgFileName);
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            File thumbnailFile = new File(previewFileName);
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Bitmap createTransparentFrom(Bitmap bit) {
        int width = bit.getWidth();
        int height = bit.getHeight();
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] allpixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < myBitmap.getHeight() * myBitmap.getWidth(); i++) {
            allpixels[i] = Color.alpha(Color.TRANSPARENT);
        }

        myBitmap.setPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }
}
