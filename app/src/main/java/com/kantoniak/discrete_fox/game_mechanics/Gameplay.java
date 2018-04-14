package com.kantoniak.discrete_fox.game_mechanics;

import android.widget.Toast;

import com.kantoniak.discrete_fox.communication.Question;
import com.kantoniak.discrete_fox.scene.Country;

import java.util.ArrayList;

public class Gameplay {
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
        country[0] = "Poland";
        country[1] = "Spain";
        country[2] = "Sweden";
        mnumberOfCountries = numberOfCountries;
        mquestions = questions;
    }

    public void tap(Country country) {
        //Map country to number of countries;
        //decisions[country] += 1;
        //decisions[country] %= 4;
    }

    public Question finishQuestion() {
        // zlicz wynik
        score = calculateScore();
        scoreTotal += score;
        if (mquestions.size()-1 == currentQuestion)
            return null;
        // zwroc odpowiedzi do wyswietlenia
        return nextQuestion();
    }

    public Question nextQuestion() {
        currentQuestion++;
        score = 0;
        return mquestions.get(currentQuestion);
    }

    private Integer calculateScore() {

        int score = 0;
        for (int i = 0; i < mnumberOfCountries; i++) {
            if (decisions[i] == 0) {
                score += noAnswer;
            } else if (Math.abs(decisions[i] - mquestions.get(currentQuestion).getCorrectAnswer(country[i])) == 1) {
                score += oneOff;
            } else if (Math.abs(decisions[i] - mquestions.get(currentQuestion).getCorrectAnswer(country[i])) == 2) {
                score += twoOff;
            }
        }
        return score;
    }
}
