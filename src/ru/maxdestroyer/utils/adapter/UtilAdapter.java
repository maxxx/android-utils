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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import ru.maxdestroyer.utils.annotation.processor.AnnProcessor;

/**
 * @param <T> your entity class
 * @param <H> your view holder, should be implemented as separate class
 */
public abstract class UtilAdapter<T, H extends UtilAdapter.BaseViewHolder> extends BaseAdapter {

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
    public T getItem(final int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    public void setData(final ArrayList<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addItem(final T item) {
        this.data.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(final T item) {
        this.data.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final H holder;
        T currentItem = getItem(position);

        if (convertView != null && convertView.getTag() != null) {
            holder = (H) convertView.getTag();
        } else {
            convertView = context.getLayoutInflater().inflate(row, parent, false);
            holder = initHolder(convertView);
            convertView.setTag(holder);
        }

        AnnProcessor.fillView(currentItem, holder.view);
        onGetView(currentItem, holder, position);

//        if (position == getCount() - 1 && getCount() < totalCount)
//        {
//            fillData();
//        }

        return convertView;
    }

    private H initHolder(final View convertView) {
        Class<T> holderClass = getHolderClass();
        return (H) buildHolder(holderClass);
    }

    private T buildHolder(Class<T> holderClass) {
        Constructor<?> constructor = holderClass.getConstructors()[0];
        try {
            T instance = (T) constructor.newInstance();
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Additional processing
     */
    protected abstract void onGetView(final T item, final H holder, final int position);

    public static class BaseViewHolder {
        protected final View view;

        public BaseViewHolder(final View view) {
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    @SuppressWarnings("unchecked") protected Class<T> getHolderClass() {
        try {
            Class<?> clazz =
                (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            return (Class<T>) clazz;
        } catch (Exception e) {
            throw new IllegalStateException(
                "Class is not parametrized with generic type!!! Please use extends <> ");
        }
    }
}