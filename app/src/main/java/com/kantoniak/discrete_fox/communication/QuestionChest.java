package com.kantoniak.discrete_fox.communication;

import android.content.res.Resources;

import com.kantoniak.discrete_fox.R;

import java.util.ArrayList;

public class QuestionChest {
    ArrayList<Question> questionsArrayList;
    // GDP, No toilet, Unemployment, forest cover, population density, waste
    private static final String[] QUERY = {
            "nama_10_gdp?precision=1&na_item=B1GQ&unit=CP_MEUR&time=2016&filterNonGeo=1",
            "ilc_mdho05?sex=T&precision=1&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=EU28&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SI&geo=SK&geo=UK&lastTimePeriod=3&incgrp=TOTAL&unit=PC&age=TOTAL&hhtyp=TOTAL",
            "une_rt_a?sex=T&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DK&geo=EE&geo=EL&geo=ES&geo=FI&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK&geo=DE&geo=FR&geo=PT&geo=EU28&precision=1&lastTimePeriod=3&unit=PC_POP&age=TOTAL",
            "lan_lcv_fao?landcover=LCC1&precision=1&unit=PC&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=EU28&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK",
            "demo_r_d3dens?unit=HAB_KM2&precision=1&lastTimePeriod=3&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=EU28&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK",
            "env_wasmun?precision=1&lastTimePeriod=3&wst_oper=GEN&unit=KG_HAB&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=EU28&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK",
    };

    private String[] description;

    private static final int[] OFFSET = {1, 6, 2, 1, 1, 3};
    private static final int[] YEAR = {2016, 2016, 2016, 2015, 2016, 2016};
    private static final int[] MINCOLOR = {0xFFAD1457, 0xFFFFD54F, 0xFF4DD0E1, 0xFFAED581, 0xFF7986CB, 0xFDCE7755};
    private static final int[] MAXCOLOR = {0xFF062922, 0xFFFF8F00, 0xFF00838F, 0xFF558B2F, 0xFF283593, 0xFF9E9DF4};
    private static final String[][] COUNTRYCODES = {{"pl", "fr", "se", "ie", "lv"}, {"pl", "de", "cz", "si", "hu"}, {"pl", "pt", "bg", "fi", "dk"}, {"pl", "at", "be", "cy", "gr"}, {"pl", "hr", "ie", "ne", "ro"}, {"pl", "fr", "se", "ie", "lv"}};
    private static final String[] MINLABEL = {"<1.05M€", "<10%", "<6.2%", "<18.1%", "<457/km2", ""};
    private static final String[] MIDLABEL = {"1.05M€ - 2.09M€", "10% - 20%", "6.2% - 10.5%", "18.1% - 37.2%", "457/km2 - 916/km2", ""};
    private static final String[] MAXLABEL = {">2.09M€", ">20%", ">10.5%", ">37.2%", ">916/km2", ""};

    public QuestionChest(Resources res) {
        description = new String[OFFSET.length];
        description[0] = res.getString(R.string.question_gdp);
        description[1] = res.getString(R.string.question_toilet);
        description[2] = res.getString(R.string.question_unemployment);
        description[3] = res.getString(R.string.question_forest);
        description[4] = res.getString(R.string.question_population_density);
        description[5] = res.getString(R.string.question_waste);
        questionsArrayList = new ArrayList<>();
        int n = QUERY.length;
        for (int i = 0; i < n; i++) {
            DataProvider dp = new DataProvider();
            AsyncTaskParams atp = new AsyncTaskParams(QUERY[i], OFFSET[i], description[i]);
            try {
                APIResponse response = dp.execute(atp).get();
                Question q = new Question(QUERY[i], response.getContent().getHashMap(), YEAR[i], description[i], MINCOLOR[i], MAXCOLOR[i], COUNTRYCODES[i], MINLABEL[i], MIDLABEL[i], MAXLABEL[i]);
                questionsArrayList.add(q);
            } catch (Exception e) {

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
