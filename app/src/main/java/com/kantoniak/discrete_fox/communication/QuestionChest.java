package com.kantoniak.discrete_fox.communication;

import android.content.res.Resources;

import com.kantoniak.discrete_fox.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestionChest {
    ArrayList<Question> questionsArrayList;
    // GDP, No toilet, Unemployment, forest cover, population density, waste
    private static final String COUNTRIES = "&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK&filterNonGeo=1";
    private static final int LASTTIMEPERIODINT = 3;
    private static final String LASTTIMEPERIOD = "&lastTimePeriod=" + String.valueOf(LASTTIMEPERIODINT);
    private static final int PRECISIONINT = 1;
    private static final String PRECISION = "&precision=" + String.valueOf(PRECISIONINT);
    private static final String[] QUERY = {
            "nama_10_gdp?na_item=B1GQ&unit=CP_MEUR",
            "ilc_mdho05?incgrp=TOTAL&unit=PC&hhtyp=TOTAL",
            "une_rt_a?unit=PC_POP&age=TOTAL&sex=T",
            "lan_lcv_fao?landcover=LCC1&unit=PC",
            "demo_r_d3dens?unit=HAB_KM2",
            "env_wasmun?wst_oper=GEN&unit=KG_HAB",
            "tin00134?unit=PC_HH",
            "nama_10_pc?na_item=B1GQ&unit=CP_EUR_HAB",
    };
    private static final QuestionCategory[] CATEGORY = {
            QuestionCategory.ECONOMY,
            QuestionCategory.POPULATION,
            QuestionCategory.POPULATION,
            QuestionCategory.GENERAL,
            QuestionCategory.POPULATION,
            QuestionCategory.ENVIRONMENT,
            QuestionCategory.SCIENCE,
            QuestionCategory.ECONOMY,
    };

    private String[] unit = {"M€", "%", "%", "%", "/km2", "kt", "%", "€"};

    private String[] description;
    private static List<String> ACOUNTRY_CODES = Arrays.asList("at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "gr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "ne", "pl", "pt", "ro", "se", "si", "sk");
    private List<List<String>> COUNTRYCODES;

    private boolean isShuffleLegit() {
        boolean legit = true;
        for (int j = 0; j < 5; j++) {
            if (ACOUNTRY_CODES.get(j) == null){
                legit = false;
            }
        }
        return legit;
    }

    public QuestionChest(Resources res) {
        COUNTRYCODES = new ArrayList<>();
        for (int i = 0; i < QUERY.length; i++) {
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
        description = new String[QUERY.length];
        description[0] = res.getString(R.string.question_gdp);
        description[1] = res.getString(R.string.question_toilet);
        description[2] = res.getString(R.string.question_unemployment);
        description[3] = res.getString(R.string.question_forest);
        description[4] = res.getString(R.string.question_population_density);
        description[5] = res.getString(R.string.question_waste);
        description[6] = res.getString(R.string.question_internet_access);
        //countryCodes = new String

        questionsArrayList = new ArrayList<>();
        int n = QUERY.length;
        for (int i = 0; i < n; i++) {
            String fullQuery = QUERY[i] + COUNTRIES + LASTTIMEPERIOD + PRECISION;
            DataProvider dp = new DataProvider();
            AsyncTaskParams atp = new AsyncTaskParams(fullQuery, description[i]);
            try {
                APIResponse response = dp.execute(atp).get();
                Question q = new Question(fullQuery, response.getContent().getHashMap(), description[i], COUNTRYCODES.get(i), unit[i], CATEGORY[i]);
                questionsArrayList.add(q);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int numberOfQuestions() {
        return questionsArrayList.size();
    }

    public Question getQuestion(int idx) {
        if (questionsArrayList != null) {
            if (questionsArrayList.size() > idx) {
                return questionsArrayList.get(idx);
            }
        }
        return null;
    }
}
