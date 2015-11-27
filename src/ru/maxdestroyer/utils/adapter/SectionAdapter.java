/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ru.maxdestroyer.utils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SectionAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private boolean init = true;

    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public SectionAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SectionAdapter(Context context, List<String> data) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init = false;
        for (String item : data) {
            if (item.startsWith("!section_")) {
                addSectionHeaderItem(item.substring("!section_".length()));
            } else {
                addItem(item);
            }
        }
        init = true;
        notifyDataSetChanged();
    }

    public void addItem(final String item) {
        mData.add(item);
        if (init) {
            notifyDataSetChanged();
        }
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        if (init) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.li_section, null);
                    holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                    break;
                case TYPE_ITEM:
                    convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
                    holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position));

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
    }

}