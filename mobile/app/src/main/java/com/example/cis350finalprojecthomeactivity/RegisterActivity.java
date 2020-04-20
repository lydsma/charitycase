package com.example.cis350finalprojecthomeactivity;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

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
}
