package com.animalesvarados.animalesvaradosapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ReportListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
