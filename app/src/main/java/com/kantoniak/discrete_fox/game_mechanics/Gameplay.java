package com.kantoniak.discrete_fox.game_mechanics;

import android.content.Context;

import com.kantoniak.discrete_fox.country.Country;
import com.kantoniak.discrete_fox.country.CountryUtil;
import com.kantoniak.discrete_fox.ask.Question;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        HashMap<Country, CountryInstance> mapCountries = map.getCountries();
        List<String> countries = mquestions.get(currentQuestion).getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            Country country = Country.Builder.fromEuCode(countries.get(i));
            decisions[i] = mapCountries.get(country).getHeight();
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
        // FIXME: This loop is duplicating code from finishQuestion, can we extract that?
        int score = 0;
        HashMap<Country, CountryInstance> mapCountries = map.getCountries();
        Question question = getCurrentQuestion();
        List<String> countries = question.getCountries();
        for (int i = 0; i < mnumberOfCountries; i++) {
            Country country = Country.Builder.fromEuCode(countries.get(i));
            String eurostatName = CountryUtil.eurostatToName(country.getEuCode()); // TODO(kedzior): country.getEurostatName()
            if (Math.abs(decisions[i] - mquestions.get(currentQuestion).getCorrectAnswer(eurostatName)) == 0) {
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

    // TODO(kedzior): Decide what to do with this part. I put it in here, maybe it should be moved
    // to the new implementation that you mentioned earlier. I would move all things like that to
    // Gameplay.
    public static Set<Country> getEnabledCountries() {
        final List<String> enabledEuCodes = Arrays.asList(
                "at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "uk", "el",
                "hr", "hu", "ie", "it", "lt", "lu", "lv", "nl", "pl", "pt", "ro", "se", "si", "sk");

        return enabledEuCodes.stream().map(Country.Builder::fromEuCode).collect(Collectors.toSet());
    }
}
