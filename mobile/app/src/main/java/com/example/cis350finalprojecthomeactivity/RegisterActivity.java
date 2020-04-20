package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL = "EMAIL";
    private EditText first;
    private EditText last;
    private EditText em;
    private EditText pass;
    private List<User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        List<String> category = new ArrayList<String>();
        category.add("Food");
        category.add("Academic");
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

        first = (EditText) findViewById(R.id.storeFirstName);
        String firstName = first.getText().toString();

        last = (EditText) findViewById(R.id.storeLastName);
        String lastName = last.getText().toString();

        em = (EditText) findViewById(R.id.storeEmail);
        String userEmail = em.getText().toString();

        pass = (EditText) findViewById(R.id.storePassword);
        String password = pass.getText().toString();

        Spinner catSpinner = findViewById(R.id.categorySpinner);
        String category = catSpinner.getSelectedItem().toString();

        Spinner donSpinner = findViewById(R.id.userSpinner);
        String donorType = donSpinner.getSelectedItem().toString();

        String fullName = firstName + " " + lastName;

        i.putExtra(EMAIL, userEmail);

        startActivity(i);
    }

    public void onBackButtonClick(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    /*                BACKEND FUNCTIONS                      */

    class getUsersFromDB extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {


            try {
                URL url = new URL("http://10.0.2.2:3000/");

                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                // send http GET request to server
                connect.setRequestMethod("GET");
                connect.connect();

                // read response using Scanner
                Scanner in = new Scanner(url.openStream());
                String msg = "";

                while (in.hasNext()) {
                    msg += in.nextLine();
                }

                // JSONObject arrayOfPosts = new JSONObject(msg);

                // turn arrayOfPosts into allPosts

                return msg;

            } catch (IOException e) {
                return e.toString();
            } catch (Exception e) {
                return e.toString();
            }
        }

    }

    /*
    ~~~~~~~~~~~~~~~  USER CLASS  ~~~~~~~~~~~~~~~
     */

    private class User {

        Category category;
        Donor donor;
        String firstName;
        String lastName;
        String email;
        String password;

        public User(String firstName, String lastName, String email, String password,
                    Category category, Donor donor) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
            this.category = category;
            this.donor = donor;
        }

    }

    private enum Category {
        FOOD, EDUCATION, CLOTHING
    }

    private enum Donor {
        DONOR, RECIPIENT
    }

}
