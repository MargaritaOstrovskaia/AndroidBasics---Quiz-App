package com.ostrov.quizapp;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

class TestJSON {
    static String ObjectToJSON() {

        Question question1 = new Question();
        question1.setQuestion("Compared to the driver of a vehicle with four wheels, which of the following would affect a motorcyclist more?");
        question1.setAnswers(new ArrayList<String>(Arrays.asList("Road conditions",
                "Rain & Wind",
                "All of above")));
        question1.setCorrectAnswer(3);

        Question question2 = new Question();
        question2.setQuestion("During bad weather and traffic conditions, you should:");
        question2.setAnswers(new ArrayList<String>(Arrays.asList("Increase your following distance",
                "Decrease your following distance",
                "Not ride your motorcycle on highways")));
        question2.setCorrectAnswer(1);

        Topic topic1 = new Topic();
        topic1.setTitle("Dangerous Surfaces");
        topic1.setQuestions(new ArrayList<Question>(Arrays.asList(question1)));

        Topic topic2 = new Topic();
        topic2.setTitle("Distance");
        topic2.setQuestions(new ArrayList<Question>(Arrays.asList(question2)));

        Quiz quiz = new Quiz();
        quiz.setTitle("Motorcycle Test");
        quiz.setTopics(new ArrayList<Topic>(Arrays.asList(topic1, topic2)));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String quizJSON = objectMapper.writeValueAsString(quiz);
            return quizJSON;
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
