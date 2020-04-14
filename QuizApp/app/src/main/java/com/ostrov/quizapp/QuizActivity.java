package com.ostrov.quizapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    String name;
    String lastName;
    String birthday;

    Quiz quiz;
    RecyclerView rv;
    LinearLayoutManager llm;
    RVAdapter adapter;
    ArrayList<Question> questions;
    int countQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        lastName = intent.getStringExtra("LASTNAME");
        birthday = intent.getStringExtra("BIRTHDAY");

        try {
            String json = loadJSONFromAsset();

            ObjectMapper objectMapper = new ObjectMapper();
            quiz = objectMapper.readValue(json, Quiz.class);
            countQuestions = 10;
            //countQuestions = quiz.getCountOfTopics();

            rv = findViewById(R.id.rv);
            llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            initializeAdapter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Load data from json file
     */
    private String loadJSONFromAsset() {
        String json = null;
        try {
            String FILE = "source_file.json";
            InputStream is = this.getAssets().open(FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
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
        for (int i = 0; i < countQuestions; i++) {
            int qCount = quiz.getTopics().get(i).getCountOfQuestions();
            //TODO int qRandom = 0;
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
                        checkAnswers();
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
     * Check your answers
     */
    private void checkAnswers() {
        int countCorrectAnswers = 0;
        for (Question q : questions)
            if (q.getCorrectAnswerNumber() == q.getSelectedAnswer())
                countCorrectAnswers++;
        llm.scrollToPosition(0);
        adapter.setIsChecked();

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);

        TextView alName = alertLayout.findViewById(R.id.alert_name);
        alName.setText(String.format("%s %s", name, lastName));

        TextView alBirthday = alertLayout.findViewById(R.id.alert_birthday);
        alBirthday.setText(birthday);

        TextView alDate = alertLayout.findViewById(R.id.alert_date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        alDate.setText(String.format("%s %s", getString(R.string.alert_date), formatter.format(calendar.getTime())));


        TextView alCorrect= alertLayout.findViewById(R.id.alert_correct);
        alCorrect.setText(getString(R.string.alert_answered_correctly, countCorrectAnswers));

        TextView alIncorrect= alertLayout.findViewById(R.id.alert_incorrect);
        alIncorrect.setText(getString(R.string.alert_answered_correctly, (countQuestions - countCorrectAnswers)));

        TextView alResult= alertLayout.findViewById(R.id.alert_result);
        int pass = (int) (countQuestions * 0.8);
        if (countCorrectAnswers >= pass) {
            alResult.setText(getString(R.string.alert_pass));
            alResult.setBackgroundColor(getResources().getColor(R.color.rightColor));
        }
        else {
            alResult.setText(getString(R.string.alert_fail));
            alResult.setBackgroundColor(getResources().getColor(R.color.wrongColor));
        }

        new AlertDialog.Builder(this)
                .setView(alertLayout)
                .setPositiveButton(R.string.ok,null)
                .show();
    }

    /** For test */
    private void checkQuizClass(Quiz quiz) {
        int topicCount = quiz.getCountOfTopics();
        Log.i("QUIZ", "Topic count = " + String.valueOf(topicCount));

        int i = 0;
        for (Topic t : quiz.getTopics()) {
            i++;
            String tt = t.getTitle();
            int tc = t.getCountOfQuestions();
            Log.i("FULL_INFO" + String.valueOf(i), "TOPIC: Title = " + tt + ", Count of questions = " + String.valueOf(tc));

            for (Question q : t.getQuestions()) {
                String qt = t.getTitle();
                String qa = q.getCorrectAnswer();
                int qan = q.getCorrectAnswerNumber();
                int qc = q.getCountOfAnswers();
                Log.i("FULL_INFO", "QUESTION: Title = " + qt +
                        ", Answer = " + String.valueOf(qa) + "(" + String.valueOf(qan) + ")" +
                        ", Count = " + String.valueOf(qc));
                for (String a : q.getAnswers()) {
                    Log.i("FULL_INFO", "QUESTION: Answer = " + a);
                }
            }
        }
        Log.i("FULL_INFO", "FINISH");
    }
}
