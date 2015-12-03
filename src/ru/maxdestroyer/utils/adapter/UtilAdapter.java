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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import butterknife.ButterKnife;
import ru.maxdestroyer.utils.annotation.processor.AnnProcessor;

import java.util.ArrayList;

public class UtilAdapter<T> extends BaseAdapter {

    protected final Activity context;
    private final int row;
    protected ArrayList<T> data = new ArrayList<>();

    public UtilAdapter(final Activity context, final ArrayList<T> data, int row) {
        this.context = context;
        this.data = data;
        this.row = row;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(final int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }


    public void setData(final ArrayList<T> tasks) {
        this.data = tasks;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BaseViewHolder holder;
        T currentItem = (T) getItem(position);

        if (convertView != null && convertView.getTag() != null) {
            holder = (BaseViewHolder) convertView.getTag();
        } else {
            convertView = context.getLayoutInflater().inflate(row, parent, false);
            holder = initHolder(convertView);
            convertView.setTag(holder);
        }

        AnnProcessor.fillView(currentItem, holder.view);
        onGetView(currentItem, holder);

//        if (position == getCount() - 1 && getCount() < totalCount)
//        {
//            fillData();
//        }

        return convertView;
    }

    protected BaseViewHolder initHolder(final View convertView) {
        return new BaseViewHolder(convertView);
    }

    /**
     * Additional processing
     */
    protected void onGetView(final T currentItem, final BaseViewHolder baseHolder) {

    }

    protected static class BaseViewHolder {
        protected final View view;

        public BaseViewHolder(final View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}