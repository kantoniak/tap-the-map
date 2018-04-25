package com.kantoniak.discrete_fox.ask;

import android.content.Context;
import android.content.res.Resources;

import com.kantoniak.discrete_fox.CountryUtil;
import com.kantoniak.discrete_fox.R;
import com.kantoniak.discrete_fox.communication.APIResponse;
import com.kantoniak.discrete_fox.communication.AsyncTaskParams;
import com.kantoniak.discrete_fox.communication.DataProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class containing all the available questions.
 */
public class QuestionChest {
    /**
     * Number of time periods to consider in presented data.
     */
    public static final int LASTTIMEPERIODINT = 3;
    /**
     * Number of decimal places in data.
     */
    private static final int PRECISIONINT = 1;
    /**
     * Name of the file containing the questions.
     */
    private static final String QUESTIONFILENAME = "questions.txt";
    /**
     * Query string for selected countries (EU28 countries).
     */
    private static final String COUNTRIES = "&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK&filterNonGeo=1";
    /**
     * Query string for the time period.
     */
    private static final String LASTTIMEPERIOD = "&lastTimePeriod=" + String.valueOf(LASTTIMEPERIODINT);
    /**
     * Query string for decimal precision.
     */
    private static final String PRECISION = "&precision=" + String.valueOf(PRECISIONINT);

    /**
     * Number of countries user can interact with.
     */
    private int mNumberOfCountries;
    /**
     * Array list of all the questions.
     */
    private ArrayList<Question> questionsArrayList;
    /**
     * Queries needed to acquire the data from Eurostat API. Read from asset file.
     */
    private List<String> QUERY;
    /**
     * Categories of the questions. Read from asset file.
     */
    private List<QuestionCategory> CATEGORY;
    /**
     * Multipier of the unit in the question. Read from asset file.
     */
    private List<Integer> multiplier;
    /**
     * Base unit of the questions. Read from asset file.
     */
    private List<String> baseUnit;

    /**
     * List of the country codes.
     */
    private static List<String> ACOUNTRY_CODES = CountryUtil.getIsoCodes();

    /**
     * Creates QuestionChest object.
     * @param res resources of the application
     * @param context context of the application
     * @param numberOfCountries number of countries user can interact with
     */
    public QuestionChest(Resources res, Context context, int numberOfCountries) {
        mNumberOfCountries = numberOfCountries;
        QUERY = new ArrayList<>();
        multiplier = new ArrayList<>();
        baseUnit = new ArrayList<>();
        CATEGORY = new ArrayList<>();
        importQuestions(context);

        List<List<String>> COUNTRYCODES = new ArrayList<>();
        for (int i = 0; i < QUERY.size(); i++) {
            do {
                // TODO create sentinel if not possible to find 5 non null values
                Collections.shuffle(ACOUNTRY_CODES);
            } while(!isShuffleLegit());

            List<String> questionCodes = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                questionCodes.add(ACOUNTRY_CODES.get(j));
            }
            COUNTRYCODES.add(questionCodes);
        }
        List<String> description = new ArrayList<>();
        description.add(res.getString(R.string.question_gdp));
        description.add(res.getString(R.string.question_toilet));
        description.add(res.getString(R.string.question_unemployment));
        description.add(res.getString(R.string.question_forest));
        description.add(res.getString(R.string.question_population_density));
        description.add(res.getString(R.string.question_waste));
        description.add(res.getString(R.string.question_internet_access));
        description.add(res.getString(R.string.question_pkb_per_capita));
        //countryCodes = new String

        questionsArrayList = new ArrayList<>();
        int n = QUERY.size();
        for (int i = 0; i < n; i++) {
            String fullQuery = QUERY.get(i) + COUNTRIES + LASTTIMEPERIOD + PRECISION;
            DataProvider dp = new DataProvider();
            AsyncTaskParams atp = new AsyncTaskParams(fullQuery, description.get(i));
            try {
                APIResponse response = dp.execute(atp).get();
                Question q = new Question(fullQuery, response.getContent().getHashMap(), description.get(i), COUNTRYCODES.get(i), baseUnit.get(i), CATEGORY.get(i), multiplier.get(i));
                questionsArrayList.add(q);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * Checks whether we can use this particular permutation.
     * @return whether we can use given permutation
     */
    private boolean isShuffleLegit() {
        boolean legit = true;
        for (int j = 0; j < mNumberOfCountries; j++) {
            if (ACOUNTRY_CODES.get(j) == null) {
                legit = false;
            }
        }
        return legit;
    }

    /**
     * Reads questions and metainfo about them. Fills query, category, multiplier and baseUnit array lists.
     * @param context application context
     */
    private void importQuestions(Context context) {
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = context.getAssets().open(QUESTIONFILENAME);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] elements = line.split(",");
                //String fullQuery = elements[0] + COUNTRIES + LASTTIMEPERIOD + PRECISION;
                //Question q = new Question(fullQuery, response.getContent().get)
                QUERY.add(elements[0]);
                multiplier.add(Integer.parseInt(elements[1]));
                baseUnit.add(elements[2]);
                CATEGORY.add(QuestionCategory.valueOf(elements[3]));
            }

        } catch (Exception e) {

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {

                }
            }
        }

    }
}
