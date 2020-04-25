package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    public static final String EMAIL = "EMAIL";
    private EditText em;
    private EditText pass;
    private List<LoginActivity.User> allUsersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
    }

    public void onSignUpButtonClick(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, 1);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {
            Intent intent = new Intent();
            intent.putExtra(EMAIL, data.getStringExtra(RegisterActivity.EMAIL));
            setResult(1, intent);

            Toast.makeText(this, data.getStringExtra(RegisterActivity.EMAIL), Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    public void onLoginButtonClick(View v) {

        em = findViewById(R.id.email);
        String email = em.getText().toString();

        pass = findViewById(R.id.password);
        String password = pass.getText().toString();

        Map<String, String> input = new HashMap<String, String>();
        input.put("email", email);
        input.put("password", password);

        try {
            URL url = new URL("http://10.0.2.2:3000/mobilechecklogin");
            AsyncTask<URL, String, String> task = new LoginUser(input);
            task.execute(url);

            // get the response and Toast it
            String msg = task.get();

            if (msg.equals("Success")) {
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(EMAIL, email);
                setResult(1, intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid login, try again", Toast.LENGTH_LONG).show();
            }

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

    /*
    ~~~~~~~~~~~~~~~  USER CLASS  ~~~~~~~~~~~~~~~
     */

    private class User {

        LoginActivity.Category category;
        LoginActivity.Donor donor;
        String firstName;
        String lastName;
        String email;
        String password;

        public User(String firstName, String lastName, String email, String password,
                    LoginActivity.Category category, LoginActivity.Donor donor) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.category = category;
            this.donor = donor;
        }

    }

    private enum Category {
        FOOD, EDUCATION, CLOTHING, EMPTY
    }

    private enum Donor {
        DONOR, RECIPIENT, EMPTY
    }

    class LoginUser extends AsyncTask<URL, String, String> {
        JSONObject postData;

        public LoginUser(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
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

                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(connect.getOutputStream());
                    writer.write(postData.toString());
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

