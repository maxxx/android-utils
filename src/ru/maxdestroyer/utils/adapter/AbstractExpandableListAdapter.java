/*
 * Copyright (C) 2016 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Maxim S. on 26.02.2016.
 */
public abstract class AbstractExpandableListAdapter<A, B> implements ExpandableListAdapter {

    private final List<Map.Entry<A, List<B>>> objects;

    private final DataSetObservable dataSetObservable = new DataSetObservable();

    private final Context context;

    private final Integer groupClosedView;

    private final Integer groupExpandedView;

    private final Integer childView;

    protected final LayoutInflater inflater;

    public AbstractExpandableListAdapter(Context context, int groupClosedView,
                                         int groupExpandedView, int childView, List<Map.Entry<A, List<B>>> objects) {
        this.context = context;
        this.objects = objects;
        this.groupClosedView = new Integer(groupClosedView);
        this.groupExpandedView = new Integer(groupExpandedView);
        this.childView = new Integer(childView);

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Map.Entry<A, List<B>> group) {
        this.getObjects().add(group);
        this.notifyDataSetChanged();
    }

    public void remove(A group) {
        for (Map.Entry<A, List<B>> entry : this.getObjects()) {
            if (entry != null && entry.getKey().equals(group)) {
                this.getObjects().remove(group);
                this.notifyDataSetChanged();
                break;
            }
        }
    }

    public void remove(Map.Entry<A, List<B>> entry) {
        remove(entry.getKey());
    }

    public void addChild(A group, B child) {
        for (Map.Entry<A, List<B>> entry : this.getObjects()) {
            if (entry != null && entry.getKey().equals(group)) {
                if (entry.getValue() == null)
                    entry.setValue(new ArrayList<B>());

                entry.getValue().add(child);
                this.notifyDataSetChanged();
                break;
            }
        }
    }

    public void removeChild(A group, B child) {
        for (Map.Entry<A, List<B>> entry : this.getObjects()) {
            if (entry != null && entry.getKey().equals(group)) {
                if (entry.getValue() == null)
                    return;

                entry.getValue().remove(child);
                this.notifyDataSetChanged();
                break;
            }
        }
    }

    public void notifyDataSetChanged() {
        this.getDataSetObservable().notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        this.getDataSetObservable().notifyInvalidated();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.getDataSetObservable().registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.getDataSetObservable().unregisterObserver(observer);
    }

    public int getGroupCount() {
        return getObjects().size();
    }

    public int getChildrenCount(int groupPosition) {
        return getObjects().get(groupPosition).getValue().size();
    }

    public A getGroup(int groupPosition) {
        return getObjects().get(groupPosition).getKey();
    }

    public B getChild(int groupPosition, int childPosition) {
        return getObjects().get(groupPosition).getValue().get(childPosition);
    }

    public long getGroupId(int groupPosition) {
        return ((Integer) groupPosition).longValue();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return ((Integer) childPosition).longValue();
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView != null && convertView.getId() !=
                (isExpanded ? getGroupExpandedView() : getGroupClosedView())) {
//          do nothing, we're good to go, nothing has changed.
        } else {
//          something has changed, update.
            convertView = inflater.inflate(isExpanded ? getGroupExpandedView() :
                    getGroupClosedView(), parent, false);
            convertView.setTag(getObjects().get(groupPosition));
        }

        return convertView;
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView != null) {
//          do nothing
        } else {
//          create
            convertView = inflater.inflate(getChildView(), parent, false);
            convertView.setTag(getObjects().get(groupPosition).getValue().get(childPosition));
        }

        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEmpty() {
        return getObjects().size() == 0;
    }

    public void onGroupExpanded(int groupPosition) {

    }

    public void onGroupCollapsed(int groupPosition) {

    }

    public long getCombinedChildId(long groupId, long childId) {
        return groupId * 10000L + childId;
    }

    public long getCombinedGroupId(long groupId) {
        return groupId * 10000L;
    }

    protected DataSetObservable getDataSetObservable() {
        return dataSetObservable;
    }

    protected List<Map.Entry<A, List<B>>> getObjects() {
        return objects;
    }

    protected Context getContext() {
        return context;
    }

    protected Integer getGroupClosedView() {
        return groupClosedView;
    }

    protected Integer getGroupExpandedView() {
        return groupExpandedView;
    }

    protected Integer getChildView() {
        return childView;
    }
}
