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
                    intent.putExtra(getString(R.string.intent_name), name);
                    intent.putExtra(getString(R.string.intent_last_name), lastName);
                    intent.putExtra(getString(R.string.intent_birthday), birthday);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
        }
    }

    /**
     * Check what you fill all information
     */
    private boolean checkCheckBoxes() {
        boolean result = isBirthday();
        result = verifyFirstName(result);
        result = verifyLastName(result);
        result = verifyCheckBox(R.id.checkBox1, result);
        result = verifyCheckBox(R.id.checkBox2, result);
        result = verifyCheckBox(R.id.checkBox3, result);
        result = verifyCheckBox(R.id.checkBox4, result);
        result = verifyCheckBox(R.id.checkBox5, result);
        result = verifyCheckBox(R.id.checkBox6, result);

        return result;
    }

    /**
     * First name verification
     */
    private boolean verifyFirstName(boolean result) {
        EditText editTextName = findViewById(R.id.editTextName);
        name = editTextName.getText().toString().trim();
        if (isName(name))
            return result;
        editTextName.setError(getString(R.string.invalid_name));
        return false;
    }

    /**
     * Last name verification
     */
    private boolean verifyLastName(boolean result) {
        EditText editTextName = findViewById(R.id.editTextLastName);
        lastName = editTextName.getText().toString().trim();
        if (isName(lastName))
            return result;
        editTextName.setError(getString(R.string.invalid_name));
        return false;
    }

    /**
     * Name verification
     */
    private boolean isName(String str) {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches(getString(R.string.name_matches))));
    }

    /**
     * CheckBox verification
     */
    private boolean verifyCheckBox(int id, boolean result) {
        CheckBox cb = findViewById(id);
        if (!cb.isChecked()) {
            cb.setTextColor(getResources().getColor(R.color.wrong_text_color));
            return false;
        }
        cb.setTextColor(getResources().getColor(R.color.black_text_color));
        return result;
    }

    /**
     * Birthday verification
     */
    private boolean isBirthday() {
        EditText editTextBirthday = findViewById(R.id.editTextBirthday);
        birthday = editTextBirthday.getText().toString().trim();

        boolean result = true;
        try {
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.date_format));
            format.setLenient(false);

            Calendar calBirthday = Calendar.getInstance();
            calBirthday.setTime(format.parse(birthday));

            Calendar calToday = Calendar.getInstance();
            calToday.setTime(new Date());

            int age = calToday.get(Calendar.YEAR) - calBirthday.get(Calendar.YEAR);
            if (calBirthday.get(Calendar.MONTH) > calToday.get(Calendar.MONTH) ||
                    (calBirthday.get(Calendar.MONTH) == calToday.get(Calendar.MONTH)
                            && calBirthday.get(Calendar.DATE) > calToday.get(Calendar.DATE))) {
                age--;
            }

            if (age < 16 || age > 90)
                result = false;
        } catch (ParseException e) {
            result = false;
        }
        if (!result)
            editTextBirthday.setError(getString(R.string.invalid_birthday));

        return result;
    }
}
