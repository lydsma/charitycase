package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

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

        allUsersArray = new ArrayList<LoginActivity.User>();
        allEmails = new HashSet<String>();

        if (allEmails.contains(email)) {
            User temp = allUsers.get(email);
            // valid email
        } else {
            Toast.makeText(this, "Incorrect Email or Password.\nTry again", Toast.LENGTH_LONG);
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
}
