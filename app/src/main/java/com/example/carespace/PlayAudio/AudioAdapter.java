package com.example.carespace.PlayAudio;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.carespace.R;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class AudioAdapter {

    public String[] Category = {
            "Soundscape",
            "ASMR",
            "Sleeping Better",
            "Anxious",
            "Depressed",
            "Feeling Down",
            "Coaching"
    };

    public String[] audioNameList= {
            "Just Relax",
            "Old Water Mild Meditation",
            "Goodbye Stress",
            "Rain Forest Sleep Yoga Meditation"
    };

    public int[] audioImages = {
            R.drawable.audioimg,
            R.drawable.audioimg1,
            R.drawable.audioimg2,
            R.drawable.sleep
    };

    public String[] audioDuration = new String[4];

    private Context context;
    private  int resid_audioFile;
    private MediaPlayer mediaPlayer;

    public AudioAdapter(){}

    public AudioAdapter(Context context)
    {
        this.context = context;
        String song_name;

        for (int i = 0; i < audioNameList.length; i++)
        {
            song_name = audioNameList[i].toLowerCase();
            song_name = song_name.replace(" ","");

            resid_audioFile = context.getResources().getIdentifier(song_name, "raw",context.getPackageName());

            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), resid_audioFile);
            audioDuration[i] = (formattedTime(mediaPlayer.getDuration()/1000));

        }

        mediaPlayer.release();
    }

    private String formattedTime(int mCurrentPosition)
    {
        String min =  String.valueOf(mCurrentPosition/60);
        return min;
    }

}