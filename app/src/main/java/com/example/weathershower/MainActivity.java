package com.example.weathershower;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editTextCity;
    TextView showCityTextView;

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

                String message = "";

                for (int i=0; i < array.length(); i++) {
                    JSONObject jsonPartial = array.getJSONObject(i);

                    String main = jsonPartial.getString("main");
                    String description = jsonPartial.getString("description");

                    if(!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    }
                }

                if(!message.equals("")){
                    showCityTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "City doesn't exist. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "City doesn't exist. Please try again.", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view) {

        try {
            DownloadData dlData = new DownloadData();
            String cityName = URLEncoder.encode(editTextCity.getText().toString(), "UTF-8");
            dlData.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=508eb656af8872b3cb9a54af0792283c");

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(showCityTextView.getWindowToken(), 0);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "City doesn't exist. Please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        showCityTextView = findViewById(R.id.showCityTextView);

    }
}