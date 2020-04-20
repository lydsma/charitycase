package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

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

    }
}
