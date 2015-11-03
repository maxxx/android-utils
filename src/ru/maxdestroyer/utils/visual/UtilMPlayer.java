/*
 * Copyright (C) 2015 Maxim Smirnov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */

package ru.maxdestroyer.utils.visual;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by Maxim S. on 01.11.2015.
 */
public class UtilMPlayer implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mp;

    /**
     * Play media file from raw folder
     */
    public UtilMPlayer(Context context, int resRaw) {
        mp = MediaPlayer.create(context, resRaw);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setVolume(0.5f, 0.5f);
        //setLooping(false);
        mp.start();
    }

    /**
     * Play medial file from asset folder
     * @param context
     * @param assetFileName - filename with extension like .mp3
     */
    public UtilMPlayer(Context context, String assetFileName) {
        try {
//            if (m.isPlaying()) {
//                m.stop();
//                m.release();
//                m = new MediaPlayer();
//            }
            mp = new MediaPlayer();
            AssetFileDescriptor descriptor = context.getAssets().openFd(assetFileName);
            mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mp.prepare();
            //m.setVolume(1f, 1f);
            //m.setLooping(true);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer media) {
        media.release();
        mp = null;
    }
}
