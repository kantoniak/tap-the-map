package pl.gov.stat.tapthemap.gameplay;

import android.content.Context;

import pl.gov.stat.tapthemap.Country;
import pl.gov.stat.tapthemap.scene.CountryInstance;
import pl.gov.stat.tapthemap.scene.Map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class responsible for gameplay,
 */
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

    /**
     * Acquire next question to display.
     *
     * @param context Context of the application
     * @return Next question
     */
    public Question finishQuestion(Context context) {
        return nextQuestion(context);
    }

    /**
     * Update score.
     *
     * @param map Current map
     */
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

    /**
     * Get next question, or return null on the last one.
     *
     * @param context Context of the application
     * @return Next question or null
     */
    private Question nextQuestion(Context context) {
        currentQuestion++;
        if (mquestions.size() == currentQuestion) {
            currentQuestion--;
            return null;
        }
        score = 0;
        return mquestions.get(currentQuestion);
    }

    /**
     * Get result.
     *
     * @return Result
     */
    public int getResult() {
        return scoreTotal;
    }

    /**
     * Calculate score.
     *
     * @param map Current map
     * @return Current score
     */
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

    /**
     * Get current question.
     *
     * @return Question
     */
    public Question getCurrentQuestion() {
        return mquestions.get(currentQuestion);
    }

    /**
     * Get maximum result.
     *
     * @return Maximum result
     */
    public int getMaxResult() {
        return maxScore;
    }

    /**
     * Get current question index.
     *
     * @return Index of current question
     */
    public int getCurrentQuestionInt() {
        return currentQuestion;
    }
}
