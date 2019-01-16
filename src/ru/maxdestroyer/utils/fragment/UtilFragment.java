/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import ru.maxdestroyer.utils.Util;

public abstract class UtilFragment extends Fragment implements View.OnClickListener
{
	protected Context context;
	protected View fv = null;

	public UtilFragment()
	{
	}

	@SuppressLint("ValidFragment")
	public UtilFragment(Context context)
	{
	   this.context = context;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                getLayout(), container, false);
        ButterKnife.bind(this, v);
        onCreateView(v);
        return v;
    }

    protected void onCreateView(final View view) {

    }

    protected abstract int getLayout();

	@Override
	public void onClick(View v)
	{

	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		Util.hideKeyboard(getActivity());
	}

	protected <T> T f(int id)
	{
		if (fv != null)
			return f(fv, id);
		if (getView() == null)
			return null;
		return (T)getView().findViewById(id);
	}

	protected <T> T f(View fv, int id)
	{
		if (fv == null)
			return f(id);
		return (T)fv.findViewById(id);
	}

	protected void Hide(View fv, int id)
	{
		if (fv != null)
		{
			fv.findViewById(id).setVisibility(View.GONE);
		}
		else
			getView().findViewById(id).setVisibility(View.GONE);
	}

	protected void Hide(int id)
	{
		if (fv != null)
		{
			Hide(fv, id);
		}else
			getView().findViewById(id).setVisibility(View.GONE);
	}

	protected void Hide(View v)
	{
		v.setVisibility(View.GONE);
	}

	protected void Hide2(View fv, int id)
	{
		if (fv != null)
		{
			fv.findViewById(id).setVisibility(View.INVISIBLE);
		}
		else
			getView().findViewById(id).setVisibility(View.INVISIBLE);
	}

	public void Show(View fv, int id)
	{
		if (fv != null)
		{
			fv.findViewById(id).setVisibility(View.VISIBLE);
		}
		else
			getView().findViewById(id).setVisibility(View.VISIBLE);
	}

	public void Show(int id)
	{
		getView().findViewById(id).setVisibility(View.VISIBLE);
	}

	protected boolean isFragmentUIActive()
	{
		return isAdded() && !isDetached() && !isRemoving() && getActivity() != null;
	}

	protected void MSG(String msg)
	{
		Util.msg(context, msg);
	}

	protected void selfRemove() {
		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
	}
}
