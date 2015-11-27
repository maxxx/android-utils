/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.maxdestroyer.utils.net.HostChecker;
import ru.maxdestroyer.utils.visual.WakeLocker;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({ "NewApi", "ServiceCast" })
@SuppressWarnings("unused")
public abstract class Util
{
	public static void MSG(Context c, Object text)
	{
		Toast.makeText(c, String.valueOf(text), Toast.LENGTH_SHORT).show();
	}
	
	public static void MSGT(Context c, Object text, int seconds)
	{
		final Toast tag = Toast.makeText(c, String.valueOf(text), Toast.LENGTH_SHORT);

		tag.show();

		new CountDownTimer(seconds * 1000, 1000)
		{
		    @Override
			public void onTick(long millisUntilFinished) {tag.show();}
		    @Override
			public void onFinish() {tag.show();}
		}.start();
	}

	/**
	 * from main thread
	 * @param c
	 * @param text
	 */
	public static void MSGM(final Activity c, final Object text)
	{
		c.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(c, String.valueOf(text), Toast.LENGTH_SHORT).show();
            }
        });
	}

	public static void GoURL(Context c, String url)
	{
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		c.startActivity(browserIntent);
	}

	// <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	// udid
	public static String GetDeviceId(Context c)
	{
		TelephonyManager tManager = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		String devId = tManager.getDeviceId();
		if (devId == null || devId.equals("000000000000000")) // эмулятор. Тогда Android Id
			devId = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
		return devId;
	}

	public static String GetSimId(Context c)
	{
		TelephonyManager tManager = (TelephonyManager) c
		  .getSystemService(Context.TELEPHONY_SERVICE);
		String devId = tManager.getSimSerialNumber() != null ? tManager.getSimSerialNumber() : "";
		return devId;
	}

	public static long GetFreeSpace(String path)
	{
		StatFs stat = new StatFs(path);
		long sdAvailSize;
		if (GetApiLvl() >= 18)
		{
			sdAvailSize = stat.getAvailableBlocksLong()
			  * stat.getBlockSizeLong();
		} else if (GetApiLvl() >= 9)
			sdAvailSize = new File(path).getUsableSpace();
		else
			sdAvailSize = stat.getAvailableBlocks()
		  * stat.getBlockSize();

		return sdAvailSize;
	}

	// TODO: skype... ?
	public static boolean CanCall(Context c)
	{
		TelephonyManager telMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		// не факт что сработает
		if (telMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)
			return false;
		
		int simState = telMgr.getSimState();
		switch (simState)
		{
			case TelephonyManager.SIM_STATE_UNKNOWN: // !
			case TelephonyManager.SIM_STATE_READY:
				return true;
			default:
				return false;
		}
	}

	public static String GetOSVersion()
	{
		return "Android " + Build.VERSION.RELEASE;
	}
	
	public static boolean IsDvoika()
	{
		return Build.VERSION.SDK_INT < 11;
	}
	
	public static int GetApiLvl()
	{
		return Build.VERSION.SDK_INT;
	}

	public static String GetDeviceName()
	{
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer))
			return capitalize(model);
		else
			return capitalize(manufacturer) + " " + model;
	}

	public static String capitalize(String s)
	{
		if (s == null || s.length() == 0)
			return "";
		char first = s.charAt(0);
		if (Character.isUpperCase(first))
			return s;
		else
			return Character.toUpperCase(first) + s.substring(1);
	}

	// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
	// />
	public static boolean HasInternet(Context ctx)
	{
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) // нет активного соединения
			return false;

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	public static boolean HasWiFi(Context c)
	{
		ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return mWifi.isConnected();
	}

	public static void DeleteRecursive(String path)
	{
		File f = new File(path);
		if (f.isDirectory())
			for (File child : f.listFiles())
				DeleteRecursive(child.getAbsolutePath());

		f.delete();
	}

	public static int GetScreenWidth(Activity c)
	{
		Display display = c.getWindowManager().getDefaultDisplay();
		final DisplayMetrics metrics = new DisplayMetrics(); 
		// only 14 15 16
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
		{
			try
			{
				Method mGetRawW = Display.class.getMethod("getRawWidth");
				return (Integer) mGetRawW.invoke(display);
			} catch (Exception e)
			{
				Log.e("GetScreenWidth", "error!");
				e.printStackTrace();
			}
		} 
		else if (Build.VERSION.SDK_INT >= 17) // 4.2.2+
		{
			display.getRealMetrics(metrics);

			return metrics.widthPixels;
		}
		else
		{
			c.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			return metrics.widthPixels;
		}

		return 0;
	}

	public static int GetScreenHeight(Activity c)
	{
		Display display = c.getWindowManager().getDefaultDisplay();
		final DisplayMetrics metrics = new DisplayMetrics(); 
		// only 14 15 16
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
		{
			try
			{
				// On Android 4.2 (API 17) and higher, you can use
				// Display.getRealMetrics to get the actual physical display
				// metrics
				Method mGetRawW = Display.class.getMethod("getRawHeight");
				return (Integer) mGetRawW.invoke(display);
			} catch (Exception e)
			{
				Log.e("GetScreenHeight", "error!");
				e.printStackTrace();
			}
		}
		else if (Build.VERSION.SDK_INT >= 17) // 4.2.2+
		{
			display.getRealMetrics(metrics);

			return metrics.heightPixels;
		}
		else
		{
			c.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			return metrics.heightPixels;
		}

		return 0;
	}

	public static int DpToPix(Context c, float dips)
	{
		return (int) (dips * c.getResources().getDisplayMetrics().density + 0.5f);
	}
	
	public static float PixToDp(Context c, float px)
	{
		Resources resources = c.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	public static boolean CheckHost(String url)
	{
		HostChecker h = new HostChecker(url);
		h.start();
		while (h.connect == -1)
		{
			try
			{
				Thread.sleep(50L);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return h.connect == 1;
	}

	public static void LOG(Object txt)
	{
		Log.e("Util.LOG", txt + "");
	}

	public static void LOGV(Object txt)
	{
		Log.v("Util.LOG", txt + "");
	}

	@SuppressLint({ "NewApi", "ServiceCast" })
	@SuppressWarnings("deprecation")
	public static void CopyToClipboard(Context c, String text)
	{
		if (android.os.Build.VERSION.SDK_INT < 11)
		{
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) c
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		}
		else
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData
					.newPlainText(text, text);
			clipboard.setPrimaryClip(clip);
		}
	}

	@SuppressWarnings("deprecation")
	public static String GetClipboard(Context c)
	{
		if (android.os.Build.VERSION.SDK_INT < 11)
		{
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) c
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return String.valueOf(clipboard.getText());
		} else
		{
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return String.valueOf(clipboard.getPrimaryClip().getItemAt(0)
					.getText());
		}
	}

	public static void MakeScreenshot(Activity c, String path, View view)
	{
		// Get device dimmensions
		int h = GetScreenHeight(c);
		int w = GetScreenWidth(c);
		// Display display = c.getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);

		// Get root view
		// View view = ???.getRootView();

		// Create the bitmap to use to draw the screenshot
		final Bitmap bitmap = Bitmap
				.createBitmap(w, h, Bitmap.Config.RGB_565);
		final Canvas canvas = new Canvas(bitmap);

		// Get current theme to know which background to use
		final Activity activity = c;
		final Theme theme = activity.getTheme();
		final TypedArray ta = theme
				.obtainStyledAttributes(new int[] { android.R.attr.windowBackground });
		final int res = ta.getResourceId(0, 0);
		final Drawable background = activity.getResources().getDrawable(res);

		// Draw background
		background.draw(canvas);

		// Draw views
        view.setDrawingCacheEnabled(true);
		view.draw(canvas);
        view.setDrawingCacheEnabled(false);

		// Save the screenshot to the file system
		FileOutputStream fos = null;
		final File sddir = new File(path);
		if (!sddir.exists())
			sddir.mkdirs();
		File f = new File(path, System.currentTimeMillis() + ".jpg");
		try
		{
			fos = new FileOutputStream(f);
			if (fos != null)
			{
				if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos))
					LOG("Compress/Write failed");
				fos.flush();
				fos.close();
			}
		} catch (Exception e)
		{
			LOG("MakeScreenshot: " + e.toString());
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static void Notify(final Context con, String title, String text,
			Class activityToRun, Bundle extras, boolean vib)
	{
		NotificationManager notificationManager = (NotificationManager) con
				.getSystemService(Context.NOTIFICATION_SERVICE);
		WakeLocker.acquire(con);

//		if (MainFrame.BEEP)
//			new SoundMgr(con, R.raw.knock, MainFrame.VOLUME, true);

		{
			// активити которая вызовется при клике на уведомление
			Intent intent = new Intent(con, activityToRun);
			if (extras != null)
				intent.putExtras(extras);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pIntent = PendingIntent.getActivity(con, 127, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder noti = new NotificationCompat.Builder(con)
					.setContentTitle(title).setContentText(text)
					.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent);
			noti.setContentInfo(title);
			
			Notification not = noti.build();

			not.flags |= Notification.FLAG_AUTO_CANCEL /*| Notification.FLAG_ONLY_ALERT_ONCE*/;
			
			if (vib)
				not.defaults |= Notification.DEFAULT_VIBRATE;

			notificationManager.notify(127, not);
			// Notifications.notify(this, 5000, "This text will go away after five seconds.");			
		}

		Handler ha = new Handler(Looper.getMainLooper());
		ha.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					WakeLocker.release();
				}
			}, 5000);
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static void Notify(final Context con, String title, String text,
			Class activityToRun, Bundle extras, boolean vib, int id)
	{
		NotificationManager notificationManager = (NotificationManager) con
				.getSystemService(Context.NOTIFICATION_SERVICE);
		WakeLocker.acquire(con);

//		if (MainFrame.BEEP)
//			new SoundMgr(con, R.raw.knock, MainFrame.VOLUME, true);

		{
			// активити которая вызовется при клике на уведомление
			Intent intent = new Intent(con, activityToRun);
			if (extras != null)
				intent.putExtras(extras);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pIntent = PendingIntent.getActivity(con, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder noti = new NotificationCompat.Builder(con)
					.setContentTitle(title).setContentText(text)
					.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent);
			noti.setContentInfo(title);
			
			Notification not = noti.build();

			not.flags |= Notification.FLAG_AUTO_CANCEL /*| Notification.FLAG_ONLY_ALERT_ONCE*/;
			
			if (vib)
				not.defaults |= Notification.DEFAULT_VIBRATE;

			notificationManager.notify(id, not);
			// Notifications.notify(this, 5000, "This text will go away after five seconds.");			
		}

		Handler ha = new Handler(Looper.getMainLooper());
		ha.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					WakeLocker.release();
				}
			}, 5000);
	}
	
	public static boolean IsTablet(Context activityContext)
	{
		// Verifies if the Generalized Size of the device is LARGE to be
		// considered a Tablet
		boolean large = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);

		//Configuration.SCREENLAYOUT_SIZE_NORMAL = 2
		// If Large, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (large)
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// htc one
			if (metrics.widthPixels == 1080 || metrics.heightPixels == 1080)
				return false;
			
			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
			// DENSITY_TV=213, DENSITY_XHIGH=320
			if (metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_TV
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH)
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean IsHTC(Context activityContext)
	{
		// Verifies if the Generalized Size of the device is LARGE to be
		// considered a Tablet
		boolean large = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);

		//Configuration.SCREENLAYOUT_SIZE_NORMAL = 2
		// If Large, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (large)
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// htc one
			if (metrics.widthPixels == 1080 || metrics.heightPixels == 1080)
				return true;
		}
		return false;
	}
	
	public static boolean IsGTP1000(Context activityContext)
	{
		// Verifies if the Generalized Size of the device is LARGE to be
		// considered a Tablet
		boolean large = ((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE);

		//Configuration.SCREENLAYOUT_SIZE_NORMAL = 2
		// If Large, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (large)
		{
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// samsung Galaxy Tab P1000
			if (metrics.densityDpi == 240 && Util.IsDvoika() && Build.MODEL.contains("GT-P1000"))
				return true;
		}
		return false;
	}
	
	public static int GetDensity(Context c)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		Activity activity = (Activity) c;
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
		// DENSITY_TV=213, DENSITY_XHIGH=320
		return metrics.densityDpi;
	}
	
	public static double GetScreenSize(Activity activity)
	{
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);

		double width = ((double)GetScreenWidth(activity) / (double)displayMetrics.densityDpi);
		double height = ((double)GetScreenHeight(activity) / (double)displayMetrics.densityDpi);

		double screenDiagonal = Math.sqrt(width * width + height * height);
		return screenDiagonal;
	}

	public static long DateToTimestamp(Object year, Object month, Object day, Object hour, Object minute)
	{
	    Calendar c = Calendar.getInstance();
	    c.set(Calendar.YEAR, Convert.ToInt(year));
	    c.set(Calendar.MONTH, Convert.ToInt(month));
	    c.set(Calendar.DAY_OF_MONTH, Convert.ToInt(day));
	    c.set(Calendar.HOUR_OF_DAY, Convert.ToInt(hour));
	    c.set(Calendar.MINUTE, Convert.ToInt(minute));
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);

	    return c.getTimeInMillis();
	}

	public static int DateToTimestamp(java.util.Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		return (int) (c.getTimeInMillis() / 1000L);
	}

	public static long DateToTimestampLong(java.util.Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		return (c.getTimeInMillis());
	}

	public static long DateToTimestampLong(Object year, Object month, Object day, Object hour, Object minute)
	{
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.set(Calendar.YEAR, Convert.ToInt(year));
		c.set(Calendar.MONTH, Convert.ToInt(month));
		c.set(Calendar.DAY_OF_MONTH, Convert.ToInt(day));
		c.set(Calendar.HOUR_OF_DAY, Convert.ToInt(hour));
		c.set(Calendar.MINUTE, Convert.ToInt(minute));
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTimeInMillis() / 1000;
	}
	
	public static String GetCurDate()
	{
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	    Calendar cal = Calendar.getInstance();

	    return dateFormat.format(cal.getTime());
	}

	public static Date TimestampToDate(long ts)
	{
		Date netDate = new Date(ts); // GMT 0
		return netDate;
	}
	
	public static String TimestampToDate(long ts, String format)
	{
		Calendar cldr = Calendar.getInstance();
		cldr.setTime(new Date(ts));
		SimpleDateFormat date = new SimpleDateFormat(format);
		date.setTimeZone(cldr.getTimeZone());
		return date.format(cldr.getTime());
	}

	public static java.util.Date StrToDate(String aDate, String aFormat)
	{
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
		java.util.Date stringDate = new java.util.Date(simpledateformat.parse(aDate, pos).getTime());
		return stringDate;
	}

    /**
     * visible/gone
     * @param v
     */
	public static void ToggleVisibility(View v)
	{
		if (v.getVisibility() == View.VISIBLE)
			v.setVisibility(View.GONE);
		else
			v.setVisibility(View.VISIBLE);
	}

    /**
     * visible/invisible
     * @param v
     */
	public static void ToggleVisibility2(View v)
	{
		if (v.getVisibility() == View.VISIBLE)
			v.setVisibility(View.INVISIBLE);
		else
			v.setVisibility(View.VISIBLE);
	}
	
	public static boolean IsLand(Context c)
	{
		return c.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT;
	}
	
	public static void ExpandList(ListView listView, boolean noAdapter)
	{
		if (noAdapter)
		{
			int totalHeight = 0;
			for (int i = 0; i < listView.getChildCount(); ++i)
				totalHeight += listView.getChildAt(i).getHeight();
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listView.getChildCount() - 1));
			listView.setLayoutParams(params);
			return;
		}
		
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
		{
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = 0;
			listView.setLayoutParams(params);
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); ++i)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
	
	public static void ExpandList(ListView listView, int maxHeight)
	{
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
		  View.MeasureSpec.AT_MOST);
		listView.setTag("expanding");
		for (int i = 0; i < listAdapter.getCount(); ++i)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			totalHeight += listItem.getMeasuredHeight();
		}
		listView.setTag(null);

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		
		if (maxHeight > 0 && params.height > maxHeight)
			params.height = maxHeight;
		listView.setLayoutParams(params);
		//listView.requestLayout();  заставляет скрулить родителя
	}

	// когда список внутри списка, высота почему-то считается криво
	public static void ExpandListPlus(ListView listView, int maxHeight, int addHeight)
	{
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
		  View.MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); ++i)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
		  + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + addHeight;

		if (maxHeight > 0 && params.height > maxHeight)
			params.height = maxHeight;
		listView.setLayoutParams(params);
		//listView.requestLayout();  заставляет скрулить родителя
	}

	// для многострочных текствью
	public static void ExpandList(ListView listView, int maxHeight, int listWidth)
	{
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int totalHeight = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listWidth,
		  View.MeasureSpec.EXACTLY);
		for (int i = 0; i < listAdapter.getCount(); ++i)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
		  + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		if (maxHeight > 0 && params.height > maxHeight)
			params.height = maxHeight;
		listView.setLayoutParams(params);
		//listView.requestLayout();  заставляет скрулить родителя
	}
	
	public static void ExpandListItem(ListView lv, int visibleItems)
	{
		lv.getLayoutParams().height = 0;
		for (int i = 0; i < visibleItems; i++)
		{
			View item = ((ListView) lv).getAdapter().getView(0, null,
					((ListView) lv));
			item.measure(0, 0);
			lv.getLayoutParams().height += (int) (item.getMeasuredHeight());
		}
		// only if inflate
//		item.setLayoutParams(new RelativeLayout.LayoutParams(
//				RelativeLayout.LayoutParams.MATCH_PARENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT));
	}
	
	public static void ExpandGridItem(GridView lv, int rows, int height)
	{
		lv.getLayoutParams().height = 0;
		if ((lv).getAdapter().getCount() > 0)
		{
			View item = (lv).getAdapter().getView(0, null, (lv));
			item.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));
			item.measure(0, 0);
			int h = height > 0 ? height : item.getMeasuredHeight();

			lv.getLayoutParams().height = h * rows;
			int pad = Util.DpToPix(lv.getContext(), 10);
			if (GetApiLvl() >= 16)
				pad = lv.getVerticalSpacing();
			lv.getLayoutParams().height += pad * (rows-1);
		}
	}

	public static String GetPhoneNumber(Context c)
	{
        TelephonyManager mTelephonyMgr = (TelephonyManager) c
                .getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
	}
	
	// <uses-permission android:name="android.permission.GET_TASKS" />

    /**
     * on;y self task after lolipop!
     * @param ctx
     * @return
     */
	public static boolean IsTaskRunning(Context ctx)
	{
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks)
		{
			if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
				return true;
		}

		return false;
	}
	
	// первый захват (...) - .get(1)
	public static ArrayList<String> RegExp(String _pattern, String source)
	{
		ArrayList<String> res = new ArrayList<String>();
		Pattern pattern = Pattern.compile(_pattern);
		Matcher matcher = pattern.matcher(source);
		if (matcher.find())
		{
			res.add(matcher.group(0)); // чтобы индексы совпадали
			// количество найденных групп
			for (int j = 1; j <= matcher.groupCount(); j++)
			{
				String cur = matcher.group(j);
				res.add(cur);
			}
		}
		return res;
	}
	
	public static boolean RegExpMatch(String _pattern, String source)
	{
		ArrayList<String> res = new ArrayList<String>();
		Pattern pattern = Pattern.compile(_pattern);
		Matcher matcher = pattern.matcher(source);
		return matcher.matches();
	}

	public static int currentTimeMillis()
	{
		return (int) (System.currentTimeMillis() / 1000L & 0x00000000FFFFFFFFL);
	}

	public static int TimeToInt(long time)
	{
		return (int) (time / 1000L & 0x00000000FFFFFFFFL);
	}
	
	// в метрах
	// GeoPoint - зависимость от яндекс картс!!
	public static int distanceGP(double la1, double lo1, double la2, double lo2)
	{
		double lat1 = la1 * Math.PI / 180.0f;
		double lat2 = la2 * Math.PI / 180.0f;
		double long1 = lo1 * Math.PI / 180.0f;
		double long2 = lo2 * Math.PI / 180.0f;
		double dL = long2 - long1;

		/* application of the Vincenty formula for spheres: */

		double tmp1 = Math.cos(lat2) * Math.sin(dL);
		tmp1 *= tmp1;
		double tmp2 = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(dL);
		tmp2 *= tmp2;
		double y = Math.sqrt(tmp1 + tmp2);
		double x = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(dL);
		double miles = 6371.009f / 2.0f * Math.atan2(y, x);

		double meterConversion = 1609.0f;

		return (int)(miles * meterConversion);
	}
	
	public static boolean IsEmulator()
	{
		return Build.FINGERPRINT.contains("generic");
	}
	
	// 0(прозрач) .. 1(норм)
	public static void SetOpacity(View v, float opacity)
	{
		AlphaAnimation alpha = new AlphaAnimation(opacity, opacity);
		alpha.setDuration(0);
		alpha.setFillAfter(true);
		v.startAnimation(alpha);
	}
	
	public static void scaleViewAndChildren(View root, float scale)
	{
		// Retrieve the view's layout information
		ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
		// Scale the view itself
		if (layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT
				&& layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			layoutParams.width *= scale;
		}
		if (layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT
				&& layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			layoutParams.height *= scale;
		}
		// If this view has margins, scale those too
		if (layoutParams instanceof ViewGroup.MarginLayoutParams)
		{
			ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
			marginParams.leftMargin *= scale;
			marginParams.rightMargin *= scale;
			marginParams.topMargin *= scale;
			marginParams.bottomMargin *= scale;
		}
		// Set the layout information back into the view
		root.setLayoutParams(layoutParams);
		// Scale the view's padding
		root.setPadding((int) (root.getPaddingLeft() * scale),
				(int) (root.getPaddingTop() * scale),
				(int) (root.getPaddingRight() * scale),
				(int) (root.getPaddingBottom() * scale));
		// If the root view is a TextView, scale the size of its text

		if (root instanceof TextView)
		{
			TextView textView = (TextView) root;
			textView.setTextSize(textView.getTextSize() * scale);
		}
		// If the root view is a ViewGroup, scale all of its children
		// recursively
		else if (root instanceof ViewGroup)
		{
			ViewGroup groupView = (ViewGroup) root;
			for (int cnt = 0; cnt < groupView.getChildCount(); ++cnt)
				scaleViewAndChildren(groupView.getChildAt(cnt), scale);
		}
	}
	
	public static void scaleViewAndChildren(View root, float scale, float fontS)
	{
		// Retrieve the view's layout information
		ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
		// Scale the view itself
		if (layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT
				&& layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			layoutParams.width *= scale;
		}
		if (layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT
				&& layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			layoutParams.height *= scale;
		}
		// If this view has margins, scale those too
		if (layoutParams instanceof ViewGroup.MarginLayoutParams)
		{
			ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
			marginParams.leftMargin *= scale;
			marginParams.rightMargin *= scale;
			marginParams.topMargin *= scale;
			marginParams.bottomMargin *= scale;
		}
		// Set the layout information back into the view
		root.setLayoutParams(layoutParams);
		// Scale the view's padding
		root.setPadding((int) (root.getPaddingLeft() * scale),
				(int) (root.getPaddingTop() * scale),
				(int) (root.getPaddingRight() * scale),
				(int) (root.getPaddingBottom() * scale));
		// If the root view is a TextView, scale the size of its text
		if (root instanceof TextView)
		{
			TextView textView = (TextView) root;
			textView.setTextSize(textView.getTextSize() * fontS);
		}
		// If the root view is a ViewGroup, scale all of its children
		// recursively
		if (root instanceof ViewGroup)
		{
			ViewGroup groupView = (ViewGroup) root;
			for (int cnt = 0; cnt < groupView.getChildCount(); ++cnt)
				scaleViewAndChildren(groupView.getChildAt(cnt), scale, fontS);
		}
	}
	
	private static void scaleContents(View rootView, View container)
	{
		// Compute the scaling ratio
		float xScale = (float) container.getWidth() / rootView.getWidth();
		float yScale = (float) container.getHeight() / rootView.getHeight();
		float scale = Math.min(xScale, yScale);
		// Scale our contents
		scaleViewAndChildren(rootView, scale);
	}
	
	public static void SetVisibility(View root, boolean vis)
	{
		root.setVisibility(vis ? View.VISIBLE : View.GONE);

		if (root instanceof ViewGroup)
		{
			ViewGroup groupView = (ViewGroup) root;
			for (int cnt = 0; cnt < groupView.getChildCount(); ++cnt)
				SetVisibility(groupView.getChildAt(cnt), vis);
		}
	}
	
	// <uses-permission android:name="android.permission.CALL_PHONE"/>
	public static void Call(String number, Context ctx)
	{
		try
		{
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number));
			ctx.startActivity(callIntent);
		} catch (ActivityNotFoundException e)
		{
			Util.LOG("Util::Call failed, ActivityNotFoundException " + e.toString());
		}
	}
	
	public static long GetFreeExternalMemory()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long availableBlocks;
        if (GetApiLvl() >= 18)
        {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

		return availableBlocks * blockSize;
	}
	
	public static void HideKeyboard(Activity context)
	{
		if (context.getCurrentFocus() == null)
			return;
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
	}

	public static void ShowKeyboard(final View v, final Activity context)
	{
		v.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
			}
		}, 50);
	}

	public static void BuildRoute(final Context context, double latFrom, double lonFrom, double lat, double lon)
	{
		Intent			intent;
		PackageManager	packageManager;
		List<ResolveInfo>	infos;

		intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
		intent.setPackage("ru.yandex.yandexnavi");

		packageManager	= context.getPackageManager();
		infos			= packageManager.queryIntentActivities(intent, 0);

		if (infos == null || infos.size() == 0)
		{
			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(false);
			builder.setMessage("Для построения маршрута требуется Яндекс.Навигатор. Скачать?")
					.setCancelable(false)
					.setPositiveButton("Да",
							new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(final DialogInterface dialog,
										final int id)
								{
									Intent intent2 = new Intent(Intent.ACTION_VIEW);
									intent2.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
									context.startActivity(intent2);
								}
							})
					.setNegativeButton("Нет",
							new DialogInterface.OnClickListener()
							{
								@Override
                                public void onClick(final DialogInterface dialog,
										final int id)
								{
									dialog.dismiss();
								}
							});
			AlertDialog alert = builder.create();
			alert.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

			alert.show();
		}
		else
		{
			if (latFrom != 0 && lonFrom != 0)
			{
				intent.putExtra("lat_from", latFrom);
				intent.putExtra("lon_from", lonFrom);
			}

			intent.putExtra("lat_to", lat);
			intent.putExtra("lon_to", lon);

			context.startActivity(intent);
		}

	}
	
	// прокрутка вью если она в прокручивающемся родителе. Например список в списке
	public static void ScrollHack(View vv)
	{
//        vv.setOnTouchListener(new ListView.OnTouchListener()
//		{
//			@Override
//			public boolean onTouch(View v, MotionEvent event)
//			{
//				int action = event.getAction();
//				switch (action)
//				{
//					case MotionEvent.ACTION_DOWN:
//						// Disallow ScrollView to intercept touch
//						// events.
//						v.getParent()
//								.requestDisallowInterceptTouchEvent(
//										true);
//						break;
//
//					case MotionEvent.ACTION_UP:
//						// Allow ScrollView to intercept touch events.
//						v.getParent()
//								.requestDisallowInterceptTouchEvent(
//										false);
//						break;
//				}
//
//				// Handle ListView touch events.
//				v.onTouchEvent(event);
//				return true;
//			}
//		});

		vv.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View __v, MotionEvent __event)
			{
				if (__event.getAction() == MotionEvent.ACTION_DOWN)
				{
					//  Disallow the touch request for parent scroll on touch of child view
					requestDisallowParentInterceptTouchEvent(__v, true);
				} else if (__event.getAction() == MotionEvent.ACTION_UP || __event.getAction() == MotionEvent.ACTION_CANCEL)
				{
					// Re-allows parent events
					requestDisallowParentInterceptTouchEvent(__v, false);
				}
				return false;
			}
		});

	}

	private static void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept)
	{
		while (__v.getParent() != null && __v.getParent() instanceof View)
		{
			//if (__v.getParent() instanceof ScrollView || __v.getParent() instanceof ListView)
			{
				__v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
			}
			__v = (View) __v.getParent();
		}
	}

	public static int CountChars(String haystack, char needle)
	{
		int count = 0;
		for (char c : haystack.toCharArray())
		{
			if (c == needle)
			{
				++count;
			}
		}
		return count;
	}

	public static void Back(Activity mActivity)
	{
		KeyEvent backEvtDown = new KeyEvent(KeyEvent.ACTION_DOWN,
		  KeyEvent.KEYCODE_BACK);
		KeyEvent backEvtUp = new KeyEvent(KeyEvent.ACTION_UP,
		  KeyEvent.KEYCODE_BACK);
		mActivity.dispatchKeyEvent(backEvtDown);
		mActivity.dispatchKeyEvent(backEvtUp);
	}

	public static String TwoDig(int dig)
	{
		 return String.format("%02d", dig);
	}

	// GetNoun(6, 'яблоко', 'яблока', 'яблок') // Вернет «яблок»
	public static String GetNoun(int dig, String one, String two, String five)
	{
		dig = Math.abs(dig);
		dig %= 100;
		if (dig >= 5 && dig <= 20)
			return five;
		dig %= 10;
		if (dig == 1)
			return one;
		if (dig >= 2 && dig <= 4)
			return two;
		return five;
	}

    /**
     * Find index of occurence of source in strings
     * @param source
     * @param strings
     * @return index of found item or -1 if not found
     */
	public static int FindIn(String source, String[] strings)
	{
		for (int i = 0; i < strings.length; i++)
		{
			if (source.equals(strings[i]))
				return i;
		}
		return -1;
	}

	public static void Rotate(View v, float degree)
	{
	//	if (Build.VERSION.SDK_INT < 11)
	//	{
			RotateAnimation animation = new RotateAnimation(0, degree, 50.0f, 50.0f);
			animation.setDuration(0);
			animation.setFillAfter(true);
			v.startAnimation(animation);
//		} else
//		{
//			v.setRotation(degree);
//		}
	}

	public static int GetLongestSide(Activity context)
	{
		return Util.GetScreenWidth(context) > Util.GetScreenHeight(context) ? Util.GetScreenWidth(context) : Util.GetScreenHeight(context);
	}

	public static String GenGUID()
	{
		return java.util.UUID.randomUUID().toString();
	}

	public static boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

    public static void ShuffleArray(int[] array)
	{
		int index;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			if (index != i)
			{
				array[index] ^= array[i];
				array[i] ^= array[index];
				array[index] ^= array[i];
			}
		}
	}
}
