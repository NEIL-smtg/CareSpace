package com.example.carespace.Exercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.carespace.R;

import pl.droidsonroids.gif.GifImageView;

public class TutorialView extends AppCompatActivity {

    //widgets
    private ImageButton forward,backward;
    private GifImageView gif;
    private TextView instruction_TV,startPauseBtn_tv,timer_tv,takeAbreak_tv,numTurns_tv;
    private ImageView startPauseBtn;

    //countdown timer
    CountDownTimer countDownTimer;
    private int timer = 30;
    private static final int breakTimer = 15;
    private int currentTime = timer;
    private static final int INTERVAL = 1000;

    //vars
    private int position = 0;
    private int[] gifList;
    private int[] instructionList;
    private boolean completed = false;  //check if user work completed every session
    private int completedTurn = 0;

    //constant
    private static final String START = "START";
    private static final String PAUSE = "PAUSE";
    private static final int MAX_TURN = 3; //3 sets

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_view);

        //get incoming intent vars
        getIncomingIntent();

        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);
        gif = findViewById(R.id.gif);
        instruction_TV = findViewById(R.id.instruction);
        startPauseBtn = findViewById(R.id.startPauseBtn);
        startPauseBtn_tv = findViewById(R.id.startPauseBtn_tv);
        timer_tv = findViewById(R.id.timer_tv);
        takeAbreak_tv = findViewById(R.id.takeAbreak_tv);
        numTurns_tv = findViewById(R.id.numTurns_tv);

        setupTrainingSession();
        startPauseBtnHandler();
    }

    private void startPauseBtnHandler()
    {
        startPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startPauseBtn_tv.getText().equals(START))
                {
                    setPauseBtnTV();
                    countDownTimer = new CountDownTimer(currentTime*INTERVAL,INTERVAL) {
                        @Override
                        public void onTick(long l) {
                            currentTime--;
                            timer_tv.setText(""+currentTime);
                        }

                        @Override
                        public void onFinish() {
                            activityHandler();
                        }
                    }.start();
                }
                else
                {
                    countDownTimer.cancel();
                    setStartBtnTV();
                }
            }
        });
    }

    private void setupTrainingSession()
    {
        updateActivity();

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityHandler();
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position--;
                updateActivity();
            }
        });

    }

    private void activityHandler()
    {
        if (position < gifList.length-1)
        {
            position++;
            updateActivity();
        }
        else
        {
            completedTurn++;
            if (completedTurn < MAX_TURN)
            {
                gif.setVisibility(View.GONE);
                startPauseBtn.setVisibility(View.GONE);
                forward.setVisibility(View.GONE);
                backward.setVisibility(View.GONE);
                numTurns_tv.setVisibility(View.GONE);
                instruction_TV.setText("Just take a deep breathe, rest..");
                takeAbreak_tv.setVisibility(View.VISIBLE);

                currentTime = breakTimer;
                countDownTimer = new CountDownTimer(currentTime*INTERVAL, INTERVAL) {
                    @Override
                    public void onTick(long l) {
                        currentTime--;
                        timer_tv.setText(""+currentTime);
                    }

                    @Override
                    public void onFinish() {
                        position=0;
                        gif.setVisibility(View.VISIBLE);
                        startPauseBtn.setVisibility(View.VISIBLE);
                        forward.setVisibility(View.VISIBLE);
                        backward.setVisibility(View.VISIBLE);
                        numTurns_tv.setVisibility(View.VISIBLE);
                        takeAbreak_tv.setVisibility(View.GONE);
                        updateActivity();
                    }
                }.start();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(TutorialView.this);
                builder.setTitle("Tough Guy")
                        .setMessage("Congratulations !!! You made it !!!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        }
    }

    private void getIncomingIntent()
    {
        gifList = getIntent().getIntArrayExtra("giflist");
        instructionList = getIntent().getIntArrayExtra("instruction");
    }

    //forward or backward to next workout session
    private void updateActivity()
    {
        currentTime = timer;
        timer_tv.setText(""+timer);
        setStartBtnTV();
        numTurns_tv.setText("Set "+(completedTurn+1));
        gif.setImageResource(gifList[position]);
        instruction_TV.setText(instructionList[position]);

        if (position != 0)
        {
            backward.setVisibility(View.VISIBLE);
        }
        else
        {
            backward.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Loser")
                .setMessage("Are you sure you want to quit halfway ???")
                .setNegativeButton("Continue Another Time", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("NO, back to training", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss dialog
                    }
                }).show();
    }

    private void setStartBtnTV()
    {
        startPauseBtn_tv.setText(START);
    }

    private void setPauseBtnTV()
    {
        startPauseBtn_tv.setText(PAUSE);
    }
}