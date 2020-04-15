package com.ostrov.quizapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    final int countQuestions = 10;
    String name;
    String lastName;
    String birthday;

    Quiz quiz;
    RecyclerView rv;
    LinearLayoutManager llm;
    RVAdapter adapter;
    ArrayList<Question> questions;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv = findViewById(R.id.rv);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        Intent intent = getIntent();
        name = intent.getStringExtra(getString(R.string.intent_name));
        lastName = intent.getStringExtra(getString(R.string.intent_last_name));
        birthday = intent.getStringExtra(getString(R.string.intent_birthday));

        try {
            String json = loadJSONFromAsset();
            ObjectMapper objectMapper = new ObjectMapper();
            quiz = objectMapper.readValue(json, Quiz.class);

            initializeAdapter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load data from json file
     */
    private String loadJSONFromAsset() {
        String json = null;
        try {
            String FILE = getString(R.string.file);
            InputStream is = this.getAssets().open(FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, getString(R.string.standard_charsets));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Initialize adapter for test
     */
    private void initializeAdapter() {
        questions = generateListOfQuestions(quiz);
        adapter = new RVAdapter(questions, this);
        rv.setAdapter(adapter);
    }

    /**
     * Create new list of questions
     */
    private ArrayList<Question> generateListOfQuestions(Quiz quiz) {
        ArrayList<Question> newQuestions = new ArrayList<>();
        if (quiz != null && quiz.getCountOfTopics() >= 10)
            for (int i = 0; i < countQuestions; i++) {
                int qCount = quiz.getTopics().get(i).getCountOfQuestions();
                int qRandom = (new Random()).nextInt(qCount);
                newQuestions.add(quiz.getTopics().get(i).getQuestions().get(qRandom));
            }
        return  newQuestions;
    }

    /**
     * Check your answers
     */
    public void onClickCheckAnswers(View view) {
        if (!isSetAllAnswers())
            return;

        new AlertDialog.Builder(this)
                .setMessage(R.string.question_check)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        llm.scrollToPosition(0);
                        adapter.setIsChecked();
                        gradeAnswers();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /**
     * Check what you answer on all questions
     */
    private boolean isSetAllAnswers() {
        for (Question q : questions)
            if (q.getSelectedAnswer() == 0) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.alert)
                        .setPositiveButton(R.string.ok, null)
                        .show();
                return false;
            }
        return true;
    }

    /**
     * Grade your answers
     */
    private void gradeAnswers() {
        int countCorrectAnswers = 0;
        for (Question q : questions)
            if (q.getCorrectAnswerNumber() == q.getSelectedAnswer())
                countCorrectAnswers++;

        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView alName = toastLayout.findViewById(R.id.toast_name);
        alName.setText(String.format("%s %s", name, lastName));

        TextView alBirthday = toastLayout.findViewById(R.id.toast_birthday);
        alBirthday.setText(birthday);

        TextView tDate = toastLayout.findViewById(R.id.toast_date);
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.toast_date_format));
        Calendar calendar = Calendar.getInstance();
        tDate.setText(getString(R.string.toast_date, formatter.format(calendar.getTime())));


        TextView tCorrect = toastLayout.findViewById(R.id.toast_correct);
        tCorrect.setText(getString(R.string.toast_answered_correctly, countCorrectAnswers));

        TextView tIncorrect = toastLayout.findViewById(R.id.toast_incorrect);
        tIncorrect.setText(getString(R.string.toast_answered_incorrectly, (countQuestions - countCorrectAnswers)));

        TextView tResult = toastLayout.findViewById(R.id.toast_result);
        if (countCorrectAnswers >= countQuestions * 0.8) {
            tResult.setText(getString(R.string.toast_pass));
            ((CardView)toastLayout.findViewById(R.id.toast_layout_root))
                    .setCardBackgroundColor(getResources().getColor(R.color.toast_pass));
        }
        else {
            tResult.setText(getString(R.string.toast_fail));
            ((CardView)toastLayout.findViewById(R.id.toast_layout_root))
                    .setCardBackgroundColor(getResources().getColor(R.color.toast_fail));
        }

        Toast toast = new Toast(this);
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Start new test
     */
    public void onClickNewTest(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.question_start);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (Question q : questions)
                    q.clearSelectedAnswer();
                initializeAdapter();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }
}
