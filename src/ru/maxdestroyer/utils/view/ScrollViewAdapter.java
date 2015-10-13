package net.malahovsky.utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

//<net.malahovsky.utils.view.ScrollViewAdapter
//  android:layout_width="match_parent"
//  android:id="@+id/sv"
//  android:layout_height="wrap_content">
//  <LinearLayout android:layout_width="match_parent"
//  android:orientation="vertical"
//  android:layout_height="wrap_content">
//
//  </LinearLayout>
//  </net.malahovsky.utils.view.ScrollViewAdapter>
public class ScrollViewAdapter extends ScrollView
{
	private LinearLayout layout;
	private int maxHeight = 0;

	public ScrollViewAdapter(Context context)
	{
		super(context);
		init();
	}

	public ScrollViewAdapter(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ScrollViewAdapter(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		setFillViewport(false);
		//layout = (LinearLayout) getChildAt(0);
		layout = new LinearLayout(getContext());
		layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		addView(layout);
	}


	public int count()
	{
		return layout.getChildCount();
	}

	public void Add(View v)
	{
		layout.addView(v);
	}

	public void SetMaxHeight(int mHeight)
	{
		maxHeight = mHeight;
//		ScrollViewAdapter.LayoutParams lp = (LayoutParams) layout.getLayoutParams();
//		lp.height = maxHeight;
//		layout.setLayoutParams(lp);
//		layout.requestLayout();
//		layout.postInvalidate();
		getLayoutParams().height = maxHeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
//		if (maxHeight > 0)
//			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent __event)
	{
//		if (__event.getAction() == MotionEvent.ACTION_DOWN)
//		{
//			//  Disallow the touch request for parent scroll on touch of child view
//			requestDisallowParentInterceptTouchEvent(this, true);
//		} else if (__event.getAction() == MotionEvent.ACTION_UP || __event.getAction() == MotionEvent.ACTION_CANCEL)
//		{
//			// Re-allows parent events
//			requestDisallowParentInterceptTouchEvent(this, false);
//		}
		return super.onTouchEvent(__event);
	}

	//private float xDistance, yDistance, lastX, lastY;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
//		switch (ev.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				xDistance = yDistance = 0f;
//				lastX = ev.getX();
//				lastY = ev.getY();
//				break;
//			case MotionEvent.ACTION_MOVE:
//				final float curX = ev.getX();
//				final float curY = ev.getY();
//				xDistance += Math.abs(curX - lastX);
//				yDistance += Math.abs(curY - lastY);
//				lastX = curX;
//				lastY = curY;
//				//scrollBy((int)xDistance, (int)yDistance);
//		}
//
//		onTouchEvent(ev);
		return super.onInterceptTouchEvent(ev);
	}

	private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept)
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
}
