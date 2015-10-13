package ru.maxdestroyer.utils.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class CapTextView extends TextView //implements ViewTreeObserver.OnPreDrawListener
{

	public CapTextView(Context context)
	{
		super(context);
	}

	public CapTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public CapTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type)
	{
		super.setText(text.toString().toUpperCase(), type);

		if (text.length() == 0) 
			setVisibility(View.GONE);
	}
}
