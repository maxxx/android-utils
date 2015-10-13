package ru.maxdestroyer.utils.imgcache;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.maxdestroyer.utils.Util;

// object - �� ����, Bitmap - �� �����. Void - ?
public class AsyncLoader extends AsyncTask<Object, Void, Bitmap>
{
	private ImageView imv;
	private int width = 0, height = 0;
	private ImageLoader il = null;
	private String img_addr = "";
	boolean resize = false;
	boolean useThumb = false;
	int h = 0;
	int w = 0;

	// imv, img_addr, path, this}
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected Bitmap doInBackground(Object... iv)
	{
		imv = (ImageView) iv[0];
		img_addr = (String)iv[1];
		String path = (String) iv[2];
		il = (ImageLoader)iv[3];
		if (iv.length > 4)
			resize = (Boolean) iv[4];
		if (iv.length > 5)
			useThumb = (Boolean) iv[5];
		if (iv.length > 6)
			width = (Integer) iv[6];
		if (iv.length > 7)
			height = (Integer) iv[7];
		
		if (useThumb)
		{
			if (Util.GetApiLvl() >= 16)
			{
				h = imv.getMaxHeight();
				w = imv.getMaxWidth();
			} else
			{
				try
				{
					Field maxWidthField = ImageView.class
							.getDeclaredField("mMaxWidth");
					Field maxHeightField = ImageView.class
							.getDeclaredField("mMaxHeight");
					maxWidthField.setAccessible(true);
					maxHeightField.setAccessible(true);

					int maxw = (Integer) maxWidthField.get(imv);
					int maxh = (Integer) maxHeightField.get(imv);
					h = maxh;
					w = maxw;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		Bitmap temp = DownloadImg();
		il.b = temp;
		
		if (useThumb)
			ImageLoader.CacheImageThumb(il.b, path, img_addr.hashCode(), w, h);
		//else ������ ������ ���� ������, �����������
			ImageLoader.CacheImage(il.b, path, img_addr.hashCode());
		
		return il.b;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
    protected void onPostExecute(Bitmap b)
	{	
		if (b == null)
			return;
		
		imv.setImageBitmap(b);
		imv.setTag(1);
		if (resize)
		{
			int h = 0;
			int w = 0;
			if (Util.GetApiLvl() >= 16)
			{
				h = b.getHeight() < imv.getMaxHeight() ? b.getHeight() : imv
						.getMaxHeight();
				w = b.getWidth() < imv.getMaxWidth() ? b.getWidth() : imv
						.getMaxWidth();
			} else
			{
				try
				{
					Field maxWidthField = ImageView.class
							.getDeclaredField("mMaxWidth");
					Field maxHeightField = ImageView.class
							.getDeclaredField("mMaxHeight");
					maxWidthField.setAccessible(true);
					maxHeightField.setAccessible(true);

					int maxw = (Integer) maxWidthField.get(imv);
					int maxh = (Integer) maxHeightField.get(imv);
					h = b.getHeight() < maxh ? b.getHeight() : maxh;
					w = b.getWidth() < maxw ? b.getWidth() : maxw;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			imv.getLayoutParams().height = h;
			imv.getLayoutParams().width = w;
		}
    }

	private Bitmap DownloadImg()
	{
		try 
		{
//			Bitmap res = null;
//			URL url;
//			url = new URL(img_addr);
//
//			HttpURLConnection c = (HttpURLConnection) url.openConnection();
//			c.setDoInput(true);
//			c.connect();
//			InputStream is = c.getInputStream();
//
//			res = BitmapFactory.decodeStream(is);
//			is.close();
//
////			if (useThumb)
////				res = ImageLoader.Resize(res, w, h);
//
//			return res;
			Bitmap res = null;
			URL url;
			url = new URL(img_addr);

			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setDoInput(true);
			c.connect();
			InputStream is = c.getInputStream();
			InputStream is1 = null, is2 = null;
			byte[] data = InputStreamTOByte(is);
			try
			{
				is1 = byteTOInputStream(data);
				is2 = byteTOInputStream(data);
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is1, null, options);
			is1.close();
			options.inJustDecodeBounds = false;

			float scale = calculateInSampleSize(options, width, height);

			if (width > 0 && height > 0)
			{
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inSampleSize = (int) scale; // двойке памяти не хватает. Больше двух? 4, 8...
			}

			res = BitmapFactory.decodeStream(is2, null, options);
			is2.close();
			if (width > 0 && height > 0)
				res = Scale(res, scale, true);

			return res;
		} catch (Exception e) 
		{
			Log.e("AsyncLoader", "error! URL: " + img_addr + ". Error: " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	
	public static float calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		float inSampleSize = 1.0f;

		if (height > reqHeight || width > reqWidth)
		{
			float heightRatio = (float) height / (float) reqHeight;
			float widthRatio = (float) width / (float) reqWidth;

			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;//Math.round(inSampleSize + 0.5f);
	}
	
	public static float calculateInSampleSize(int height, int width, int reqWidth, int reqHeight) 
	{
		float inSampleSize = 1.0f;
		if (reqWidth == 0 || reqHeight == 0)
			return 1.0f;

		if (height > reqHeight || width > reqWidth)
		{
			float heightRatio = (float) height
					/ (float) reqHeight;
			float widthRatio = (float) width / (float) reqWidth;

			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;//
	}
	
	public static float calculateInSampleSizeH(int height, int reqHeight) 
	{
		float inSampleSize = 1.0f;
		if (reqHeight == 0)
			return 1.0f;

		if (height > reqHeight)
		{
			float heightRatio = (float) height
					/ (float) reqHeight;

			inSampleSize = heightRatio;
		}

		return inSampleSize;
	}

//	Passing filter = false will result in a blocky, pixellated image.
//	Passing filter = true will give you smoother edges.
	public static Bitmap Scale(Bitmap bmp, float scale, boolean filtering)
	{
		int width  = (int) (bmp.getWidth() / scale + 0.5f);
		int height = (int) (bmp.getHeight() / scale + 0.5f);
		return Bitmap.createScaledBitmap(bmp, width, height, filtering);
	}
	
	public static Bitmap ScaleH(Bitmap bmp, float scale, boolean filtering)
	{
		int height = (int) (bmp.getHeight() / scale + 0.5f);
		return Bitmap.createScaledBitmap(bmp, bmp.getWidth(), height, filtering);
	}
	
	public byte[] InputStreamTOByte(InputStream in) throws IOException
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024 * 16];
		int count = -1;
		while ((count = in.read(data, 0, 1024 * 16)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return outStream.toByteArray();
	}

	public InputStream byteTOInputStream(byte[] in) throws Exception
	{  
	    ByteArrayInputStream is = new ByteArrayInputStream(in);  
	    return is;  
	}
}
