package com.rajkmaurya111.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.sql.Time;
import java.lang.Throwable;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

   private ArrayAdapter<String> mForecastAdapter;
    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id== R.id.action_refresh){
            FetchWeatherTask  weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        String[] data = {
                "Today-Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 65/56",
                "Sat - Help Trapped in Weather Station - 60/51 ",
                "Sun - Sunny - 80/86"
        };
        List<String> weekForecast = new ArrayList<String>(
                Arrays.asList(data));

        //making the adapter

        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
               // Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();

            }
        }

        );

        return rootView;


    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String  LOG_TAG = FetchWeatherTask.class.getSimpleName();

        /* The date/time conversion code is going to be moved outside the asynctask later,
       * so for convenience we're breaking it out into its own method now.
       */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time*1000);
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM dd");
            return format.format(date).toString();
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */







         private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
          //  final String OWN_LIST = "coord"
           // final String OWM_LIST = "list";
          //  final String OWM_WEATHER = "weather";
            final String OWM_LIST = "main";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "temp_max";
            final String OWM_MIN = "temp_min";
            //final String OWM_DATETIME = "dt";
          //  final String OWM_DESCRIPTION = "main";



          JSONObject forecastJson = new JSONObject(forecastJsonStr);
       //    JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
             String[] module = new String[numDays];
             for(int i = 0; i < numDays; i++) {
             String module1 = forecastJson.getJSONObject(OWM_LIST).getString(OWM_TEMPERATURE);
             String module2 = forecastJson.getJSONObject(OWM_LIST).getString(OWM_MAX);
             String module3 = forecastJson.getJSONObject(OWM_LIST).getString(OWM_MIN);

             // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

             module[i] = "day" + " - " + "Max_temp" + module2+ " - " + "Min_temp" +  module3+"   " + "Avg."+ module1;
         }

        for (String s : module) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }
        return module;


            // now we work exclusively in UTC

         }

           /* String[] resultStrs = new String[numDays];
            for(int i = 0; i < 2; i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                //JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
           //     long dateTime = dayForecast.getLong(OWM_MAX);
                // Cheating to convert this to UTC time, which is what we want anyhow

            //   day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
          //      JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
          //      description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
          //      JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            //    double high = temperatureObject.getDouble(OWM_MAX);
              //  double low = temperatureObject.getDouble(OWM_MIN);

               // highAndLow = formatHighLows(high, low);
                resultStrs[i] = "day" + " - " + "description" + " - " + "highAndLow";
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        } */

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/weather?q=94043,uk&appid=1873d577b970348a4b80535da6293743";

             //   URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                final String QUERY_PARAM = "q";
                final  String FORMAT_PARAM = "mode";
                final  String UNITS_PARAM = "UNITS";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read input stream

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null){
                    return null;
                }
reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine())!= null){
                    buffer.append(line+ "\n");
                }
                if (buffer.length() == 0) {

                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String " + forecastJsonStr);
            }catch (IOException e){
                Log.e(LOG_TAG, "Error", e);

                return null;
            }finally {
                if (urlConnection!= null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try{
                return (getWeatherDataFromJson(forecastJsonStr, numDays));
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result!=null){
                mForecastAdapter.clear();
                for (String dayForecastStr : result){
                    mForecastAdapter.addAll(dayForecastStr);
                }

;            }
           // super.onPostExecute(result);

        }
    }
}
