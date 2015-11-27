/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import ru.maxdestroyer.utils.UtilConfig;
import ru.maxdestroyer.utils.fragment.UtilFragment;

import java.lang.reflect.Method;
import java.util.List;

// ru.maxdestroyer.utils.activity.UtilActivity
@SuppressWarnings("unused")
public abstract class UtilActivityFragment extends FragmentActivity implements OnClickListener
{
	public static UtilActivityFragment _this;
	protected Integer realW = 0;
	protected Integer realH = 0;
	public int width;
	public int height;
	Toast toast = null;
	protected boolean msg_queued = false;
	public ProgressDialog pDialog = null;
	public static UtilConfig cfg;
	public static boolean currentlyVisible = true;
	public android.support.v4.app.FragmentTransaction fTrans = null;
	public boolean blockBack = false;
	public Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		_this = this;
		GetWandH();
		cfg = new UtilConfig().getInstance().init(this);
		pDialog = new ProgressDialog(this);
		handler = new Handler();
	}

	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		ButterKnife.bind(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (blockBack)
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void MSG(Object text)
	{
		if (msg_queued)
		{
			if (toast != null)
			{
				// toast.cancel();
				toast.setText(String.valueOf(text));
			} else
				toast = Toast.makeText(this, String.valueOf(text),
						Toast.LENGTH_LONG);
			toast.show();
		} else
			Toast.makeText(this, String.valueOf(text),
					Toast.LENGTH_SHORT).show();
	}
	
	// ignore queue
	protected void MSG2(Object text)
	{
		Toast.makeText(this, String.valueOf(text),
					Toast.LENGTH_SHORT).show();
	}
	
	// UI thread
	public void MSG3(final Object text)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(_this, String.valueOf(text),
							Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	protected void HideMSG()
	{
		if (toast != null)
			toast.cancel();
	}
	
	protected void LOG(Object text)
	{
		ru.maxdestroyer.utils.Util.LOG(text);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		GetWandH();
	}
	
	@SuppressLint("NewApi")
	protected void GetWandH()
	{
		final DisplayMetrics metrics = new DisplayMetrics(); 
	    Display display = getWindowManager().getDefaultDisplay();  
	    
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// ��� ���������������
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		
		// only 14 15 16
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
		{
			Method mGetRawH, mGetRawW;
			try
			{
				mGetRawH = Display.class.getMethod("getRawHeight");
				mGetRawW = Display.class.getMethod("getRawWidth");
				// ��� ��������
				realW = (Integer) mGetRawW.invoke(display);
				realH = (Integer) mGetRawH.invoke(display);
			} catch (Exception e)
			{
				Log.e("mGetRawH", "error!");
				e.printStackTrace();
			}
		}
		else if (Build.VERSION.SDK_INT >= 17) // 4.2.2+
		{
			display.getRealMetrics(metrics);

			realW = metrics.widthPixels;
			realH = metrics.heightPixels;
		}
		else
		{
			realW = width;
			realH = height;
		}
	}

	protected void startActivity(Class<?> ac)
	{
		startActivity(new Intent(this, ac));
	}

	public void startActivity(Class<?> ac, Bundle extra)
	{
		startActivity(new Intent(this, ac).putExtras(extra));
	}

	protected void startActivityForResult(Class<?> ac, int code)
	{
		startActivityForResult(new Intent(this, ac), code);
	}

	protected void startActivityForResult(Class<?> ac, int code, Bundle extra)
	{
		startActivityForResult(new Intent(this, ac).putExtras(extra), code);
	}

	@Override
	public void onClick(View v)
	{
		if (getVisibleFragment() != null)
			getVisibleFragment().onClick(v);
	}
	
	protected String S(int res)
	{
		return getString(res);
	}
	
	protected boolean IsLand()
	{
		return ru.maxdestroyer.utils.Util.IsLand(_this);
	}
	
	public void Hide(int res)
	{
		findViewById(res).setVisibility(View.GONE);
	}
	
	public void Hide(Object res)
	{
		((View)res).setVisibility(View.GONE);
	}
	
	public void Hide2(int res)
	{
		findViewById(res).setVisibility(View.INVISIBLE);
	}
	
	public void Hide2(View res)
	{
		res.setVisibility(View.INVISIBLE);
	}
	
	public void Show(int res)
	{
		findViewById(res).setVisibility(View.VISIBLE);
	}
	
	public void Show(Object res)
	{
		((View)res).setVisibility(View.VISIBLE);
	}
	
	protected static long NOW()
	{
		return System.currentTimeMillis();
	}
	
	public <T> T f(int id)
	{
		return (T)findViewById(id);
	}
	
	public static void ShowDialog(final String msg)
	{
		_this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (_this.pDialog.isShowing() || _this.isFinishing())
					return;

				_this.pDialog.setTitle("");
				_this.pDialog.setMessage(msg);
				_this.pDialog.setIndeterminate(true);
				// ua.pDialog.setCancelable(false);
				_this.pDialog.show();

				{
					TextView tv1 = (TextView) _this.pDialog
							.findViewById(android.R.id.message);
					tv1.setTextColor(Color.WHITE);
					((android.widget.LinearLayout) tv1.getParent())
							.setBackgroundColor(Color.BLACK);
				}
			}
		});
	}

	public UtilFragment getVisibleFragment()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<Fragment> fragments = fragmentManager.getFragments();
		if (fragments == null)
			return null;
		for (Fragment fragment : fragments)
		{
			if (fragment != null && fragment.isVisible()) {
				return (UtilFragment) fragment;
			}
		}
		return null;
	}

	// is backstack non empty
	public boolean CanBack()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		return fragmentManager.getBackStackEntryCount() > 0;
	}
	
	public static void CancelDial()
	{
		// ������ � UI ������!
		_this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (_this.pDialog.isShowing())
					_this.pDialog.cancel();
			}
		});
	}
	
	//@Override
	public void OnTaskFinish(Object[] param) {}

	@Override
	protected void onDestroy()
	{
		//_this = null; // �� ���� ���, ������ ��
		currentlyVisible = false;
		//db.close();
		super.onDestroy();
	};
	
	@Override
	protected void onPause()
	{
		currentlyVisible = false;
		
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		currentlyVisible = true;

		super.onResume();
	}
}
