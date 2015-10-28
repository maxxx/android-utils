/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils;

import android.location.Location;

public class GPSData
{
	private Location loc;
	public long time;
	public double lat, lon;
	public double height; // attitude
	public float acc;
	public int speed, direction;
	public int satellites;
	
	public GPSData(Location _loc, long _time)
	{
		if (_loc == null)
			return;
		loc = _loc;
		time = _time;
		lat = loc.getLatitude();
		lon = loc.getLongitude();
		height = loc.getAltitude();
		speed = Util.ToInt(loc.getSpeed() * 1000.0f / 3600.0f); // km/h
		direction = Util.ToInt(loc.getBearing());
		acc = loc.getAccuracy();
	}
	
	public GPSData(Location _loc, long _time, int sat)
	{
		if (_loc == null)
			return;
		loc = _loc;
		time = _time;
		lat = loc.getLatitude();
		lon = loc.getLongitude();
		height = loc.getAltitude();
		speed = Util.ToInt(loc.getSpeed() * 1000.0f / 3600.0f); // km/h
		direction = Util.ToInt(loc.getBearing());
		acc = loc.getAccuracy();
		satellites = sat;
	}
	
	public GPSData(Location _loc)
	{
		if (_loc == null)
			return;
		loc = _loc;
		time = loc.getTime();
		lat = loc.getLatitude();
		lon = loc.getLongitude();
		height = loc.getAltitude();
		speed = Util.ToInt(loc.getSpeed() * 1000.0f / 3600.0f); // km/h
		direction = Util.ToInt(loc.getBearing());
		acc = loc.getAccuracy();
	}
	
	public void Save(UtilConfig cfg, int num)
	{
		cfg.Save("gpslat" + num, lat);
		cfg.Save("gpslon" + num, lon);
		cfg.Save("gpstime" + num, time);
	}
}
