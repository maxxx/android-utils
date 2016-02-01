package ru.maxdestroyer.utils.view;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

public class SamplePagerAdapter extends PagerAdapter
{
	public List<View> pages = null;
	private float pageWidth = 1.0f;

	public SamplePagerAdapter(List<View> pages)
	{
		this.pages = pages;
	}
	
	// http://commonsware.com/blog/2012/08/20/multiple-view-viewpager-options.html
	public SamplePagerAdapter(List<View> pages, float _pageWidth)
	{
		this.pages = pages;
		pageWidth = _pageWidth;
	}

	@Override
	public Object instantiateItem(View collection, int position)
	{
		View v = pages.get(position);
		if (position == 0 || position == pages.size())
		{
	        //((ViewPager)collection).setPageMargin(-10);
	        //v.setPadding(-50, 0, -50, 0);
	    }
		// already added
		if (v.getParent() != null)
		{
			//Util.log("SPA:instantiateItem: parent = " + v.getParent().getClass().toString());
			android.support.v4.view.ViewPager vp = (ViewPager) v.getParent();
			vp.removeView(v);
			//return v;
		}
		((ViewPager) collection).addView(v, 0);
		return v;
	}

	@Override
	public void destroyItem(View collection, int position, Object view)
	{
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public int getCount()
	{
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view.equals(object);
	}

	@Override
	public void finishUpdate(View arg0)
	{
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}
	
	@Override public float getPageWidth(int position)
	{
		return pageWidth;
	}
}
