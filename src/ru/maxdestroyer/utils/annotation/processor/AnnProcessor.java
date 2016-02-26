/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.annotation.processor;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import ru.maxdestroyer.utils.Util;
import ru.maxdestroyer.utils.annotation.BindView;

public class AnnProcessor {

    public static void fillView(final Object entity, View base) {
        try {
            Class cl = entity.getClass();

            for (final Field f : cl.getDeclaredFields()) {
                try {
                    BindView ann = f.getAnnotation(BindView.class);
                    int vId = 0;
                    if (ann == null) {
                        continue;
                    }
                    vId = ann.viewId();
                    View view = base.findViewById(vId);
                    f.setAccessible(true);
                    if (view != null) {
                        if (view instanceof EditText) {
                            ((EditText) view).setText(f.get(entity).toString());
                            ((EditText) view).addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    try {
                                        f.set(entity, editable.toString());
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if (view instanceof TextView) {
                            ((TextView) view).setText(f.get(entity).toString());
                        }
                    } else {
                        //throw new RuntimeException("AnnProcessor:fillView: view not found! " + f.getName());
                        Util.logv("AnnProcessor:fillView: view not found -  " + f.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseView(final Object entity, View base) {
        try {
            Class cl = entity.getClass();

            for (final Field f : cl.getDeclaredFields()) {
                try {
                    BindView ann = f.getAnnotation(BindView.class);
                    if (ann == null) {
                        continue;
                    }

                    int vId = ann.viewId();
                    View view = base.findViewById(vId);
                    f.setAccessible(true);
                    if (view != null) {
                        if (view instanceof TextView) {
                            f.set(entity, ((TextView) view).getText().toString());
                        }
                    } else {
                        //throw new RuntimeException("AnnProcessor:parseView: view not found! " + f.getName());
                        Util.logv("AnnProcessor:parseView: view not found - " + f.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
