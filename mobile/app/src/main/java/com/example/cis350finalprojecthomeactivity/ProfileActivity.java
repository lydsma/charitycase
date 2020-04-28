package com.example.cis350finalprojecthomeactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        Intent i = getIntent();
        email = i.getStringExtra(MainActivity.ID);

        Map<String, String> input = new HashMap<String, String>();
        input.put("email", email);
        String msg = "";

        try {
            URL url = new URL("http://10.0.2.2:3000/mobilegetuser");
            AsyncTask<URL, String, String> task = new LoadUser(input);
            task.execute(url);

            // get the response and Toast it
            msg = task.get();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonArr = null;
        try {
            jsonArr = jsonObj.getJSONObject("accountinfo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String name = "";
        String email = "";
        String pic = "";
        boolean rec = true;

        try {
            name = jsonArr.getString("name");
            email = jsonArr.getString("email");
            pic = jsonArr.getString("profilepic");
            rec = jsonArr.getBoolean("recipient");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView welcomeTextView = findViewById(R.id.textView2);
        welcomeTextView.setText("Welcome, " + name);

        TextView nameView = findViewById(R.id.textView3);
        nameView.setText(name);

        TextView emailView = findViewById(R.id.textView5);
        emailView.setText(email);

        TextView recView = findViewById(R.id.textView7);
        if (rec) {
            recView.setText("Seeking donations");
        } else {
            recView.setText("Looking to donate");
        }

        ImageView picView = findViewById(R.id.imageView2);
        Picasso.get().load(pic).into(ImageView);

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
