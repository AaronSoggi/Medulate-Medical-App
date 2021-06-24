package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class alarm_activity extends AppCompatActivity implements View.OnClickListener{

    private int notificationId = 1;
    private TextView theDate;
    private EditText reminderText;
    public final String savedText = "saved_text";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_activity);
        setTitle("Pills reminder");

        reminderText = (EditText) findViewById(R.id.reminderText);


        Toolbar toolbar = findViewById(R.id.toolbar_in_editNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting the onclick listener
        findViewById(R.id.setBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.btn_calendar).setOnClickListener(this);
        theDate = (TextView) findViewById(R.id.date);

        // retrieving the date from the calendar
        Intent incomingIntent = getIntent();
        String date = incomingIntent.getStringExtra("date");
        theDate.setText(date);

        final SharedPreferences textHolder = PreferenceManager.getDefaultSharedPreferences(this);
        reminderText.setText(textHolder.getString(savedText, ""));
        reminderText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textHolder.edit().putString(savedText, s.toString()).apply();

            }
        });

    }

    @Override
    public void onClick(View view) {

        // getting the year date and month that was set, in the calendar activity class.
        Intent incomingIntent = getIntent();
        int day = incomingIntent.getIntExtra("day",1);
        int month = incomingIntent.getIntExtra("month", 2);
        int year = incomingIntent.getIntExtra("year", 3);


        TimePicker timePicker = findViewById(R.id.timePicker);

        // set notification & message
        Intent intent = new Intent(alarm_activity.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("message", reminderText.getText().toString());


        //Pending Intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                alarm_activity.this, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        //AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        switch (view.getId()){
            case R.id.setBtn:

                if(reminderText.length() == 0)
                {
                    Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();

                }else{
                    //here we are going to set the alarm.
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();

                    //Creating the time
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, hour);
                    startTime.set(Calendar.MINUTE,minute);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.DAY_OF_MONTH, day);
                    startTime.set(Calendar.MONTH, month);
                    startTime.set(Calendar.YEAR, year);
                    long alarmStartTime = startTime.getTimeInMillis();

                    
                    //Set Alarm
                    alarmManager.set(AlarmManager.RTC_WAKEUP,alarmStartTime,alarmIntent);
                    reminderText.getText().clear();
                    Toast.makeText(this, "Your reminder has been set", Toast.LENGTH_LONG).show();


                }
                break;

            case R.id.cancelBtn:
                alarmManager.cancel(alarmIntent);
                Toast.makeText(this, "Reminder has been Cancelled", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_calendar:
                Intent in = new Intent(alarm_activity.this, CalendarActivity.class);
                startActivity(in);
                break;
        }

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(alarm_activity.this, MainMenu.class) );
            reminderText.getText().clear();
        }

        return super.onOptionsItemSelected(item);
    }

}