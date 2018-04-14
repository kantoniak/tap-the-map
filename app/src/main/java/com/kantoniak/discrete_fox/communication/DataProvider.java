package com.kantoniak.discrete_fox.communication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Class that asynchronously retrieves and parses the data from Eurostat API.
 */
public class DataProvider extends AsyncTask<AsyncTaskParams, Void, APIResponse> {
    private static final String MAINURL ="http://ec.europa.eu/eurostat/wdds/rest/data/v2.1/json/en/";
    private static final String TAG = "[DATAPROVIDER]";
    WeakReference<ArrayList<Question>> mquestionArrayList;

    //private WeakReference<Context> mContext;
    private String mquery;

    /**
     * Constructor that takes context to the application in order to update the UI in the
     * onPostExecute method.
     *
     * @param context context of the current application
     */
    public DataProvider(ArrayList<Question> questionArrayList) {
        mquestionArrayList = new WeakReference<>(questionArrayList);
    }

    /**
     * Method used to asynchronously retrieve the data from the Eurostat, as well, as parse it
     * into the APIResponse object.
     *
     * @param params N/A
     * @return APIResponse object for given QUERY
     */
    public APIResponse doInBackground(AsyncTaskParams... params){
        mquery = params[0].getQuery();
        ContentObject content = params[0].getContent();
        String buffer = retrieveObject(mquery);
        return parseObject(buffer, content);
    }

    /**
     * Method called after finishing the retrieving and parsing the data received from Eurostat
     * service.
     *
     * @param res an APIResponse object, that was retireved and parsed
     */
    protected void onPostExecute(APIResponse res) {
        //Question q = new Question(mquery, res.getContent().getHashMap(), 2016);
        //ArrayList<Question> questionArrayList = mquestionArrayList.get();
        //questionArrayList.add(q);
        Log.e("pleb", "pleb");

        //Context context = mContext.get();

        /*
        Toast.makeText(context, res.getLabel(), Toast.LENGTH_LONG).show();
        Toast.makeText(context, res.getPopulation().getValueForCountry("Czech Republic", 2016), Toast.LENGTH_LONG).show();
        Toast.makeText(context, res.getPopulation().getValueForCountry("Poland", 2016), Toast.LENGTH_LONG).show();
        Toast.makeText(context, res.getPopulation().getValueForCountry("Romania", 2016), Toast.LENGTH_LONG).show();
        */
    }

    /**
     * Retrieve json from Eurostat API.
     * @return return json in String format
     */
    private String retrieveObject(String query) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        URL url;
        StringBuilder buffer = new StringBuilder();
        try {
            url = new URL(MAINURL + query);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0)
                return null;
            return buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * Method used to parse the json response from Eurostat into legitimate APIResponse object.
     *
     * @param response json object in String format
     * @return response parsed into APIResponse object
     */
    private APIResponse parseObject(String response, ContentObject content) {
        APIResponse res = null;
        try {
            JSONObject obj;
            obj = new JSONObject(response);
            String version = obj.getString("version");
            String label = obj.getString("label");
            String href = obj.getString("href");
            String source = obj.getString("source");
            String updated = obj.getString("updated");
            String className = obj.getString("class");
            assertEquals(className, "dataset");
            String status = obj.getString("status");
            JSONObject extension = obj.getJSONObject("extension");
            JSONObject value = obj.getJSONObject("value");
            JSONObject dimension = obj.getJSONObject("dimension");
            JSONArray id = obj.getJSONArray("id");
            JSONArray size = obj.getJSONArray("size");

            res = new APIResponse(version, label, href, source, updated, status, extension, value, dimension, id, size, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
