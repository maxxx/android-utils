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
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class DBWorker extends SQLiteOpenHelper implements BaseColumns
{
	private static final String DATABASE_NAME = "app.db";
	private static final int DATABASE_VERSION = 1;
	protected SQLiteDatabase db = null;
	private boolean firstRun = false;
	
	// ������� "x"
	public static final String TABLE_X = "x";
	//public static final String CATEGORY_ID = "_id"; // ��� "_"  �� ����� �������� ������� � ���� ������, ���������� �� ��������.
	public static final String X_NAME = "name";
	
	private static final String CREATE_X = "CREATE TABLE "
			+ TABLE_X + " (" + DBWorker._ID + " INTEGER PRIMARY KEY,"
			+ X_NAME + " VARCHAR(255)" +
			");";
	
	public DBWorker(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		if (NeedRefill())
			context.deleteDatabase(DATABASE_NAME);
		db = getWritableDatabase();
		db.setVersion(DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase _db)
	{
		FillInitialData(_db);
		firstRun = true;
	}
	
	public abstract void FillInitialData(SQLiteDatabase _db);

	// ��� ������ ��������� �������� ���������� ������ ����, ��� ��������� ����� onUpgrade()
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// ������� ���������� ������� ��� ��������
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);
//		// ������ ����� ��������� �������
//		onCreate(db);
	}
	
	/*
	 *  ��� �������� �������������
	 */
	
	public void Truncate(String tableName)
	{
		// SQLite �� ������������ ����� TRUNCATE.
		db.execSQL("DELETE FROM " + tableName);
		// ����� ������ ���������� ����� (����������)
		db.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + tableName + "'");
	}
	
	public Cursor Query(String q)
	{
		Cursor res = db.rawQuery(q, null);
		return res;
	}

	public AbstractWindowedCursor QueryTyped(String q)
	{
		Cursor c = Query(q);

		if (c == null || !c.moveToFirst())
			return null;
		CursorWrapper cw = new CursorWrapper(c);

		Class<?> cursorWrapper = CursorWrapper.class;
		Field mCursor;
		try
		{
			mCursor = cursorWrapper.getDeclaredField("mCursor");
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			cw.close();
			return null;
		}
		mCursor.setAccessible(true);
		AbstractWindowedCursor abstractWindowedCursor;
		try
		{
			abstractWindowedCursor = (AbstractWindowedCursor) mCursor.get(cw);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

//		CursorWindow cursorWindow = abstractWindowedCursor.getWindow();
//		int pos = abstractWindowedCursor.getPosition();
//		for (int i = 0; i < c.getColumnCount(); ++i)
//		{
//			// String type = null;
//			if (cursorWindow.isNull(pos, i))
//			{
//				// type = "Cursor.FIELD_TYPE_NULL";
//			} else if (cursorWindow.isLong(pos, i))
//			{
//				tests.add(c.getInt(i));
//			} else if (cursorWindow.isFloat(pos, i))
//			{
//				// type = "Cursor.FIELD_TYPE_FLOAT";
//			} else if (cursorWindow.isString(pos, i))
//			{
//				tests.add(c.getString(i));
//			} else if (cursorWindow.isBlob(pos, i))
//			{
//				// type = "Cursor.FIELD_TYPE_BLOB";
//			}
//		}
//		c.close();

		return abstractWindowedCursor;
	}
	
	public void Execute(String q) 
	{
		db.execSQL(q);
	}
	
	public String GetValue(String q)
	{
		Cursor c = null;
		String res = "";
		c = db.rawQuery(q, null);
		if (c.moveToNext())
			res = c.getString(0);
		c.close();
		
		return res;
	}
	
	public String GetValue(String q, String column)
	{
		Cursor c = null;
		String res = "";
		c = db.rawQuery(q, null);
		if (c.moveToNext())
			res = c.getString(c.getColumnIndex(column));
		c.close();
		
		return res;
	}

	@Override
	public synchronized void close()
	{
		db.close();
		super.close();
	}
	
	public boolean IsOpen()
	{
		return db.isOpen();
	}

	public boolean IsClean() 
	{
		return firstRun;
	}
	
	private static String E(String query)
	{
		return "'" + query + "'";
	}
	
	private static String Percent(String query)
	{
		return query.replace("%", "__");
	}
	
	// string format "insert into '%s' values (%u, %i)"
	public static String MakeQ(String query, Object... arg)
	{
		query = query.replace("%s", "'%s'");
		// �������� :(
		// Use d for int instead of i and f and for double instead of d in the following line
		query = query.replace("%u", "%d");
		query = query.replace("%i", "%d");

		for (int i = 0; i < arg.length; ++i)
		{
			if (arg[i] instanceof String)
			{
				arg[i] = Percent(((String)arg[i]));
				//arg[i] = E(((String)arg[i]));
			}
		}
		// ������ ������ - LIKE %...%
		query = query.replaceAll("LIKE[ ]?'%(.*)%'", "LIKE '__$1__'");
		query = String.format(query, arg);
		// ���������� ��������
		query = query.replaceAll("LIKE[ ]?'__(.*)__'", "LIKE '%$1%'");
		return query;
	}
	
	// �������� ���� �����, ������� �������
	public ArrayList<String> GetValues(String q)
	{
		Cursor c = Query(q);
		ArrayList<String> tests = new ArrayList<String>();

		if (c != null && c.moveToFirst())
		{
			while (!c.isAfterLast())
			{
				tests.add(c.isNull(0) ? "" : c.getString(0));
				c.moveToNext();
			}
			c.close();
		}

		return tests;
	}

	public ArrayList<Pair<String, String>> GetPairValues(String q)
	{
		Cursor c = Query(q);
		ArrayList<Pair<String, String>> tests = new ArrayList<Pair<String, String>>();

		if (c != null && c.moveToFirst())
		{
			while (!c.isAfterLast())
			{
				Pair<String, String> p = new Pair<String, String>(c.isNull(0) ? "" : c.getString(0), c.isNull(1) ? "" : c.getString(1));
				tests.add(p);
				c.moveToNext();
			}
			c.close();
		}

		return tests;
	}

	public ArrayList<Float> GetValuesGeneric(String q)
	{
		AbstractWindowedCursor c = QueryTyped(q);
		ArrayList<Float> tests = new ArrayList<Float>();

		if (c != null)
		{
			while (c.moveToNext())
			{
				if (c.isFloat(0))
				{
					tests.add(c.isNull(0) ? 0.0f : c.getFloat(0));
				}
				else if (c.isString(0))
				{
					tests.add(c.isNull(0) ? 0.0f : Convert.ToFloat(c.getString(0)));
				} else
					tests.add(c.isNull(0) ? 0.0f : c.getInt(0));

				c.moveToNext();
			}
			c.close();
		}

		return tests;
	}

	// ???????? ???? ???????? ????? ??????
	@SuppressWarnings("deprecation")
	public ArrayList<Object> GetValuesRow(String q)
	{
		Cursor c = Query(q);
		ArrayList<Object> tests = new ArrayList<Object>();

		if (c == null || !c.moveToFirst())
			return tests;
		CursorWrapper cw = new CursorWrapper(c);

		Class<?> cursorWrapper = CursorWrapper.class;
		Field mCursor;
		try
		{
			mCursor = cursorWrapper.getDeclaredField("mCursor");
		} catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			cw.close();
			return tests;
		}
		mCursor.setAccessible(true);
		AbstractWindowedCursor abstractWindowedCursor;
		try
		{
			abstractWindowedCursor = (AbstractWindowedCursor) mCursor.get(cw);
		} catch (Exception e)
		{
			e.printStackTrace();
			return tests;
		}
		CursorWindow cursorWindow = abstractWindowedCursor.getWindow();
		int pos = abstractWindowedCursor.getPosition();
		for (int i = 0; i < c.getColumnCount(); ++i)
		{
			// String type = null;
			if (cursorWindow.isNull(pos, i))
			{
				// type = "Cursor.FIELD_TYPE_NULL";
			} else if (cursorWindow.isLong(pos, i))
			{
				tests.add(c.getInt(i));
			} else if (cursorWindow.isFloat(pos, i))
			{
				// type = "Cursor.FIELD_TYPE_FLOAT";
			} else if (cursorWindow.isString(pos, i))
			{
				tests.add(c.getString(i));
			} else if (cursorWindow.isBlob(pos, i))
			{
				// type = "Cursor.FIELD_TYPE_BLOB";
			}
		}
		c.close();

		return tests;
	}

	public abstract boolean NeedRefill();


	protected boolean IsColumnExists(String table, String column)
	{
		int index = -1;
		Cursor c = Query("SELECT * FROM " + table + " LIMIT 1");
		if (c.moveToNext())
		{
			index = c.getColumnIndex(column);
		}
		c.close();
		return index != -1;
	}
}
