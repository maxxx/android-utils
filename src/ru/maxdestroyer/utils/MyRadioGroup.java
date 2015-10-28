/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import java.util.ArrayList;

public class MyRadioGroup
{
	ArrayList<View> views = new ArrayList<View>();
	public OnClickListener onClickAttached = null; // onClick из активити
	public boolean atleast_one = true; // если тру - хотя бы одна кнопка остается нажатой всегда

	public MyRadioGroup()
	{
	}

	public void Add(View v)
	{
		views.add(v);
		if (v instanceof CheckBox)
			((CheckBox)v).setOnCheckedChangeListener(onCheck);
		else if (v instanceof Button)
			((Button)v).setOnClickListener(onClick);
		else if (v instanceof ImageView)
			((ImageView)v).setOnClickListener(onClick);
	}
	
	OnCheckedChangeListener onCheck = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton vi,
				boolean isChecked)
		{
			if (!isChecked)
				return;
			
			for (Object v : views)
			{
				if (v instanceof CheckBox)
				{
					if (v != vi)
						((CheckBox)v).setChecked(false);
				}
			}
		}
	};
	
	OnClickListener onClick = new OnClickListener()
	{
		@Override
		public void onClick(View vi)
		{
			// нажата кнопка
			if (vi.getTag() != null)
			{
				if (!atleast_one)
					onClickAttached.onClick(vi);
				//((Button)vi).setTag(null);
				return;
			}
			onClickAttached.onClick(vi);
			vi.setTag(1);
			
			for (View v : views)
			{
				if (v instanceof Button  || v instanceof ImageView)
					if (v != vi)
						if (v.getTag() != null) // checked
						{
							//((Button)v).performClick();
							onClickAttached.onClick(v);
							v.setTag(null);
						}
			}
		}
	};
	
}
