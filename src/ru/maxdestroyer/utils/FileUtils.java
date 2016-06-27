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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

  /**
   * @return first file
   * @throws IOException
   */
  public static File unzip(File zipFile, File targetDirectory) throws IOException {
    File result = null;
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
          byte[] buffer = new byte[8 * 1024];
          while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            if (result == null) {
              result = file;
            }
                /* if time should be restored as well
				long time = ze.getTime();
				if (time > 0)
					file.setLastModified(time); */
            }
        } finally {
            zis.close();
        }
    return result;
    }

    /**
     * @param url like http://some.com/1.zip?param=true#5
     * @return name.ext
     */
    public static String getFileNameFromUrl(URL url) {
        String urlString = url.getFile();
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

  /**
   * @param ext - "jpg" etc...
   * @return первый найденный файл
   */
  public static File getFileInDir(File dir, final String ext) {
    File[] files = getFilesInDir(dir, ext);

    if (files != null && files.length > 0) return files[0];

    return null;
  }

  /**
   * @param ext - "jpg" etc...
   * @return все файлы
   */
  public static File[] getFilesInDir(File dir, final String ext) {
    File[] files = dir.listFiles(new FilenameFilter() {
      @Override public boolean accept(File dir, String name) {
        return name.endsWith("." + ext);
      }
    });

    return files;
  }
}
