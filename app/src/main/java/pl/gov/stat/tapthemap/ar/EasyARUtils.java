package pl.gov.stat.tapthemap.ar;

import android.app.Activity;
import android.util.Log;

import pl.gov.stat.tapthemap.R;

import cn.easyar.Engine;

/**
 * Class responsible for east AR utils.
 */
public class EasyARUtils {

    static final String TAG_AR = "AR";

    /**
     * Initialize engine.
     *
     * @param activity Application activity
     */
    public static void initializeEngine(Activity activity) {
        if (!Engine.initialize(activity, activity.getString(R.string.easy_ar_key))) {
            Log.e(TAG_AR, "Initialization Failed.");
        }
    }

}
