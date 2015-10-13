package net.malahovsky.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by 5 on 04.06.2014.
 */
public abstract class FileUtil
{
	public static void Write(byte[] data, String fileName) throws IOException
	{
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(data);
		out.flush();
		out.close();
	}

	public static void Write(byte[] data, File file) throws IOException
	{
		FileOutputStream out = new FileOutputStream(file);
		out.write(data);
		out.flush();
		out.close();
	}

	public static void Append(byte[] data, File file) throws IOException
	{
		FileOutputStream out = new FileOutputStream(file, true);
		out.write(data);
		out.flush();
		out.close();
	}

	public static void Append(String str, File file) throws IOException
	{
		FileOutputStream out = new FileOutputStream(file, true);
		OutputStreamWriter bos = new OutputStreamWriter(out);
		bos.write(str);
		bos.close();
		out.close();
	}

	public static byte[] Read(String fileName) throws IOException
	{
		File file = new File(fileName);
		int size = (int) file.length();
		byte[] bytes = new byte[size];
		try
		{
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
			buf.read(bytes, 0, bytes.length);
			buf.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return bytes;
	}

	public static byte[] Read(File file) throws IOException
	{
		int size = (int) file.length();
		byte[] bytes = new byte[size];
		try
		{
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
			buf.read(bytes, 0, bytes.length);
			buf.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return bytes;
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = new ObjectOutputStream(bos);
//		oos.writeObject(file);
//		bos.close();
//		oos.close();
//		return bos.toByteArray();
	}

	public static void SaveBitmap(Bitmap result, String path)
	{
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(path);
			result.compress(Bitmap.CompressFormat.JPEG, 85, out);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static Bitmap LoadBitmap(String imgPath)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		try
		{
			return BitmapFactory.decodeFile(imgPath, options);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
