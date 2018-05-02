package com.kantoniak.discrete_fox.ar;

import android.app.Activity;
import android.util.Log;

import com.kantoniak.discrete_fox.R;

import cn.easyar.Engine;

public class EasyARUtils {

    static final String TAG_AR = "AR";

    public static void initializeEngine(Activity activity) {
        if (!Engine.initialize(activity, activity.getString(R.string.easy_ar_key))) {
            Log.e(TAG_AR, "Initialization Failed.");
        }
    }

}
