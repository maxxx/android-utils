package net.malahovsky.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class MyFlipper extends ViewFlipper
{
	public FlipListener listener;
	public MyFlipper(Context context)
	{
		super(context);
	}

	public MyFlipper(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public void setDisplayedChild(int whichChild)
	{
		super.setDisplayedChild(whichChild);
		if (listener != null)
			listener.flip(whichChild);
	}

	public interface FlipListener
	{
		void flip(int newChild);
	}

}