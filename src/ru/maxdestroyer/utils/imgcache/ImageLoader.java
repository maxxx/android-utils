package ru.maxdestroyer.utils.imgcache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ru.maxdestroyer.utils.Convert;
import ru.maxdestroyer.utils.Util;

public class ImageLoader // extends AsyncTask<Object, Void, Bitmap>
{
	private int width, height;
	private ImageView imv;
	public Bitmap b = null;
	private String img_addr = "";
	public String path  = "";
	
	// (iv, url, path, resize, thumb, width, height);
	public ImageLoader(Object... iv)
	{
		imv = (ImageView)iv[0];
		img_addr = (String)iv[1];
		path = (String) iv[2];
		boolean resize = false;
		if (iv.length > 3)
			resize = (Boolean) iv[3];
		boolean useThumb = false;
		if (iv.length > 4)
			useThumb = (Boolean) iv[4];
		if (iv.length > 5)
			width = (Integer) iv[5];
		if (iv.length > 6)
			height = (Integer) iv[6];

		if (img_addr.equals(""))
		{
			imv.setImageBitmap(null);
			return;
		}
		
		final boolean u = useThumb;
		final boolean r = resize;
		String fname = Convert.ToStr(img_addr.hashCode());
		if (u)
			fname = "thumb_" + fname;
		b = LoadCachedImage(fname, path);
		
		if (b != null)
		{
			imv.setImageBitmap(b);
			imv.setTag(1);
		} else // � ���� ����
		{
			Object[] obj = new Object[] {imv, img_addr, path, this, r, u, width, height};
	        new AsyncLoader().execute(obj);
		}
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				String fname = Util.ToStr(img_addr.hashCode());
//				if (u)
//					fname = "thumb_" + fname;
//				b = LoadCachedImage(fname, path);
//				
//				if (b != null)
//				{
//					imv.post(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							imv.setImageBitmap(b);
//						}
//					});
//				} else // � ���� ����
//				{
//					Object[] obj = new Object[] {imv, img_addr, path, this, r, u};
//			        new AsyncLoader().execute(obj);
//				}
//			}
//		}).start();

	}
	
	public static void CacheImage(Bitmap bmp, String path, Object name)
	{
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		    return;
		
		String root = Environment.getExternalStorageDirectory().toString();
	    File myDir = new File(root + path);
		if (!myDir.exists())
	    	myDir.mkdirs();
		String hashname = name + ".jpeg";
		//Log.e("cache", hashname + "___" + bmp.getWidth());
		
		try 
		{
			FileOutputStream out = new FileOutputStream(new File (myDir, hashname));
			//bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, out);
			out.flush();
			out.close();
	    } catch (Exception e)
	    {
	    	Log.e("CacheImage error", "a: " + name + ". " + e.toString());
			e.printStackTrace();
	    }
	}
	
	public static void CacheImageThumb(Bitmap bmp, String path, Object name, int maxw, int maxh)
	{
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		    return;
		
		String root = Environment.getExternalStorageDirectory().toString();
	    File myDir = new File(root + path);
		if (!myDir.exists())
	    	myDir.mkdirs();
		String hashname = "thumb_" + name.toString() + ".jpeg";
		//Log.e("cache", hashname + "___" + bmp.getWidth());
		
		try 
		{
			bmp = Resize(bmp, maxw, maxh);
			FileOutputStream out = new FileOutputStream(new File (myDir, hashname));
			bmp.compress(Bitmap.CompressFormat.JPEG, 85, out);
			out.flush();
			out.close();
	    } catch (Exception e)
	    {
	    	Log.e("CacheImageThumb error", "a: " + name + ". " + e.toString());
			e.printStackTrace();
	    }
	}
	
//	public Bitmap CacheImage(InputStream input)
//	{
//		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
//		    return null;
//		
//		String root = Environment.getExternalStorageDirectory().toString();
//	    File myDir = new File(root + MainFrame.PATH);    
//	    myDir.mkdirs();
//		String hashname = Integer.toString(img_addr.hashCode()) + ".jpeg";
//		File file = new File (myDir, hashname);
//		
//		try 
//		{
//			FileOutputStream out = new FileOutputStream(file);
//			byte data[] = new byte[1024];
//			int count;
//            while ((count = input.read(data)) != -1)
//            {
//            	out.write(data, 0, count);
//            }
//			out.flush();
//			out.close();
//			input.close();
//	    } catch (Exception e)
//	    {
//			e.printStackTrace();
//	    }
//		
//		return LoadCachedImage(img_addr.hashCode());
//	}
//	
	public static Bitmap LoadCachedImage(String fileName, String path)
	{
		Bitmap bmp = null;
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		    return bmp;
		
		String root = Environment.getExternalStorageDirectory().toString();
		fileName = fileName + ".jpeg";
	    File f = new File(root + path + "/", fileName);
	    if (!f.exists())
	    {
	    	Util.LOG("LoadCachedImage: file not found - " + f.getAbsolutePath());
	    	return bmp;
	    }
	    String fullPath = f.getAbsolutePath();
	    
	    //float sc = LoadScale(hash, suffix);
		
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    //if (Build.PRODUCT.equals("sdk_x86")) // �� ��������� ������ ������ �� �������!
	    	options.inPreferredConfig = Bitmap.Config.RGB_565;
	    //else 
	    	//options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    //options.inSampleSize = 2;
	    try
	    {
	    	bmp = BitmapFactory.decodeFile(fullPath, options);
	    } catch (Exception e)
	    {
	    	Log.e("LoadCachedImage error", e.toString());
	    	e.printStackTrace();
	    }
		return bmp;
	}
	
	public static boolean IsExists(String hash, String path)
	{
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		    return false;
		
		String root = Environment.getExternalStorageDirectory().toString();
	    String filename = hash + ".jpeg";
	    File f = new File(root + path + "/", filename);
	    if (!f.exists())
	    	return false;

	    return true;
	}
	
	public static Bitmap Resize(Bitmap oldBmp, int w, int h) throws FileNotFoundException
	{
		// Find the correct scale value. It should be the power of 2.
		int width_tmp = oldBmp.getWidth(), height_tmp = oldBmp.getHeight();
		int scale = 1;
		while (true)
		{
			if (width_tmp / 2 < w || height_tmp / 2 < h)
			{
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		return Bitmap.createScaledBitmap(oldBmp, width_tmp, height_tmp, false);
	}
}
