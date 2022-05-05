package com.example.carespace.Appointment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.carespace.ClinicHospitalView.DoctorsModel;
import com.example.carespace.R;

import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class AppointmentPickerView extends AppCompatActivity implements TimeSlotAdapter.onItemClickListener{

    //incoming model
    private DoctorsModel model;
    private String workingHours;

    //vars
    private int startTime, endTime;
    private ArrayList<String> timeSlotList;

    //rv
    private TimeSlotAdapter adapter;

    //widgets
    private HorizontalCalendar calendar;
    private RecyclerView rv;
    private ImageButton next;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_picker_view);

        model = (DoctorsModel) getIntent().getSerializableExtra("model");
        workingHours = model.getWorkingHours();
        timeSlotList = new ArrayList<>();

        setupID();
        setupCalendar();
        calcTime();
        setupRV(); //set up recycler view
    }

    private void setupRV()
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false);
        rv.setLayoutManager(gridLayoutManager);

        //error on initialization of calendar get selected date
        if (Calendar.getInstance().getTime().after(calendar.getSelectedDate().getTime()))
        {
            adapter = new TimeSlotAdapter(this,this, timeSlotList,model.getDoctorID(), Calendar.getInstance().getTime());
            rv.setAdapter(adapter);
        }
        else
        {
            adapter = new TimeSlotAdapter(this,this, timeSlotList,model.getDoctorID(), calendar.getSelectedDate().getTime());
            rv.setAdapter(adapter);
        }

    }

    private void calcTime()
    {
        String arr[] = workingHours.split(" - " );

        //morning shift
        if (arr[0].contains("am") && arr[1].contains("am"))
        {
            arr[0] = arr[0].replace("am","");
            arr[1] = arr[1].replace("am","");
            startTime =Integer.parseInt(arr[0]);
            endTime =Integer.parseInt(arr[1]);

            for (int i= startTime; i < endTime; i++)
            {
                if (i != 7)
                {
                    timeSlotList.add( i + " am");
                }
            }
        }

        //normal shift
        if (arr[0].contains("am") && arr[1].contains("pm"))
        {
            arr[0] = arr[0].replace("am","");
            arr[1] = arr[1].replace("pm","");
            startTime =Integer.parseInt(arr[0]);
            endTime =Integer.parseInt(arr[1]) + 12;

            for (int i= startTime; i < endTime; i++)
            {
               if (!(i == 13 || i == 18))
               {
                   if (i > 12)
                   {
                       timeSlotList.add((i - 12) + " pm");
                   }
                   else if (i == 12)
                   {
                       timeSlotList.add(i + " pm");
                   }
                   else
                   {
                       timeSlotList.add(i + " am");
                   }
               }
            }
        }

        //night shift
        if (arr[0].contains("pm") && arr[1].contains("pm"))
        {
            arr[0] = arr[0].replace("pm","");
            arr[1] = arr[1].replace("pm","");
            startTime =Integer.parseInt(arr[0]);
            endTime =Integer.parseInt(arr[1]);

            for (int i= startTime; i < endTime; i++)
            {
                if (!(i == 13 || i == 18)) // 1pm, 7pm
                {
                    timeSlotList.add(i + " pm");
                }
            }
        }

    }

    private void setupCalendar()
    {
        /* starts from today */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        /* ends after 2 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        calendar = new HorizontalCalendar.Builder(this, R.id.calendar)
                .range(startDate,endDate)
                .datesNumberOnScreen(5)
                .build();

        calendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                setupRV();
                next.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void setupID()
    {
        rv = findViewById(R.id.rv);
        next = findViewById(R.id.nextBtn);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onTimeSlotClicked(int position) {

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeSlot = timeSlotList.get(position);

                Intent intent = new Intent(AppointmentPickerView.this, AppointmentInfoView.class);
                intent.putExtra("model",model);
                intent.putExtra("appointmentDate", calendar.getSelectedDate().getTime());
                intent.putExtra("timeSlot",timeSlot);
                startActivity(intent);

                finish();
            }
        });
    }
}