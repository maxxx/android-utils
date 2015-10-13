package net.malahovsky.utils.visual;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class SoundMgr
{
	private static float volume = 1.0f; // 0..1

	private MediaPlayer mediaPlayer;
	private static int sound = 0;

	public SoundMgr(Context context, int _sound, float _volume, boolean playNow)
	{
		//setVolumeControlStream(AudioManager.STREAM_MUSIC);
		sound = _sound;
		volume = _volume;
		mediaPlayer = buildMediaPlayer(context);
		if (playNow)
			play();
	}

	void play()
	{
		mediaPlayer.setVolume(volume, volume);
		mediaPlayer.start();
	}

	private static MediaPlayer buildMediaPlayer(Context context)
	{
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer player)
			{
				player.seekTo(0);
			}
		});

		AssetFileDescriptor file = context.getResources().openRawResourceFd(sound);
		try
		{
			mediaPlayer.setDataSource(file.getFileDescriptor(),
					file.getStartOffset(), file.getLength());
			file.close();
			mediaPlayer.prepare();
			mediaPlayer.setLooping(false);
		} catch (IOException ioe)
		{
			Log.e("SoundMgr", ioe.toString());
			mediaPlayer = null;
		}
		return mediaPlayer;
	}

}
