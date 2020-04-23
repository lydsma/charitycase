package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL = "EMAIL";
    private EditText first;
    private EditText last;
    private EditText em;
    private EditText pass;
    private List<User> allUsers;
    private HashSet<String> allEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        List<String> category = new ArrayList<String>();
        category.add("Food");
        category.add("Education");
        category.add("Clothing");

        List<String> donorType = new ArrayList<String>();
        donorType.add("Donor");
        donorType.add("Recipient");

        // category spinner
        Spinner catSpinner = (Spinner) findViewById(R.id.categorySpinner);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, category);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        catSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        catSpinner.setAdapter(catAdapter);

        // donor spinner
        Spinner donSpinner = (Spinner) findViewById(R.id.userSpinner);

        ArrayAdapter<String> donAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, donorType);
        donAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        donSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        donSpinner.setAdapter(donAdapter);
    }

    public void onSubmitButtonClick(View v) {
        Intent i = new Intent(this, MainActivity.class);

        first = findViewById(R.id.storeFirstName);
        String firstName = first.getText().toString();

        last = findViewById(R.id.storeLastName);
        String lastName = last.getText().toString();

        em = findViewById(R.id.storeEmail);
        String userEmail = em.getText().toString();

        pass = findViewById(R.id.storePassword);
        String password = pass.getText().toString();

        Spinner catSpinner = findViewById(R.id.categorySpinner);
        String category = catSpinner.getSelectedItem().toString();

        Spinner donSpinner = findViewById(R.id.userSpinner);
        String donorType = donSpinner.getSelectedItem().toString();

        String fullName = firstName + " " + lastName;

        User curr = new User(firstName, lastName, userEmail, password);

        Map<String, String> input = new HashMap<String, String>();
        input.put("name", fullName);
        input.put("email", curr.email);
        input.put("password", curr.password);
        input.put("accType", donorType);

        try {
            URL url = new URL("http://10.0.2.2:3000/mobilesignup");
            AsyncTask<URL, String, String> task = new RegisterUser(input);
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

    public void onBackButtonClick(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    /*                BACKEND FUNCTIONS                      */

    class RegisterUser extends AsyncTask<URL, String, String> {
        JSONObject postData;

        public RegisterUser(Map<String, String> postData) {
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

    /*
    ~~~~~~~~~~~~~~~  USER CLASS  ~~~~~~~~~~~~~~~
     */

    private class User {

        String firstName;
        String lastName;
        String email;
        String password;

        public User(String firstName, String lastName, String email, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
        }

    }


}
