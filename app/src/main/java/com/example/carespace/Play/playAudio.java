package com.example.carespace.Play;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.MainPage.AudioAdapter;
import com.example.carespace.R;

public class playAudio extends AppCompatActivity {

    //widgets
    TextView category,song_name,durationPlayed,durationTotal,back;
    ImageButton play,previous,next,repeat;
    ImageView coverImg;
    SeekBar seekBar;

    //vars
    private String name;
    private int position=-1;
    private int resid;

    //media player
    static MediaPlayer mediaPlayer;

    private Handler handler = new Handler();

    //Threads
    private Thread playThread,prevThread,nextThread;

    AudioAdapter audioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        //initialize widgets
        category = (TextView) findViewById(R.id.category_playAudio);
        song_name = (TextView) findViewById(R.id.title_playAudio);
        durationPlayed = (TextView) findViewById(R.id.durationPlayed_audioPlay);
        durationTotal = (TextView) findViewById(R.id.durationTotal_audioPlay);
        play = (ImageButton) findViewById(R.id.btnplay_audioPlay);
        previous = (ImageButton) findViewById(R.id.btn_previous_audioPlay);
        next = (ImageButton) findViewById(R.id.btn_skipnext_audioPlay);
        repeat = (ImageButton) findViewById(R.id.btn_repeat);
        coverImg = (ImageView) findViewById(R.id.img_playAudio);
        seekBar = (SeekBar) findViewById(R.id.seekbar_audioPlay);
        back = (TextView) findViewById(R.id.backbtn_playAudio);

        audioAdapter = new AudioAdapter(this);

        //get neccessary data from previous intent
        getIncomingIntent();

        //set up widgets
        setupWidgets();

        //set up media player
        setupPlayer();

        //set up buttons listener
        setupListener();
    }

    private void getIncomingIntent()
    {
        name = getIntent().getStringExtra("audio_name");
        name = name.replace(" ","");
        name = name.toLowerCase();

        resid = getResources().getIdentifier(name,"raw",getPackageName());

        position = getIntent().getIntExtra("position",-1);
    }

    private void setupWidgets()
    {
        AudioAdapter audioAdapter = new AudioAdapter(this);
        category.setText(audioAdapter.Category[position]);
        song_name.setText(audioAdapter.audioNameList[position]);

        Glide
                .with(this)
                .load(audioAdapter.audioImages[position])
                .into(coverImg);
    }

    private void setupPlayer()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resid);
            mediaPlayer.start();
            play.setImageResource(R.drawable.ic_pause);
        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), resid);
            mediaPlayer.start();
            play.setImageResource(R.drawable.ic_pause);
        }

        //set max
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        durationTotal.setText(formattedTime(mediaPlayer.getDuration()/1000));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (mediaPlayer !=null)
                {
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        playAudio.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

    }

    private String formattedTime(int mCurrentPosition)
    {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition%60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":0" +seconds;

        if (seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            return totalout;
        }
    }

    private void setupListener()
    {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
    }

    private void prevThreadBtn()
    {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevbtnlicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevbtnlicked()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (position >= 0)
            {
                position--;
            }


            name = audioAdapter.audioNameList[position];
            name = name.replace(" ","");
            name = name.toLowerCase();
            resid = getResources().getIdentifier(name,"raw",getPackageName());

            mediaPlayer = MediaPlayer.create(getApplicationContext(),resid);

            category.setText(audioAdapter.Category[position]);
            song_name.setText(audioAdapter.audioNameList[position]);

            Glide
                    .with(this)
                    .load(audioAdapter.audioImages[position])
                    .into(coverImg);

            seekBar.setMax(mediaPlayer.getDuration()/1000);
            playAudio.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            play.setImageResource(R.drawable.ic_pause);
        }
    }

    private void nextThreadBtn()
    {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nxtbtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nxtbtnClicked()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (position <4)
            {
                position+=1;
            }
            else
            {
                position = 0;
            }
            name = audioAdapter.audioNameList[position];
            name = name.replace(" ","");
            name = name.toLowerCase();
            resid = getResources().getIdentifier(name,"raw",getPackageName());

            mediaPlayer = MediaPlayer.create(getApplicationContext(),resid);

            category.setText(audioAdapter.Category[position]);
            song_name.setText(audioAdapter.audioNameList[position]);

            Glide
                    .with(this)
                    .load(audioAdapter.audioImages[position])
                    .into(coverImg);

            seekBar.setMax(mediaPlayer.getDuration()/1000);
            playAudio.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            play.setImageResource(R.drawable.ic_pause);
        }
    }

    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseClicked()
    {
        if (mediaPlayer.isPlaying())
        {
            play.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            playAudio.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
            play.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            playAudio.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }
}