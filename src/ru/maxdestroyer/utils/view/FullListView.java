package ru.maxdestroyer.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

public class FullListView extends ListView
{
	private android.view.ViewGroup.LayoutParams params;
	private int old_count = 0;
	
	public FullListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FullListView(Context context) 
	{
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		if (getCount() != old_count)
		{
			old_count = getCount();
			params = getLayoutParams();

			if (getChildCount() == getCount())
			{
				for (int i = 0; i < getCount(); ++i)
					params.height = (getChildAt(i).getHeight() + getDividerHeight())
							- getDividerHeight();
			}
			else
			{
				if (old_count > 0 && getCount() > 0 && getChildAt(0) != null)
					params.height = getCount()
							* (getChildAt(0).getHeight() + getDividerHeight())
							- getDividerHeight();
				else
					params.height = 0;
			}
			
			setLayoutParams(params);
		}

		super.onDraw(canvas);
	}
}