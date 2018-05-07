package com.kantoniak.discrete_fox.gameplay;

import android.content.Context;

import com.kantoniak.discrete_fox.Country;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.Map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Gameplay {

    public static class Settings {
        public static final int COUNTRIES_PER_QUESTION = 5;
        public static final int QUESTIONS_PER_SERIES = 5;
        public static final int MAX_COUNTRY_HEIGHT = 3;
        public static final Set<Country> ENABLED_COUNTRIES = Arrays.asList(
                "at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "uk", "el", "hr",
                "hu", "ie", "it", "lt", "lu", "lv", "nl", "pl", "pt", "ro", "se", "si", "sk", "mt")
                .stream().map(Country.Builder::fromEuCode).collect(Collectors.toSet());
    }

    private int currentQuestion;
    private int mnumberOfCountries;
    private int scoreTotal;
    private int score;
    private int maxScore;
    private Integer[] decisions;
    private List<Question> mquestions;


    public Gameplay(List<Question> questions, int numberOfCountries) {
        score = 0;
        mnumberOfCountries = numberOfCountries;
        maxScore = mnumberOfCountries * questions.size();
        currentQuestion = 0;
        decisions = new Integer[mnumberOfCountries];
        for (int i = 0; i < mnumberOfCountries; i++) {
            decisions[i] = 0;
        }
        mquestions = questions;
    }

    public Question finishQuestion(Context context) {
        return nextQuestion(context);
    }

    public void updateScore(Map map) {
        // Gather decisions
        HashMap<Country, CountryInstance> mapCountries = map.getCountries();
        List<Country> countries = mquestions.get(currentQuestion).getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            Country country = countries.get(i);
            decisions[i] = mapCountries.get(country).getHeight();
        }
        // Calculate score
        score = calculateScore(map);
        scoreTotal += score;
    }

    private Question nextQuestion(Context context) {
        currentQuestion++;
        if (mquestions.size() == currentQuestion) {
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
        Question question = getCurrentQuestion();
        List<Country> countries = question.getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            Country country = countries.get(i);
            int dec = decisions[i];
            Question q = mquestions.get(currentQuestion);
            try {
                int qq = q.getCorrectAnswer(country);
                if (Math.abs(dec - qq) == 0) {
                    score += 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
