package com.kantoniak.discrete_fox.gameplay;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kantoniak.discrete_fox.communication.APIResponse;
import com.kantoniak.discrete_fox.communication.AsyncTaskParams;
import com.kantoniak.discrete_fox.communication.DataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Class containing all the available questions.
 */
public class QuestionChest {
    /**
     * Number of time periods to consider in presented data.
     */
    public static final int LAST_TIME_PERIOD_INT = 3;
    /**
     * Number of decimal places in data.
     */
    private static final int PRECISION_INT = 1;
    /**
     * Name of the file containing the questions.
     */
    private static final String QUESTION_FILENAME = "questions.json";
    /**
     * Query string for selected countries (EU28 countries).
     */
    private static final String COUNTRIES = "&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK&filterNonGeo=1";
    /**
     * Query string for the time period.
     */
    private static final String LAST_TIME_PERIOD = "&lastTimePeriod=" + String.valueOf(LAST_TIME_PERIOD_INT);
    /**
     * Query string for decimal precision.
     */
    private static final String PRECISION = "&precision=" + String.valueOf(PRECISION_INT);

    /**
     * Number of countries user can interact with.
     */
    private int mNumberOfCountries;
    /**
     * Array list of all the questions.
     */
    private ArrayList<Question> questionsArrayList;

    private class QuestionObject {
        /**
         * Queries needed to acquire the data from Eurostat API. Read from asset file.
         */
        private String query;
        /**
         * Categories of the questions. Read from asset file.
         */
        private QuestionCategory category;
        /**
         * Multipier of the unit in the question. Read from asset file.
         */
        private Integer multiplier;
        /**
         * Base unit of the questions. Read from asset file.
         */
        private String baseUnit;
        private Map<String, String> localized_text;

        QuestionObject() {
            query = "";
            multiplier = 0;
            baseUnit = "";
            localized_text = new HashMap<>();
        }

        String getQuery() {
            return query;
        }

        private Integer getMultiplier() {
            return multiplier;
        }

        String getBaseUnit() {
            return baseUnit;
        }

        QuestionCategory getCategory() {
            return category;
        }

        public String getDescription() {
            try {
                return localized_text.get(Locale.getDefault().getLanguage());
            } catch (Exception e) {
                return localized_text.get("en");
            }
        }
    }

    private ArrayList<QuestionObject> questionObjects;

    /**
     * Creates QuestionChest object.
     *
     * @param numberOfCountries number of countries user can interact with
     */
    public QuestionChest(Context context, int numberOfCountries, int numberOfQuestions) {
        mNumberOfCountries = numberOfCountries;
        List<QuestionObject> questionObjects = importQuestions(context);

        questionsArrayList = new ArrayList<>();
        int n = questionObjects.size();
        for (int i = 0; i < n; i++) {
            String fullQuery = questionObjects.get(i).getQuery() + COUNTRIES + LAST_TIME_PERIOD + PRECISION;
            DataProvider dp = new DataProvider();
            AsyncTaskParams atp = new AsyncTaskParams(fullQuery, questionObjects.get(i).getDescription());
            APIResponse response = null;
            try {
                response = dp.execute(atp).get();
            } catch (Exception ignored) {
            }
            if (response == null) {
                return;
            }
            Question q = new Question(fullQuery, response.getContent().getHashMap(), questionObjects.get(i).getDescription(), questionObjects.get(i).getBaseUnit(), questionObjects.get(i).getCategory(), questionObjects.get(i).getMultiplier());
            questionsArrayList.add(q);
        }
        Collections.shuffle(questionsArrayList);
        while (questionsArrayList.size() > numberOfQuestions) {
            questionsArrayList.remove(questionsArrayList.size() - 1);
        }
    }

    /**
     * Returns number of all the questions in the chest.
     * @return number of all the questions in the chest
     */
    public int numberOfQuestions() {
        return questionsArrayList.size();
    }

    /**
     * Get one, specific question.
     * @param idx index of the question
     * @return question at given index
     */
    public Question getQuestion(int idx) {
        if (questionsArrayList != null) {
            if (questionsArrayList.size() > idx) {
                return questionsArrayList.get(idx);
            }
        }
        return null;
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Reads questions and metainfo about them. Fills query, category, multiplier and baseUnit array lists.
     */
    private List<QuestionObject> importQuestions(Context context) {
        List<QuestionObject> res = null;
        try {
            InputStream inputStream = context.getAssets().open(QUESTION_FILENAME);
            String buffer = inputStreamToString(inputStream);

            Type questionObjectType = new TypeToken<List<QuestionObject>>() {}.getType();
            Gson gson = new Gson();
            res = gson.fromJson(buffer, questionObjectType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
