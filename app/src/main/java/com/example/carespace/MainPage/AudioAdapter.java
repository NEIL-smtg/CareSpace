package com.example.carespace.MainPage;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.carespace.R;

import java.io.File;
import java.io.InputStream;

public class AudioAdapter {

    private Context context;

    public AudioAdapter(Context context) {
        this.context = context;
    }

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

    public int[] audio ={
            R.raw.justrelax,
            R.raw.oldwatermildmeditation,
            R.raw.goodbyestress,
            R.raw.rainforestsleepyogameditation
    };

//    public String[] audioDuration = {
//            getDuration("justrelax"),
//            getDuration("oldwatermildmeditation"),
//            getDuration("goodbyestress"),
//            getDuration("rainforestsleepyogameditation")
//    };

    public int[] audioImages = {
            R.drawable.audioimg,
            R.drawable.audioimg1,
            R.drawable.audioimg2,
            R.drawable.sleep
    };


    private String getDuration(String filename) {
        String mediaPath = Uri.parse("android.resource://com.example.carespace/raw/"+filename).getPath();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mediaPath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mmr.release();

        return duration;
    }
}
