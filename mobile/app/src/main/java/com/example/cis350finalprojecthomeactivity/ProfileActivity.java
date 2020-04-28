package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Intent i = getIntent();
        email = i.getStringExtra(MainActivity.ID);
        TextView welcomeTextView = findViewById(R.id.textView2);
        welcomeTextView.setText("Welcome, " + email);

        Map<String, String> input = new HashMap<String, String>();
        input.put("email", email);

        try {
            URL url = new URL("http://10.0.2.2:3000/viewprofile/*");
            AsyncTask<URL, String, String> task = new ProfileActivity.LoadUser(input);
            task.execute(url);

            // get the response and Toast it
            String msg = task.get();

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSignoutButtonClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onBackButtonClick(View v) {
        finish();
    }

    class LoadUser extends AsyncTask<URL, String, String> {
        JSONObject userData;

        public LoadUser(Map<String, String> userData) {
            if (userData != null) {
                this.userData = new JSONObject(userData);
            }
        }

        @Override
        protected String doInBackground(URL... urls) {
            try {
                URL url = urls[0];
                HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                connect.setDoInput(true);
                connect.setDoOutput(true);
                connect.setRequestProperty("Content-Type", "application/json");
                connect.setRequestMethod("POST");
                connect.connect();

                if (this.userData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(connect.getOutputStream());
                    writer.write(userData.toString());
                    writer.flush();
                }

                int statusCode = connect.getResponseCode();

                if (statusCode ==  200) {
                    InputStream inputStream = new BufferedInputStream(connect.getInputStream());
                    String msg = convertInputStreamToString(inputStream);
                    Log.v("results!", msg);
                    return msg;
                } else {
                    return "error";
                }
            } catch (IOException e) {
                return e.toString();
            } catch (Exception e) {
                return e.toString();
            }
        }

        private String convertInputStreamToString(InputStream inputStream) {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

    }

}
