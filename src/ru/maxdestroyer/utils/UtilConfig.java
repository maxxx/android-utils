/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

@SuppressLint("CommitPrefEdits")
public class UtilConfig {
	private SharedPreferences sPref;
	private Editor ed;
	private boolean commit = true;
	private static UtilConfig instance = null;

	public static UtilConfig getInstance() {
		if (instance == null) {
			instance = new UtilConfig();
		}
		return instance;
	}

	private UtilConfig() {
	}

	public UtilConfig init(@NonNull Context c) {
		if (isInitialized()) {
			Log.v("UtilConfig", "already initialized");
		}
		sPref = c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE);
		ed = sPref.edit();
		return this;
	}

	public boolean isInitialized() {
		return sPref != null;
	}

	public void Save(String name, String val) {
		ed.putString(name, val);
		if (commit) {
			ed.commit();
		}
	}

	public void Save(String name, int val) {
		ed.putInt(name, val);
		if (commit) {
			ed.commit();
		}
	}

	public void Save(String name, long val) {
		ed.putLong(name, val);
		if (commit) {
			ed.commit();
		}
	}

	public void Save(String name, double val)
	{
		ed.putFloat(name, (float) val);
		if (commit) {
			ed.commit();
		}
	}

	public void Save(String name, float val) {
		ed.putFloat(name, val);
		if (commit) {
			ed.commit();
		}
	}

	public void Save(String name, boolean val) {
		ed.putBoolean(name, val);
		if (commit) {
			ed.commit();
		}
	}

	private void SaveObj(String name, Object object) {
		if (object instanceof String) {
			Save(name, (String) object);
		} else if (object instanceof Integer) {
			Save(name, (int) (Integer) object);
		} else if (object instanceof Long) {
			Save(name, (long) (Long) object);
		} else if (object instanceof Float) {
			Save(name, (float) (Float) object);
		} else if (object instanceof Double) {
			Save(name, (double) (Double) object);
		} else if (object instanceof Boolean) {
			Save(name, (boolean) (Boolean) object);
		} else if (object != null && object.getClass() != null) {
			Util.log("CFG::Save with unk object " + object.getClass().getCanonicalName());
		}
	}

	private Object LoadObj(String name) {
		Map<String, ?> keys = sPref.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			if (entry.getKey().equals(name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public String Load(String name) {
		return sPref.getString(name, "");
	}
	// не получится так, надо еще сохранять тип
//	public <T> T Load(String name)
//	{
//		return (T) sPref.getString(name, "");
//	}

	public int LoadInt(String name)
	{
		return sPref.getInt(name, 0);
	}

	public Long LoadLong(String name)
	{
		try {
			return sPref.getLong(name, 0L);
		} catch (ClassCastException ex) {
			return (long) sPref.getInt(name, 0);
		}
	}

	public boolean LoadBool(String name) {
		return sPref.getBoolean(name, false);
	}

	public float LoadFloat(String name) {
		return sPref.getFloat(name, 0.0f);
	}

	public float LoadDouble(String name)
	{
		return sPref.getFloat(name, 0.0f);
	}

	public ArrayList<Object> LoadArray(String name) {
		ArrayList<Object> arr = new ArrayList<Object>();
		int size = LoadInt(name + "_size");

		for (int i = 0; i < size; ++i) {
			arr.add(LoadObj(name + i));
		}

		return arr;
	}

	public ArrayList<String> LoadArrayS(String name) {
		ArrayList<String> arr = new ArrayList<String>();
		int size = LoadInt(name + "_size");

		for (int i = 0; i < size; ++i) {
			arr.add(Load(name + i));
		}

		return arr;
	}

	public void SaveArray(String name, ArrayList<?> arr) {
		commit = false;

		Object[] array = Convert.listToArrObj(arr);
		for (int i = 0; i < array.length; ++i) {
			SaveObj(name + i, array[i]);
		}

		commit = true;

		Save(name + "_size", arr.size());
	}

//	public String LoadFromFile(Context c, String name, String fName)
//	{
//		return sPref.getString(name, "");
//	}
//	
//	public void SaveToFile(Context c, String name, String val, String fName)
//	{
//		File f = new File(c.getApplicationInfo().dataDir + fName);
//		
//		try
//		{
//			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
//			writer.write(name + "::" + val);
//			writer.close();
//		} catch (IOException e)
//		{
//			Util.log(e.toString());
//			e.printStackTrace();
//		}
//	}
}
