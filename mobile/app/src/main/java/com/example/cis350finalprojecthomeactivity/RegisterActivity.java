package com.example.cis350finalprojecthomeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL = "EMAIL";
    private EditText first;
    private EditText last;
    private EditText em;
    private EditText pass;

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
        String LastName = last.getText().toString();

        em = (EditText) findViewById(R.id.storeEmail);
        String userEmail = em.getText().toString();

        pass = (EditText) findViewById(R.id.storePassword);
        String password = pass.getText().toString();

        Spinner catSpinner = (Spinner) findViewById(R.id.categorySpinner);
        String category = catSpinner.getSelectedItem().toString();

        Spinner donSpinner = (Spinner) findViewById(R.id.userSpinner);
        String donorType = donSpinner.getSelectedItem().toString();

        i.putExtra(EMAIL, userEmail);

        startActivity(i);
    }

    public void onBackButtonClick(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
