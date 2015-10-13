package ru.maxdestroyer.utils.imgcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 5 on 31.01.14.
 */
public class ImageMgr
{
	private HashMap<String, Bitmap> mBitmaps;
	private HashMap<String, Drawable> mDrawables;
	private Context mContext;

	private boolean mActive = true;

	public ImageMgr(Context c)
	{
		mBitmaps = new HashMap<String, Bitmap>();
		mDrawables = new HashMap<String, Drawable>();
		mContext = c;
	}

	// We need to share and cache resources between objects to save on memory.
	public Bitmap getBitmap(String id)
	{
		return mBitmaps.get(id);
	}

	public void AddBitmap(String id, Bitmap bmp)
	{
		if (!mBitmaps.containsKey(id))
			mBitmaps.put(id, bmp);
	}

	public Drawable getDrawable(String id)
	{
		return mDrawables.get(id);
	}

	public void AddDrawable(String id, Drawable bmp)
	{
		if (!mDrawables.containsKey(id))
			mDrawables.put(id, bmp);
	}

	public void Recycle()
	{
		Iterator itr = mBitmaps.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry e = (Map.Entry) itr.next();
			((Bitmap) e.getValue()).recycle();
		}
		mBitmaps.clear();
	}
}
