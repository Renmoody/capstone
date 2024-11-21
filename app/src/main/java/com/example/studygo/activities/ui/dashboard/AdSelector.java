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

import com.example.studygo.databinding.ActivityCreateAdBinding;
import com.example.studygo.models.Ad;
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

public class AdSelector extends AppCompatActivity {
    private ActivityCreateAdBinding binding;
    private PreferenceManager preferenceManager;
    private final Ad ad = new Ad();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

    }

    private void setListeners() {
        binding.registerEvent.setOnClickListener(view -> {
            if (!checkAd()) {
                return;
            }
            Intent resultIntent = new Intent();
            setAd();
            resultIntent.putExtra("ad", ad);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        binding.buttonSelectDate.setOnClickListener(view -> showCalendar(0));
        binding.buttonSelectEndDate.setOnClickListener(view -> showCalendar(1));
        binding.buttonSelectTime.setOnClickListener(view -> showTime(0));
        binding.buttonSelectTime2.setOnClickListener(view -> showTime(1));
        binding.imageBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
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

    public void setAd() {
        Timestamp timestamp = new Timestamp(c.getTime());

        ad.name = binding.editTextSessionName.getText().toString();
        ad.details = binding.editTextSessionDetails.getText().toString();
        ad.dateStart = getDateFromString(binding.editTextSelectDate.getText().toString(), binding.editTextSelectTime.getText().toString());
        ad.dateEnd = getDateFromString(binding.editTextSelectEndDate.getText().toString(), binding.editTextSelectTime2.getText().toString());
        ad.dateObjectStart = timestamp.toDate();
        ad.authorId = preferenceManager.getString(Constants.KEY_USER_ID);
    }

    private Boolean checkAd() {
        if (binding.editTextSessionName.getText().toString().isEmpty()) {
            showToast("Fill out Ad name");
            return false;
        } else if (binding.editTextSessionDetails.getText().toString().isEmpty()) {
            showToast("Fill out Ad details");
            return false;
        } else if (binding.editTextSelectDate.getText().toString().isEmpty()) {
            showToast("Select start date");
            return false;
        } else if (binding.editTextSelectEndDate.getText().toString().isEmpty()) {
            showToast("Select end date");
            return false;
        } else if (binding.editTextSelectTime.getText().toString().isEmpty()) {
            showToast("Select start time");
            return false;
        } else if (binding.editTextSelectTime2.getText().toString().isEmpty()) {
            showToast("Select end time");
            return false;
        } else if (!(binding.radioMonday.isChecked() || binding.radioTuesday.isChecked()
                || binding.radioWednesday.isChecked() || binding.radioThursday.isChecked() || binding.radioFriday.isChecked()
                || binding.radioSaturday.isChecked() || binding.radioSunday.isChecked())) {
            showToast("Select Day(s)");
            return false;
        } else {
            return true;
        }
    }

    //    Reduce redundancy and also for testing
    private void showToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    }

    private int mYear, mMonth, mDay, mHour, mMinute;
    private Date date;
    private Calendar c;

    private void showCalendar(int i) {
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    // Set the selected date in the EditText
                    if (i == 0) {
                        binding.editTextSelectDate.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year));
                    } else {
                        binding.editTextSelectEndDate.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year));
                    }
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


    private void showTime(int i) {
        // Get Current Time
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        if (i == 0)
                            binding.editTextSelectTime.setText(hourOfDay + ":" + minute);
                        else {
                            binding.editTextSelectTime2.setText(hourOfDay + ":" + minute);
                        }
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }
}
