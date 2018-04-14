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
    public HashMap<String, String> country_codes;
    final static int oneOff = 10;
    final static int twoOff = 20;
    final static int noAnswer = 30;
    int step;
    int currentQuestion;
    int mnumberOfCountries;
    int scoreTotal;
    int score;
    Integer[] decisions;
    ArrayList<Question> mquestions;
    String[] country;

    public Gameplay(ArrayList<Question> questions, int numberOfCountries) {
        step = 0;
        score = 0;
        currentQuestion = 0;
        decisions = new Integer[numberOfCountries];
        decisions[0] = 0;
        decisions[1] = 0;
        decisions[2] = 0;
        country = new String[numberOfCountries];
        country[0] = "pl";
        country[1] = "es";
        country[2] = "se";
        mnumberOfCountries = numberOfCountries;
        mquestions = questions;
    }

    public Question finishQuestion(Context context, Map map) {
        // Gather decisions
        HashMap<String, Country> mapCountries = map.getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            decisions[i] = mapCountries.get(country[i]).getHeight();
        }
        // Calculate score
        score = calculateScore(map);
        scoreTotal += score;
        // zwroc odpowiedzi do wyswietlenia
        return nextQuestion(context);
    }

    public String getCurrentDesc() {
        return mquestions.get(currentQuestion).getDesc();
    }

    private Question nextQuestion(Context context) {
        currentQuestion++;
        if (mquestions.size() == currentQuestion) {
            // TODO Present superb view of the final result!
            // We probably should delete this gameplay, and create a new one, otherwise, we should
            // clean this one.
            Toast.makeText(context, String.valueOf(scoreTotal), Toast.LENGTH_LONG).show();

            return null;
        }
        score = 0;
        return mquestions.get(currentQuestion);
    }

    private Integer calculateScore(Map map) {

        int score = 0;
        CountryUtil cu = new CountryUtil();
        for (int i = 0; i < mnumberOfCountries; i++) {
            if (decisions[i] == 0) {
                score += noAnswer;
            } else if (Math.abs(decisions[i] - mquestions.get(i).getCorrectAnswer(cu.convert(map.getCountries().get(country[i]).getCode()))) == 1) {
                score += oneOff;
            } else if (Math.abs(decisions[i] - mquestions.get(i).getCorrectAnswer(cu.convert(map.getCountries().get(country[i]).getCode()))) == 2) {
                score += twoOff;
            }
        }
        return score;
    }
}
