package net.malahovsky.utils.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class UtilDial extends Dialog
{
	public String misc;
	public Integer misc2;
	public boolean noTitle = true;

	public UtilDial(Context context)
	{
		super(context);
		if (noTitle)
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public UtilDial(Context context, int theme)
	{
		super(context, theme);
	}

	protected UtilDial(Context context, boolean cancelable, OnCancelListener cancelListener)
	{
		super(context, cancelable, cancelListener);
	}

	public <T> T f(int id)
	{
		return (T)findViewById(id);
	}
}
