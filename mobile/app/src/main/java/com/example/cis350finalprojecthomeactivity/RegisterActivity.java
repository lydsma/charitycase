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
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL = "EMAIL";
    private EditText name;
    private EditText em;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_view);

        Spinner spinner1 = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = (Spinner) findViewById(R.id.userSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
    }

    public void onSubmitButtonClick(View v) {
        Intent i = new Intent(this, MainActivity.class);

        name = findViewById(R.id.storeName);
        String fullName = name.getText().toString();

        em = findViewById(R.id.storeEmail);
        String userEmail = em.getText().toString();

        pass = findViewById(R.id.storePassword);
        String password = pass.getText().toString();

        Spinner catSpinner = findViewById(R.id.categorySpinner);
        String category = catSpinner.getSelectedItem().toString();

        Spinner donSpinner = findViewById(R.id.userSpinner);
        String donorType = donSpinner.getSelectedItem().toString();

        User curr = new User(fullName, userEmail, password);

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

            if (msg.contains("Please")) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_LONG).show();
            }

            else if (msg.contains("User")) {
                Toast.makeText(this, "User account already exists", Toast.LENGTH_LONG).show();
            }

            else {
                Toast.makeText(this, "Registering...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra(EMAIL, curr.email);
                setResult(1, intent);
                startActivityForResult(intent, 1);
            }
/*
            if (msg.equals("results: Please fill out all fields")) {
                Toast.makeText(this, "Registering...", Toast.LENGTH_LONG).show();
            }

            if (msg.equals("Success")) {
                Toast.makeText(this, "Registering...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra(EMAIL, curr.email);
                setResult(1, intent);
                finish();
            } else {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }


 */
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

        String fullName;
        String email;
        String password;

        public User(String name, String email, String password) {
            this.fullName = name;
            this.email = email;
            this.password = password;
        }

    }


}
