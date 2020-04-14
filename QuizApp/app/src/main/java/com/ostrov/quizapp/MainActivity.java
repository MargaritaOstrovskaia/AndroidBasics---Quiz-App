package com.ostrov.quizapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String name;
    String lastName;
    String birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * Start test activity
     */
    public void onClickStart(View view) {
        if (checkCheckBoxes()) {
            new AlertDialog.Builder(this)
            .setMessage(R.string.question_start)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    intent.putExtra("NAME", name);
                    intent.putExtra("LASTNAME", lastName);
                    intent.putExtra("BIRTHDAY", birthday);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
        }
        else
            Toast.makeText(this, R.string.main_alert, Toast.LENGTH_LONG).show();
    }

    /**
     * Check what you fill all information
     */
    private boolean checkCheckBoxes() {
        boolean result = true;

        EditText editTextName = findViewById(R.id.editTextName);
        name = editTextName.getText().toString().trim();
        if (!isName(name)) {
            editTextName.setError(getString(R.string.invalid_name));
            result = false;
        }

        EditText editTextLastName = findViewById(R.id.editTextLastName);
        lastName = editTextLastName.getText().toString().trim();
        if (!isName(lastName)) {
            editTextLastName.setError(getString(R.string.invalid_last_name));
            result = false;
        }

        EditText editTextBirthday = findViewById(R.id.editTextBirthday);
        birthday = editTextBirthday.getText().toString().trim();
        if (!isBirthday(birthday)) {
            editTextBirthday.setError(getString(R.string.invalid_birthday));
            result = false;
        }

        CheckBox cb = findViewById(R.id.checkBox1);
        if (!cb.isChecked()) {
            cb.setTextColor(Color.RED);
            result = false;
        }
        else
            cb.setTextColor(Color.BLACK);

        cb = findViewById(R.id.checkBox2);
        if (!cb.isChecked()) {
            cb.setTextColor(Color.RED);
            result = false;
        }
        else
            cb.setTextColor(Color.BLACK);

        cb = findViewById(R.id.checkBox3);
        if (!cb.isChecked()) {
            cb.setTextColor(Color.RED);
            result = false;
        }
        else
            cb.setTextColor(Color.BLACK);

        cb = findViewById(R.id.checkBox4);
        if (!cb.isChecked()) {
            cb.setTextColor(Color.RED);
            result = false;
        }
        else
            cb.setTextColor(Color.BLACK);

        cb = findViewById(R.id.checkBox5);
        if (!cb.isChecked()) {
            cb.setTextColor(Color.RED);
            result = false;
        }
        else
            cb.setTextColor(Color.BLACK);

        cb = findViewById(R.id.checkBox6);
        if (!cb.isChecked()) {
            cb.setTextColor(Color.RED);
            result = false;
        }
        else
            cb.setTextColor(Color.BLACK);

        return result;
    }

    /**
     * Verify name
     */
    private boolean isName(String str) {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    /**
     * Verify birthday
     */
    private boolean isBirthday(String b ) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.format));
            format.setLenient(false);

            Calendar calBirthday = Calendar.getInstance();
            calBirthday.setTime(format.parse(b));

            Calendar calToday = Calendar.getInstance();
            calToday.setTime(new Date());

            int age = calToday.get(Calendar.YEAR) - calBirthday.get(Calendar.YEAR);
            if (calBirthday.get(Calendar.MONTH) > calToday.get(Calendar.MONTH) ||
                    (calBirthday.get(Calendar.MONTH) == calToday.get(Calendar.MONTH)
                            && calBirthday.get(Calendar.DATE) > calToday.get(Calendar.DATE))) {
                age--;
            }

            if (age < 16 || age > 90)
                return false;

        } catch (ParseException e) {
            //e.printStackTrace();
            return false;
        }

        return true;
    }
}
