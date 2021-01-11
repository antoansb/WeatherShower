package com.example.weathershower;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnect = null;

            try {

                url = new URL(urls[0]);
                urlConnect = (HttpURLConnection) url.openConnection();
                InputStream input = urlConnect.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            try {
                JSONObject jsonObject = new JSONObject(string);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather info", weatherInfo);

                JSONArray array = new JSONArray(weatherInfo);

                for (int i=0; i < array.length(); i++) {
                    JSONObject jsonPartial = array.getJSONObject(i);

                    Log.i("main", jsonPartial.getString("main"));
                    Log.i("description", jsonPartial.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadData dlData = new DownloadData();
        dlData.execute("https://api.openweathermap.org/data/2.5/weather?q=Berlin&appid=508eb656af8872b3cb9a54af0792283c");
    }
}