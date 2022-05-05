package com.example.carespace.Alarm;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carespace.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;

public class Alarm extends AppCompatActivity {

    //widgets
    private TextView  btn_addAlarm, btnDone_water_alarm, txt_time_picker , btnDone_medicine_alarm, alarm_time_txt;
    private ImageButton back;
    private MaterialTimePicker customAlarmtimePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Dialog add_alarm_dialog, timepicker_dialog, custom_alarm_confirmation_dialog;
    private CardView cardbtn_water_alarm, cardbtn_medicine_alarm, cardbtn_custom_alarm;
    private ImageButton btn_close_addDialog, btnClose_water_alarm, btnConfirm_custom_alarm;
    private NumberPicker alarm_time_picker;
    private EditText edittext_addAlarmDescription;
    private RecyclerView RV_alarmList;

    //const vars
    private static final String WATER_ALARM = "water_alarm";
    private static final String MEDICINE_ALARM = "medicine_alarm";
    private static final String CUSTOM_ALARM = "custom_alarm";

    private static final int INTERVAL = 60*1000;

    //list view adapter
    AlarmListAdapter listAdapter;

    //local database
    AlarmDatabaseHelper myDB;

    //vars
    ArrayList<String> db_id, db_title, db_time, db_desc;
    ArrayList<Integer> db_pendingID;
    private int notifyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        back =  findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_addAlarm =  findViewById(R.id.btnAdd_timeralarm);
        btn_addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlarmPickerDialog();
            }
        });

        add_alarm_dialog = new Dialog(this);
        timepicker_dialog = new Dialog(this);
        custom_alarm_confirmation_dialog = new Dialog(this);

        myDB = new AlarmDatabaseHelper(Alarm.this);
        db_id = new ArrayList<>();
        db_title = new ArrayList<>();
        db_time = new ArrayList<>();
        db_desc = new ArrayList<>();
        db_pendingID = new ArrayList<>();

        storeAlarmDataInArrays();
        notifyPosition = db_id.size()-1;
        listAdapter = new AlarmListAdapter(Alarm.this,db_id,db_title,db_time,db_desc,db_pendingID);
        RV_alarmList = findViewById(R.id.rv_alarmList);
        RV_alarmList.setAdapter(listAdapter);

        createNotificationChannel();
    }

    private void storeAlarmDataInArrays()
    {
        Cursor cursor = myDB.readAllData();

        if (db_id.size() == 0)
        {
            if (cursor.getCount() == 0)
            {
                Toast.makeText(this, "No alarm", Toast.LENGTH_SHORT).show();
            }
            else
            {
                while (cursor.moveToNext())
                {
                    db_id.add(cursor.getString(0));
                    db_title.add(cursor.getString(1));
                    db_time.add(cursor.getString(2));
                    db_desc.add(cursor.getString(3));
                    db_pendingID.add(cursor.getInt(4));
                }
            }
        }
        else
        {
            cursor.moveToLast();
            db_id.add(cursor.getString(0));
            db_title.add(cursor.getString(1));
            db_time.add(cursor.getString(2));
            db_desc.add(cursor.getString(3));
            db_pendingID.add(cursor.getInt(4));
        }

    }

    private void showAlarmPickerDialog()
    {
        //dialog for choosing which alarm to add
        add_alarm_dialog.setContentView(R.layout.alarm_picker_dialog);
        add_alarm_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //time picker for water and medicine alarm
        timepicker_dialog.setContentView(R.layout.time_picker);
        timepicker_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //confirmation dialog
        custom_alarm_confirmation_dialog.setContentView(R.layout.custom_alarm_comfirmation_dialog);
        custom_alarm_confirmation_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //widget used in choosing alarm dialog
        cardbtn_water_alarm = (CardView) add_alarm_dialog.findViewById(R.id.cardbtn_waterAlarm);
        cardbtn_medicine_alarm = (CardView) add_alarm_dialog.findViewById(R.id.cardbtn_medicineAlarm);
        cardbtn_custom_alarm = (CardView) add_alarm_dialog.findViewById(R.id.cardbtn_customAlarm);
        btn_close_addDialog = (ImageButton) add_alarm_dialog.findViewById(R.id.btnClose_alarmdialog);

        //widget used in set alarm dialog
        TextView timepickertxt = timepicker_dialog.findViewById(R.id.timepickertxt);
        btnDone_water_alarm = (TextView) timepicker_dialog.findViewById(R.id.btnDone_water_alarm);
        btnDone_medicine_alarm = (TextView) timepicker_dialog.findViewById(R.id.btnDone_medicine_alarm);
        btnClose_water_alarm = (ImageButton) timepicker_dialog.findViewById(R.id.btnClose_water_alarm);
        txt_time_picker = (TextView) timepicker_dialog.findViewById(R.id.txt_time_picker);
        alarm_time_picker = (NumberPicker) timepicker_dialog.findViewById(R.id.alarm_time_picker);

        //widgets used in confirmation dialog
        edittext_addAlarmDescription = (EditText) custom_alarm_confirmation_dialog.findViewById(R.id.edittext_addAlarmDescription);
        btnConfirm_custom_alarm = (ImageButton) custom_alarm_confirmation_dialog.findViewById(R.id.btnConfirm_custom_alarm);
        alarm_time_txt = (TextView) custom_alarm_confirmation_dialog.findViewById(R.id.alarm_time_txt);

        //initialize time values for picker
        String[] minuteValues = new String[10];

        for (int i = 0, j=3; i < minuteValues.length; i++,j++) {
            String number = Integer.toString(j*5);
            //String number = Integer.toString(i);
            //minuteValues[i] = number.length() < 2 ? "0" + number : number;
            minuteValues[i] = number;
        }

        alarm_time_picker.setDisplayedValues(minuteValues);

        //size = 10, index = 9
        alarm_time_picker.setMaxValue(minuteValues.length-1);

        //water alarm card on click
        cardbtn_water_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_alarm_dialog.dismiss();
                timepicker_dialog.show();
                btnDone_water_alarm.setVisibility(View.VISIBLE);
                timepickertxt.setText(getResources().getString(R.string.water_time_gap_title));
            }
        });

        //when user scrolling through the minutes, change the text
        alarm_time_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                txt_time_picker.setText("Time Gap : " + minuteValues[newVal] + " min");
            }
        });

        //done scrolling, execute the alarm
        btnDone_water_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int repeating_gap =  Integer.parseInt(minuteValues[alarm_time_picker.getValue()]);
                setRepeatingAlarm(repeating_gap,WATER_ALARM);
                timepicker_dialog.dismiss();
                btnDone_water_alarm.setVisibility(View.GONE);
            }
        });

        //done scrolling, execute the alarm
        btnDone_medicine_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int repeating_gap =  Integer.parseInt(minuteValues[alarm_time_picker.getValue()]);
                setRepeatingAlarm(repeating_gap,MEDICINE_ALARM);
                timepicker_dialog.dismiss();
                btnDone_medicine_alarm.setVisibility(View.GONE);
            }
        });

        //close the water alarm dialog
        btnClose_water_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timepicker_dialog.dismiss();
            }
        });


        //when medicine alarm on click/user click on medicine alarm
        cardbtn_medicine_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_alarm_dialog.dismiss();
                timepicker_dialog.show();
                btnDone_medicine_alarm.setVisibility(View.VISIBLE);
                timepickertxt.setText(getResources().getString(R.string.medicine_time_gap_title));
            }
        });


        //user click on custom alarm
        cardbtn_custom_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_alarm_dialog.dismiss();
                showCustomTimePicker();
            }
        });

        //close the alarm picker dialog
        btn_close_addDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_alarm_dialog.dismiss();
            }
        });

        add_alarm_dialog.show();
    }


    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showCustomTimePicker()
    {
        customAlarmtimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        customAlarmtimePicker.show(getSupportFragmentManager(), "foxandroid");

        customAlarmtimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String alarmTime;

                if (customAlarmtimePicker.getHour() > 12)
                {
                    alarmTime = (customAlarmtimePicker.getHour()-12) + " : " + String.format("%02d",customAlarmtimePicker.getMinute()) + " PM";
                    alarm_time_txt.setText("Your Alarm : " + alarmTime);
                }
                else
                {
                    alarmTime =  customAlarmtimePicker.getHour() + ":" + String.format("%02d",customAlarmtimePicker.getMinute()) + " AM";
                    alarm_time_txt.setText("Your Alarm :" + alarmTime );
                }

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, customAlarmtimePicker.getHour());
                calendar.set(Calendar.MINUTE, customAlarmtimePicker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                custom_alarm_confirmation_dialog.show();

                btnConfirm_custom_alarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        custom_alarm_confirmation_dialog.dismiss();
                        String alarm_description = edittext_addAlarmDescription.getText().toString();
                        setAlarm(CUSTOM_ALARM,alarm_description,alarmTime);

                    }
                });
            }
        });
    }

    private void setAlarm(String alarm_type, String alarm_description, String alarmTime)
    {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        final int pending_id = (int) calendar.getTimeInMillis();

        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("alarm_type",alarm_type);
        intent.putExtra("alarm_description",alarm_description);
        intent.putExtra("pending_ID",pending_id);

        pendingIntent = PendingIntent.getBroadcast(this,pending_id,intent,0);

        alarmManager.setExact
                (
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        //AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );

        addToDB(alarm_type,alarmTime,pending_id);

    }

    private void setRepeatingAlarm(int repeating_gap, String alarm_type)
    {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        final int pending_id = (int) System.currentTimeMillis();

        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("pending_ID",pending_id);

        if (alarm_type.equals(WATER_ALARM))
        {
            intent.putExtra("alarm_type",WATER_ALARM);
        }

        if (alarm_type.equals(MEDICINE_ALARM))
        {
            intent.putExtra("alarm_type",MEDICINE_ALARM);
        }

        pendingIntent = PendingIntent.getBroadcast(this,pending_id,intent,0);

        alarmManager.setInexactRepeating
                (
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + INTERVAL,
                        repeating_gap * INTERVAL,
                        pendingIntent
                );

        addToDB(alarm_type,String.valueOf(repeating_gap), pending_id);
    }

    private void addToDB(String alarm_type, String alarmTime, int pending_id)
    {
        if (alarm_type.equals(WATER_ALARM))
        {
            myDB.addAlarm(
                    alarm_type,
                    alarmTime,
                    "Time to get hydrated !! Drink !!",
                    pending_id
                    );

        }
        else if (alarm_type.equals(MEDICINE_ALARM))
        {
            myDB.addAlarm(
                    alarm_type,
                    alarmTime,
                    "Time to eat medicine !! Get well soon !!",
                    pending_id
            );
        }
        else
        {
            myDB.addAlarm(
                    alarm_type,
                    alarmTime,
                    edittext_addAlarmDescription.getText().toString(),
                    pending_id
            );
        }

        storeAlarmDataInArrays();
        notifyPosition++;
        listAdapter.notifyItemInserted(notifyPosition);

    }


}