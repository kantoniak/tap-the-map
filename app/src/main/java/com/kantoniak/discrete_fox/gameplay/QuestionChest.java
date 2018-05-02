package com.kantoniak.discrete_fox.gameplay;

import android.content.Context;
import android.content.res.Resources;

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
    private List<String> queries;
    /**
     * Categories of the questions. Read from asset file.
     */
    private List<QuestionCategory> categories;
    /**
     * Multipier of the unit in the question. Read from asset file.
     */
    private List<Integer> multipliers;
    /**
     * Base unit of the questions. Read from asset file.
     */
    private List<String> baseUnits;

    /**
     * Creates QuestionChest object.
     * @param res resources of the application
     * @param context context of the application
     * @param numberOfCountries number of countries user can interact with
     */
    public QuestionChest(Resources res, Context context, int numberOfCountries, int numberOfQuestions) {
        mNumberOfCountries = numberOfCountries;
        queries = new ArrayList<>();
        multipliers = new ArrayList<>();
        baseUnits = new ArrayList<>();
        categories = new ArrayList<>();
        importQuestions(context);
        List<String> description = Arrays.asList(res.getStringArray(R.array.questions));

        questionsArrayList = new ArrayList<>();
        int n = queries.size();
        for (int i = 0; i < n; i++) {
            String fullQuery = queries.get(i) + COUNTRIES + LASTTIMEPERIOD + PRECISION;
            DataProvider dp = new DataProvider();
            AsyncTaskParams atp = new AsyncTaskParams(fullQuery, description.get(i));
            try {
                APIResponse response = dp.execute(atp).get();
                Question q = new Question(fullQuery, response.getContent().getHashMap(), description.get(i), baseUnits.get(i), categories.get(i), multipliers.get(i));
                questionsArrayList.add(q);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                queries.add(elements[0]);
                multipliers.add(Integer.parseInt(elements[1]));
                baseUnits.add(elements[2]);
                categories.add(QuestionCategory.valueOf(elements[3]));
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
