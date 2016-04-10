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
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.maxdestroyer.utils.net.HostChecker;
import ru.maxdestroyer.utils.visual.WakeLocker;

@SuppressLint({ "NewApi", "ServiceCast" })
@SuppressWarnings("unused")
public abstract class Util
{
    public static void msg(Context c, Object text) {
		Toast.makeText(c, String.valueOf(text), Toast.LENGTH_SHORT).show();
	}

    /**
     * @param text - will be parsed as string
     */
    public static void msgt(Context c, Object text, int seconds) {
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
    public static void msgm(final Activity c, final Object text) {
		c.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(c, String.valueOf(text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void goURL(Context c, String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		c.startActivity(browserIntent);
	}

	// <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	// udid
    public static String getDeviceId(Context c) {
		TelephonyManager tManager = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		String devId = tManager.getDeviceId();
		if (devId == null || devId.equals("000000000000000")) // эмулятор. Тогда Android Id
			devId = Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
		return devId;
    }

    public static String getSimId(Context c) {
		TelephonyManager tManager = (TelephonyManager) c
		  .getSystemService(Context.TELEPHONY_SERVICE);
		String devId = tManager.getSimSerialNumber() != null ? tManager.getSimSerialNumber() : "";
		return devId;
	}

	// TODO: skype... ?
    public static boolean canCall(Context c) {
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

    public static String getOSVersion() {
		return "Android " + Build.VERSION.RELEASE;
	}
	
	public static boolean IsDvoika()
	{
		return Build.VERSION.SDK_INT < 11;
    }

    public static int getApiLvl() {
		return Build.VERSION.SDK_INT;
    }

    public static String getDeviceName() {
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
    public static boolean hasInternet(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) // нет активного соединения
			return false;

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static boolean hasWiFi(Context c) {
		ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return mWifi.isConnected();
    }

    public static int getScreenWidth(Activity c) {
		Display display = c.getWindowManager().getDefaultDisplay();
		final DisplayMetrics metrics = new DisplayMetrics(); 
		// only 14 15 16
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
		{
			try
			{
				Method mGetRawW = Display.class.getMethod("getRawWidth");
				return (Integer) mGetRawW.invoke(display);
			} catch (Exception e) {
                Log.e("getScreenWidth", "error!");
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

    public static int getScreenHeight(Activity c) {
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
			} catch (Exception e) {
                Log.e("getScreenHeight", "error!");
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

    public static boolean checkHost(String url) {
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

    public static void log(Object txt) {
        Log.e("Util.log", txt + "");
    }

    public static void logv(Object txt) {
        Log.v("Util.log", txt + "");
    }

	@SuppressLint({ "NewApi", "ServiceCast" })
	@SuppressWarnings("deprecation")
    public static void toClipboard(Context c, String text) {
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
    public static String getClipboard(Context c) {
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

	@SuppressWarnings({ "deprecation", "rawtypes"})
    public static void notify(final Context con, String title, String text,
                              Class activityToRun, Bundle extras, boolean vib) {
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
					.setSmallIcon(R.drawable.ic_launcher_def).setContentIntent(pIntent);
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
	
	@SuppressWarnings({ "deprecation", "rawtypes"})
    public static void notify(final Context con, String title, String text,
                              Class activityToRun, Bundle extras, boolean vib, int id) {
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
					.setSmallIcon(R.drawable.ic_launcher_def).setContentIntent(pIntent);
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

    public static boolean isTablet(Context activityContext) {
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

    public static int getDensity(Context c) {
		DisplayMetrics metrics = new DisplayMetrics();
		Activity activity = (Activity) c;
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
		// DENSITY_TV=213, DENSITY_XHIGH=320
		return metrics.densityDpi;
    }

    public static double getScreenSize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);

        double width = ((double) getScreenWidth(activity) / (double) displayMetrics.densityDpi);
        double height = ((double) getScreenHeight(activity) / (double) displayMetrics.densityDpi);

		double screenDiagonal = Math.sqrt(width * width + height * height);
		return screenDiagonal;
    }

    /**
     * @return dd.MM.yyyy
     */
    public static String getCurDate() {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	    Calendar cal = Calendar.getInstance();

	    return dateFormat.format(cal.getTime());
	}

    /**
     * visible/gone
     * @param v
     */
    public static void toggleVisibility(View v) {
		if (v.getVisibility() == View.VISIBLE)
			v.setVisibility(View.GONE);
		else
			v.setVisibility(View.VISIBLE);
	}

    /**
     * visible/invisible
     * @param v
     */
    public static void toggleVisibility2(View v) {
		if (v.getVisibility() == View.VISIBLE)
			v.setVisibility(View.INVISIBLE);
		else
			v.setVisibility(View.VISIBLE);
	}

    public static boolean isLand(Context c) {
		return c.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT;
	}

    public static void expandList(ListView listView, boolean noAdapter) {
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

    public static void expandList(ListView listView, int maxHeight) {
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
    public static void expandListPlus(ListView listView, int maxHeight, int addHeight) {
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
    public static void expandList(ListView listView, int maxHeight, int listWidth) {
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

    public static void expandListItem(ListView lv, int visibleItems) {
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

    public static void expandGridItem(GridView lv, int rows, int height) {
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
            int pad = ImageUtils.dpToPix(lv.getContext(), 10);
            if (getApiLvl() >= 16)
                pad = lv.getVerticalSpacing();
			lv.getLayoutParams().height += pad * (rows-1);
		}
	}

    public static String getPhoneNumber(Context c) {
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
    public static boolean isTaskRunning(Context ctx) {
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
    public static ArrayList<String> regExp(String _pattern, String source) {
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

    public static boolean regExpMatch(String _pattern, String source) {
		ArrayList<String> res = new ArrayList<String>();
		Pattern pattern = Pattern.compile(_pattern);
		Matcher matcher = pattern.matcher(source);
		return matcher.matches();
	}

	public static int currentTimeMillis()
	{
		return (int) (System.currentTimeMillis() / 1000L & 0x00000000FFFFFFFFL);
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

    public static boolean isEmulator() {
		return Build.FINGERPRINT.contains("generic");
	}
	
	// 0(прозрач) .. 1(норм)
    public static void setOpacity(View v, float opacity) {
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

    public static void setVisibility(View root, boolean vis) {
		root.setVisibility(vis ? View.VISIBLE : View.GONE);

		if (root instanceof ViewGroup)
		{
			ViewGroup groupView = (ViewGroup) root;
			for (int cnt = 0; cnt < groupView.getChildCount(); ++cnt)
                setVisibility(groupView.getChildAt(cnt), vis);
        }
	}
	
	// <uses-permission android:name="android.permission.CALL_PHONE"/>
    public static void call(String number, Context ctx) {
		try
		{
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number));
			ctx.startActivity(callIntent);
		} catch (ActivityNotFoundException e)
		{
            Util.log("Util::call failed, ActivityNotFoundException " + e.toString());
        }
	}

    public static void sendEmail(Context context, String to, String subject, String text) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        i.putExtra(Intent.EXTRA_SUBJECT, to);
        i.putExtra(Intent.EXTRA_TEXT, text);
        try {
            context.startActivity(Intent.createChooser(i, "Отправить email через"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getFreeExternalMemory() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long availableBlocks;
        if (getApiLvl() >= 18) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

		return availableBlocks * blockSize;
	}

    public static void hideKeyboard(Activity context) {
		if (context.getCurrentFocus() == null)
			return;
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
	}

    public static void showKeyboard(final View v, final Activity context) {
		v.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(v, 0);
			}
		}, 50);
	}

    /**
     * calls external app (yandexnavi)
     */
    public static void buildRoute(final Context context, double latFrom, double lonFrom, double lat, double lon) {
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
    public static void scrollHack(View vv) {
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

    public static int countChars(String haystack, char needle) {
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

    /**
     * can use with askEnablingGPS(ctx);
     *
     * @param ctx
     * @return
     */
    public static boolean isGPSEnabled(Context ctx) {
        final LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void askEnablingGPS(
            final Activity activity) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service for location logs.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public static void back(Activity mActivity) {
		KeyEvent backEvtDown = new KeyEvent(KeyEvent.ACTION_DOWN,
		  KeyEvent.KEYCODE_BACK);
		KeyEvent backEvtUp = new KeyEvent(KeyEvent.ACTION_UP,
		  KeyEvent.KEYCODE_BACK);
		mActivity.dispatchKeyEvent(backEvtDown);
		mActivity.dispatchKeyEvent(backEvtUp);
	}

    // ?
    public static String TwoDig(int dig)
	{
		 return String.format("%02d", dig);
	}

    // getNoun(6, 'яблоко', 'яблока', 'яблок') // Вернет «яблок»
    public static String getNoun(int dig, String one, String two, String five) {
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
    public static int findIn(String source, String[] strings) {
		for (int i = 0; i < strings.length; i++)
		{
			if (source.equals(strings[i]))
				return i;
		}
		return -1;
	}

    public static void rotateView(View v, float degree) {
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

    public static int getLongestSide(Activity context) {
        return Util.getScreenWidth(context) > Util.getScreenHeight(context) ? Util.getScreenWidth(context) :
                Util.getScreenHeight(
                        context);
    }

    public static String genGUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

    public static void shuffleArray(int[] array) {
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
