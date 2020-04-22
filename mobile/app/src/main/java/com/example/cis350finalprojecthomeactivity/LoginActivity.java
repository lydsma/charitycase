package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    public static final String EMAIL = "EMAIL";
    private EditText em;
    private EditText pass;
    private List<LoginActivity.User> allUsersArray;
    private Hashtable<String, User> allUsers;
    private HashSet<String> allEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

    }

    public void onSignUpButtonClick(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void onLoginButtonClick(View v) {

        for (LoginActivity.User user : allUsersArray) {
            allEmails.add(user.email);
            allUsers.put(user.email, user);
        }

        em = findViewById(R.id.email);
        String email = em.getText().toString();

        pass = findViewById(R.id.password);
        String password = pass.getText().toString();

        try {
            URL url = new URL("http://10.0.2.2:3000/mobilechecklogin");
            AsyncTask<URL, String, String> task = new LoginActivity.LoginUser();
            task.execute(url);

            // get the response and Toast it
            String msg = task.get();

            //@stev and ozzi this is 4 if u need to covert to json array
            /**
             JSONObject arrayOfPosts = new JSONObject(msg);
             JSONArray posts = arrayOfPosts.getJSONArray("posts");
             */

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

        allUsersArray = new ArrayList<LoginActivity.User>();
        allEmails = new HashSet<String>();

        if (allEmails.contains(email)) {
            User temp = allUsers.get(email);
            // valid email
            if (temp.password.equals(password)) {
                // valid login, go to home
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra(EMAIL, temp.email);
                startActivity(i);
            } else {
                Toast.makeText(this, "Incorrect Email or Password.\nTry again", Toast.LENGTH_LONG);
            }
        } else {
            Toast.makeText(this, "No account found with this email.\nTry again", Toast.LENGTH_LONG);
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

        @Override
        protected String doInBackground(URL... urls) {


            try {
                URL url = urls[0];

                HttpURLConnection connect = (HttpURLConnection) url.openConnection();

                // send http GET request to server
                connect.setRequestMethod("GET");
                connect.connect();

                // read response using Scanner
                Scanner in = new Scanner(url.openStream());
                String msg = "";

                while (in.hasNext()) {
                    msg = in.useDelimiter("\\A").next();
                }

                // JSONObject arrayOfPosts = new JSONObject(msg);

                // turn arrayOfPosts into allPosts
                Log.v("string results", msg);
                return msg;

            } catch (IOException e) {
                return e.toString();
            } catch (Exception e) {
                return e.toString();
            }
        }

    }
}
