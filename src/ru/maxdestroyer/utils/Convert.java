/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package ru.maxdestroyer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Convert
{
	public static int ToInt(Object obj)
	{
		if (obj == null || obj.toString().equals(""))
			return 0;

		return Integer.parseInt(obj.toString());
	}

	public static int ToInt(float f)
	{
		return (int)(Math.round(f));
	}

	public static String ToStr(int digit)
	{
		return Integer.toString(digit);
	}

	public static String ToStr(float digit)
	{
		return Float.toString(digit);
	}

	public static String ToStr(double digit)
	{
		return Double.toString(digit);
	}

	public static String ToStr(Object digit)
	{
		return digit.toString();
	}

	public static long ToLong(String s)
	{
		return Long.parseLong(s);
	}

	public static double ToDouble(String s)
	{
		return Double.parseDouble(s);
	}

	public static float ToFloat(String s)
	{
		return Float.parseFloat(s);
	}

	// generic
	public static String[] ObjListToStrArr(ArrayList<String> arr)
	{
		String[] list = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++)
			list[i] = arr.get(i).toString();
		return list;
	}
	
	public static String ListToString(List<? extends Object> arr, boolean ws)
	{
		if (arr.size() > 0)
		{
			StringBuilder nameBuilder = new StringBuilder();

			for (Object n : arr)
				nameBuilder.append(n.toString()).append(ws ? ", " : ",");

			nameBuilder.deleteCharAt(nameBuilder.length() - 1);

			if (ws)
			{
				nameBuilder.deleteCharAt(nameBuilder.length() - 1);
			}

			return nameBuilder.toString();
		} else
			return "";
	}

	// sep = ","
	public static ArrayList<String> StrToArr(String str, String sep)
	{
		String[] arr = str.split(sep);
		return ArrToList(arr);
	}
	
	// json
	public static ArrayList<Object> JSONArrToList(JSONArray arr)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < arr.length(); i++)
			try
			{
				list.add(arr.get(i));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		return list;
	}
	

	public static JSONArray JSONArrRemove(final int idx, final JSONArray from)
	{
		final List<JSONObject> objs = JSONArrToJList(from);
		objs.remove(idx);

		final JSONArray ja = new JSONArray();
		for (final JSONObject obj : objs)
		{
			ja.put(obj);
		}

		return ja;
	}

	public static List<JSONObject> JSONArrToJList(final JSONArray ja)
	{
		final int len = ja.length();
		final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
		for (int i = 0; i < len; i++)
		{
			final JSONObject obj = ja.optJSONObject(i);
			if (obj != null)
			{
				result.add(obj);
			}
		}
		return result;
	}
	
	// js.put("_del", 1);
	public static JSONArray JSONArrClear(final JSONArray from)
	{
		final List<JSONObject> objs = JSONArrToJList(from);
		for (int j = 0; j < objs.size(); j++)
		{
			if (objs.get(j).has("_del"))
			{
				objs.remove(j);
				j--;
			}
		}

		final JSONArray ja = new JSONArray();
		for (final JSONObject obj : objs)
		{
			ja.put(obj);
		}

		return ja;
	}

	public static <T> ArrayList<T> ArrToList(T[] arr)
	{
		return new ArrayList<T>(Arrays.asList(arr));
	}

	public static File BMPToFile(Bitmap bitmap, Context context)
	{
		//create a file to write bitmap data
		final File f = new File(context.getCacheDir(), "temp"+System.currentTimeMillis()+".jpeg");
		try
		{
			f.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// Convert bitmap to byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 85 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		// write the bytes in file
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return f;
	}

	public static File ImageViewToFile(ImageView iv, Context context)
	{
		Drawable dr = iv.getDrawable();
		if (dr == null)
			return null;
		Bitmap bitmap = ((BitmapDrawable)dr).getBitmap();
		// create a file to write bitmap data
		final File f = new File(context.getCacheDir(), "temp"+System.currentTimeMillis()+".jpeg");
		try
		{
			f.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		// Convert bitmap to byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 85 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		// write the bytes in file
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return f;
	}

	public static <T> T[] ListToArr(ArrayList<T> arr)
	{
		final T t = arr.get(0);
		final T[] res = (T[]) Array.newInstance(t.getClass(), arr.size());
		for (int i = 0; i < arr.size(); i++) {
			res[i] = arr.get(i);
		}
		return res;
	}

	public static Object[] ListToArrObj(ArrayList<?> arr)
	{
		Object[] array = arr.toArray(new Object[arr.size()]);
		return array;
	}

	public static byte[] InputStreamToByteArr(InputStream is)
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[4096];

		try
		{
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}
}
