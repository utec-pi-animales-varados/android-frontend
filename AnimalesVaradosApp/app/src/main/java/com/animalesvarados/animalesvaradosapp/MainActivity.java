package com.animalesvarados.animalesvaradosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("Registered")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Reporte animales varados");
    }

    public Activity getActivity(){
        return this;
    }

    public void Go_login(View view){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }


    public void Go_register(View view){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
