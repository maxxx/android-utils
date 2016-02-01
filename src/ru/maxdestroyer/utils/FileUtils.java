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
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class FileUtils
{
    public static void write(byte[] data, String fileName) throws IOException {
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(data);
		out.flush();
		out.close();
	}

    public static void write(byte[] data, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(data);
		out.flush();
		out.close();
	}

    public static void append(byte[] data, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file, true);
		out.write(data);
		out.flush();
		out.close();
	}

    public static void append(String str, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file, true);
		OutputStreamWriter bos = new OutputStreamWriter(out);
		bos.write(str);
		bos.close();
		out.close();
	}

    public static byte[] read(String fileName) throws IOException {
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

    public static byte[] read(File file) throws IOException {
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


    public static void deleteRecursive(String path) {
        File f = new File(path);
        if (f.isDirectory())
            for (File child : f.listFiles())
                deleteRecursive(child.getAbsolutePath());

        f.delete();
    }

    @SuppressLint("NewApi")
    public static long getFreeSpace(String path) {
        StatFs stat = new StatFs(path);
        long sdAvailSize;
        if (Util.getApiLvl() >= 18) {
            sdAvailSize = stat.getAvailableBlocksLong()
                    * stat.getBlockSizeLong();
        } else if (Util.getApiLvl() >= 9)
            sdAvailSize = new File(path).getUsableSpace();
        else
            sdAvailSize = stat.getAvailableBlocks()
                    * stat.getBlockSize();

        return sdAvailSize;
    }
}
