package com.kantoniak.discrete_fox.communication;

import java.util.ArrayList;

public class QuestionChest {
    ArrayList<Question> questionsArrayList;
    private static final String QUERY1 = "nama_10_gdp?precision=1&na_item=B1GQ&unit=CP_MEUR&time=2016&filterNonGeo=1";
    private static final String QUERY2 = "ilc_mdho05?sex=T&precision=1&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DE&geo=DK&geo=EE&geo=EL&geo=ES&geo=EU28&geo=FI&geo=FR&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=PT&geo=RO&geo=SI&geo=SK&geo=UK&lastTimePeriod=3&incgrp=TOTAL&unit=PC&age=TOTAL&hhtyp=TOTAL";
    private static final String QUERY3 = "une_rt_a?sex=T&geo=AT&geo=BE&geo=BG&geo=CY&geo=CZ&geo=DK&geo=EE&geo=EL&geo=ES&geo=FI&geo=HR&geo=HU&geo=IE&geo=IT&geo=LT&geo=LU&geo=LV&geo=MT&geo=NL&geo=PL&geo=RO&geo=SE&geo=SI&geo=SK&geo=UK&geo=DE&geo=FR&geo=PT&geo=EU28&precision=1&lastTimePeriod=3&unit=PC_POP&age=TOTAL";

    public QuestionChest() {
        questionsArrayList = new ArrayList<>();
        DataProvider dp = new DataProvider(questionsArrayList);
        GDPObject gdpObject = new GDPObject();
        AsyncTaskParams atp = new AsyncTaskParams(QUERY1, gdpObject);
        try {
            APIResponse response = dp.execute(atp).get();
            Question q = new Question(QUERY1, response.getContent().getHashMap(), 2016);
            questionsArrayList.add(q);
        } catch (Exception e) {

        }
        PopulationWithNoToilet toilet = new PopulationWithNoToilet();
        atp = new AsyncTaskParams(QUERY2, toilet);
        DataProvider dp2 = new DataProvider(questionsArrayList);
        try {
            APIResponse response = dp2.execute(atp).get();
            Question q = new Question(QUERY2, response.getContent().getHashMap(), 2016);
            questionsArrayList.add(q);
        } catch (Exception e) {

        }

        UnemploymentRate unemploymentRate = new UnemploymentRate();
        atp = new AsyncTaskParams(QUERY3, unemploymentRate);
        DataProvider dp3 = new DataProvider(questionsArrayList);
        try {
            APIResponse response = dp3.execute(atp).get();
            Question q = new Question(QUERY3, response.getContent().getHashMap(), 2016);
            questionsArrayList.add(q);
        } catch (Exception e) {

        }
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
