package com.animalesvarados.animalesvaradosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("Registered")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Reporte animales varados");

        final Button loginbtn = findViewById(R.id.go_login);
        final Button regbtn = findViewById(R.id.go_register);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Go_login();
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Go_register();
            }
        });
    }

    public Activity getActivity(){
        return this;
    }


    public void Go_login(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }


    public void Go_register(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
