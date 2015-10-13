package net.malahovsky.utils.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import net.malahovsky.utils.Util;
import net.malahovsky.utils.UtilConfig;

import java.lang.reflect.Method;

// net.malahovsky.utils.view.UtilActivity
@SuppressWarnings("unused")
public abstract class UtilActivity extends Activity implements OnClickListener
{
	public static UtilActivity _this;
	protected Integer realW = 0;
	protected Integer realH = 0;
	protected int width;
	protected int height;
	protected Toast toast = null;
	protected boolean msg_queued = false;
	public ProgressDialog pDialog = null;
	public static UtilConfig cfg;
	public boolean currentlyVisible = true;
	public Handler handler;
	private boolean blockBack = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		_this = this;
		GetWandH();
		cfg = new UtilConfig(this);
		pDialog = new ProgressDialog(this);
		handler = new Handler();
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
				if (toast != null)
					toast.setText(String.valueOf(text));
				else
					toast = Toast.makeText(_this, String.valueOf(text),
							Toast.LENGTH_SHORT);
				toast.show();
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
		Util.LOG(text);
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

	@Override
	public void onClick(View arg0)
	{
	}
	
	public String S(int res)
	{
		return getString(res);
	}
	
	protected boolean IsLand()
	{
		return Util.IsLand(_this);
	}
	
	protected void Hide(int res)
	{
		findViewById(res).setVisibility(View.GONE);
	}
	
	protected void Hide(View res)
	{
		res.setVisibility(View.GONE);
	}
	
	protected void Hide2(int res)
	{
		findViewById(res).setVisibility(View.INVISIBLE);
	}
	
	protected void Hide2(View res)
	{
		res.setVisibility(View.INVISIBLE);
	}
	
	protected void Show(int res)
	{
		findViewById(res).setVisibility(View.VISIBLE);
	}
	
	protected void Show(View res)
	{
		res.setVisibility(View.VISIBLE);
	}
	
	public static long NOW()
	{
		return System.currentTimeMillis();
	}
	
	public <T> T f(int id)
	{
		return (T)findViewById(id);
	}

	protected View fv(int id)
	{
		return findViewById(id);
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
	
	protected static void RunDelayed(final Runnable r, final int delay)
	{
		_this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				new Handler().postDelayed(r, delay);
			}
		});
	}

	public static void Run(final Runnable r)
	{
		_this.runOnUiThread(r);
	}

	public void Back()
	{
		Util.Back(this);
	}

	protected void startActivity(Class<?> ac)
	{
		startActivity(new Intent(this, ac));
	}

	protected void startActivity(Class<?> ac, Bundle extra)
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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && blockBack)
		{
			//preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
