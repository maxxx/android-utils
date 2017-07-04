/*
 * Copyright (C) 2017 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by Maxim S. on 13.04.2016.
 */

public final class ProgressHelper {

    private Activity context = null;
    private ProgressDialog dial = null;

    public ProgressHelper(Activity context) {
        this.context = context;
    }

    public void show(String msg) {
        hide();

        dial = new ProgressDialog(context);
        dial.setMessage(msg);
        dial.setCancelable(true);
        dial.setCanceledOnTouchOutside(false);
        dial.setOnCancelListener(null);
        dial.show();
    }

    public void show(String msg, DialogInterface.OnCancelListener onCancel) {
        hide();
        if (context == null) {
            Log.e("ProgressHelper", "show: context is null");
            return;
        }

        dial = new ProgressDialog(context);
        dial.setMessage(msg);
        dial.setCancelable(true);
        dial.setCanceledOnTouchOutside(false);
        dial.setOnCancelListener(onCancel);
        dial.show();
    }

    public void hide() {
        if (context == null)
            return;

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dial != null) {
                    if (dial.isShowing())
                        dial.dismiss();
                    dial = null;
                }
            }
        });
    }

    public void destroy() {
        hide();
        this.context = null;
    }
}
