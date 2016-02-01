/*
 * Copyright (C) 2016 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Maxim S. on 01.02.2016.
 */
public class DownloadFileTask extends AsyncTask<String, Integer, Void> {

    public static final int BUFFER_SIZE = 4096;
    private Runnable onFinish = null;
    private ProgressDialog progressDialog = null;
    File filePath = null; // where to save file
    URL url = null; //new URL(Constants.URL.APK + filename);
    private Context context = null;

    public DownloadFileTask(final Context context, final File filePath, Runnable onFinish) {
        this.filePath = filePath;
        this.onFinish = onFinish;
        this.context = context;
    }

    public DownloadFileTask(final Context context, final File filePath, final ProgressDialog progressDialog, Runnable onFinish) {
        this.filePath = filePath;
        this.progressDialog = progressDialog;
        this.context = context;
        this.onFinish = onFinish;
    }

    @Override
    protected Void doInBackground(final String... strings) {

        try {
            url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            /* Get information about downloadable apk */
            //int fileLength = connection.getContentLength();
            final long fileLength = Long.parseLong(connection.getHeaderField("Content-Length"));
            //lastMod = DateUtils.parseDate(connection.getHeaderField("Last-Modified")).getTime();
            final long existFileLength = filePath.length();
            long offset = 0L;

            if (existFileLength != fileLength) {
                if (fileLength == filePath.length()) {
                    // file already loaded
                    return null;
                } else {
                    // resume download
                    connection.disconnect();
                    connection = (HttpURLConnection) url.openConnection();
                    offset = existFileLength;
                    connection.setRequestProperty("Range", "bytes=" + offset + '-');
                    connection.connect();
                }
            } else {
                // file doesn't exist or obsolete
                filePath.delete();
            }
            // download the file
            final InputStream input = new BufferedInputStream(connection.getInputStream());
            final OutputStream output = new FileOutputStream(filePath, true);
            final byte[] data = new byte[BUFFER_SIZE];
            long total = offset;
            long count;
            int lastProgressValue = -1;
            try {
                while ((count = input.read(data)) != -1L) {
                    total += count;
                    if (0L < fileLength) {
                        final int percent = (int) (total * 100L / fileLength);
                        if (percent != lastProgressValue) {
                            lastProgressValue = percent;
                            if (progressDialog != null)
                                publishProgress(lastProgressValue);
                        }
                    }
                    output.write(data, 0, (int) count);
                    output.flush();
                }
            } finally {
                output.flush();
                output.close();
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Integer... args) {
        progressDialog.setProgress(args[0]);
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        super.onPostExecute(aVoid);
        if (progressDialog != null)
            progressDialog.dismiss();
        if (onFinish != null)
            onFinish.run();
    }
}