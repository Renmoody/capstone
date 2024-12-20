package com.example.studygo.activities.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studygo.databinding.ActivityCreateEventBinding;
import com.example.studygo.models.Event;
import com.example.studygo.utilities.Constants;
import com.example.studygo.utilities.PreferenceManager;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventSelector extends AppCompatActivity {
    private ActivityCreateEventBinding binding;
    private final Event event = new Event();
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.registerEvent.setOnClickListener(view -> {
            if (!checkEvent()) {
                return;
            }
            Intent resultIntent = new Intent();
            setEvent();
            resultIntent.putExtra("event", event);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        binding.buttonSelectDate.setOnClickListener(view -> showCalendar());
        binding.buttonSelectTime.setOnClickListener(view -> showTime());
        binding.imageBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    public void setEvent() {
        Timestamp timestamp = new Timestamp(c.getTime());

        event.name = binding.editTextSessionName.getText().toString();
        event.details = binding.editTextSessionDetails.getText().toString();
        event.date = getDateFromString(binding.editTextSelectDate.getText().toString(), binding.editTextSelectTime.getText().toString());
        event.dateObject = timestamp.toDate();
        event.access = checkAccess(binding.spinnerAccess.getSelectedItem().toString().toLowerCase());
        event.authorId = preferenceManager.getString(Constants.KEY_USER_ID);
        Log.d("Spinner", "Event access" + event.access);
    }

    private String checkAccess(String lowerCase) {
        if (lowerCase.equals("friends only")) {
            return Constants.KEY_EVENT_ACCESS_FRIENDS;
        } else if (lowerCase.equals("public")) {
            return Constants.KEY_EVENT_ACCESS_PUBLIC;
        } else {
            return Constants.KEY_EVENT_ACCESS_PRIVATE;
        }
    }

    //    Chat GPT wrote this
    private String getDateFromString(String dateStr, String timeStr) {
        // Parse date (dd-MM-yyyy)
        String[] dateParts = dateStr.split("-");
        LocalDate date = LocalDate.of(
                Integer.parseInt(dateParts[2]),   // Year
                Integer.parseInt(dateParts[1]),   // Month
                Integer.parseInt(dateParts[0])    // Day
        );
        // Parse time (HH:mm)
        String[] timeParts = timeStr.split(":");
        LocalTime time = LocalTime.of(
                Integer.parseInt(timeParts[0]),   // Hour
                Integer.parseInt(timeParts[1])    // Minute
        );
        // Combine date and time
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        // Format as "MMMM dd, yyyy - hh:mm a"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yy - hh:mm a", Locale.getDefault());
        return dateTime.format(formatter);
    }

    private Boolean checkEvent() {
        if (binding.spinnerAccess.getSelectedItem().toString().isEmpty()) {
            event.access = "public";
        }
        if (binding.editTextSelectDate.getText().toString().isEmpty()) {
            showToast("Select a time");
            return false;
        } else if (binding.editTextSelectDate.getText().toString().isEmpty()) {
            showToast("Select a date");
            return false;
        } else if (binding.editTextSessionName.getText().toString().isEmpty()) {
            showToast("Fill out event name");
            return false;
        } else {
            return true;
        }
    }


    //    Reduce redundancy and also for testing
    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private Date date;
    private Calendar c;
    private void showCalendar() {
        c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Set the selected date in the EditText
                    binding.editTextSelectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    // Convert the selected date to a Date object
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, monthOfYear);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    date = selectedDate.getTime(); // This is the Date object you can use
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void showTime() {
        // Get Current Time
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        binding.editTextSelectTime.setText(hourOfDay + ":" + minute);
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

}
