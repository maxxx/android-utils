/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ru.maxdestroyer.utils.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class NameValue extends BaseAdapter {
    public ArrayList<? extends Object> data = new ArrayList<>();
    LayoutInflater inflater;
    int row;

    public NameValue(Activity context, ArrayList<? extends Object> data, int resource) {
        this.data = data;
        this.row = resource;
        inflater = context.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewParent row = super.getView(position, convertView, parent);
        LinearLayout view = (LinearLayout) inflater.inflate(row, parent, false);

        Object call = getItem(position);
        Field[] fields = call.getClass().getDeclaredFields();
        String value = "";
        String name = "";

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                value = field.get(call).toString();
                name = field.getName();
                LinearLayout _row = (LinearLayout) inflater.inflate(R.layout.li_namevalue, parent, false);
                TextView tv1 = (TextView) _row.getChildAt(0);
                TextView tv2 = (TextView) _row.getChildAt(1);

                tv1.setText(name);
                tv2.setText(value);

                view.addView(_row);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }

        return view;
    }
}