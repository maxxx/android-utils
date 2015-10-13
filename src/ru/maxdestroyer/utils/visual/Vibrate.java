package ru.maxdestroyer.utils.visual;

import android.content.Context;
import android.os.Vibrator;

// <uses-permission android:name="android.permission.VIBRATE" />
// new Vibrate(getApplicationContext(), 500);
public class Vibrate
{
	public Vibrate(Context c, int time)
	{
		Vibrator mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		mVibrator.vibrate(time);
	}
}
