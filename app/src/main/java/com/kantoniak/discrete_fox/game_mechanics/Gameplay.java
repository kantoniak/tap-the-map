package com.kantoniak.discrete_fox.game_mechanics;

import android.content.Context;
import android.widget.Toast;

import com.kantoniak.discrete_fox.CountryUtil;
import com.kantoniak.discrete_fox.communication.Question;
import com.kantoniak.discrete_fox.scene.Country;
import com.kantoniak.discrete_fox.scene.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Gameplay {
    public final static int NUMBEROFQUESTIONS = 5;
    int step;
    int currentQuestion;
    int mnumberOfCountries;
    int scoreTotal;
    int score;
    int maxScore;
    Integer[] decisions;
    ArrayList<Question> mquestions;

    public Gameplay(ArrayList<Question> questions, int numberOfCountries) {
        step = 0;
        score = 0;
        mnumberOfCountries = numberOfCountries;
        maxScore = mnumberOfCountries * NUMBEROFQUESTIONS;
        currentQuestion = 0;
        decisions = new Integer[NUMBEROFQUESTIONS];
        for (int i = 0; i < NUMBEROFQUESTIONS; i++) {
            decisions[i] = 0;
        }
        mquestions = questions;
    }

    public Question finishQuestion(Context context, Map map) {
        // Gather decisions
        HashMap<String, Country> mapCountries = map.getCountries();
        String[] countries = mquestions.get(currentQuestion).getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            decisions[i] = mapCountries.get(countries[i]).getHeight();
        }
        // Calculate score
        score = calculateScore(map);
        scoreTotal += score;
        // zwroc odpowiedzi do wyswietlenia
        return nextQuestion(context);
    }

    private Question nextQuestion(Context context) {
        currentQuestion++;
        if (mquestions.size() == currentQuestion) {
            // TODO Present superb view of the final result!
            // We probably should delete this gameplay, and create a new one, otherwise, we should
            // clean this one.
            currentQuestion--;
            return null;
        }
        score = 0;
        return mquestions.get(currentQuestion);
    }

    public int getResult() {
        return scoreTotal;
    }

    private Integer calculateScore(Map map) {
        int score = 0;
        CountryUtil cu = new CountryUtil();
        HashMap<String, Country> mapCountries = map.getCountries();
        Question question = getCurrentQuestion();
        String[] countries = question.getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            if (Math.abs(decisions[i] - mquestions.get(currentQuestion).getCorrectAnswer(cu.convert(mapCountries.get(countries[i]).getCode()))) == 0) {
                score += 1;
            }
        }
        return score;
    }

    public Question getCurrentQuestion() {
        return mquestions.get(currentQuestion);
    }

    public int getMaxResult() {
        return maxScore;
    }
}
