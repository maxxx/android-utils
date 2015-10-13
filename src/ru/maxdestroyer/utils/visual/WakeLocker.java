package ru.maxdestroyer.utils.visual;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;

@SuppressLint("Wakelock")
public abstract class WakeLocker
{
	private static PowerManager.WakeLock wakeLock;

	@SuppressWarnings("deprecation")
	public static void acquire(Context ctx)
	{
		KeyguardManager m_keyguardManager	= (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock m_keyguardLock		= m_keyguardManager.newKeyguardLock("TAG");
		
		if (wakeLock != null)
			wakeLock.release();

		PowerManager pm = (PowerManager) ctx
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "WakeLock");
		wakeLock.acquire();		
		m_keyguardLock.disableKeyguard();
	}

	public static void release()
	{
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}
}