package com.kantoniak.discrete_fox.game_mechanics;

import android.content.Context;

import com.kantoniak.discrete_fox.CountryUtil;
import com.kantoniak.discrete_fox.ask.Question;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gameplay {
    public final int NUMBEROFQUESTIONS;
    int step;
    int currentQuestion;
    int mnumberOfCountries;
    int scoreTotal;
    int score;
    int maxScore;
    Integer[] decisions;
    ArrayList<Question> mquestions;

    public Gameplay(ArrayList<Question> questions, int numberOfCountries) {
        NUMBEROFQUESTIONS = questions.size();
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
        HashMap<String, CountryInstance> mapCountries = map.getCountries();
        List<String> countries = mquestions.get(currentQuestion).getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            decisions[i] = mapCountries.get(countries.get(i)).getHeight();
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
            // TODO: We probably should delete this gameplay, and create a new one, otherwise, we should clean this one.
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
        HashMap<String, CountryInstance> mapCountries = map.getCountries();
        Question question = getCurrentQuestion();
        List<String> countries = question.getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            if (Math.abs(decisions[i] - mquestions.get(currentQuestion).getCorrectAnswer(CountryUtil.eurostatToName(mapCountries.get(countries.get(i)).getCode()))) == 0) {
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

    public int getCurrentQuestionInt() {
        return currentQuestion;
    }
}
