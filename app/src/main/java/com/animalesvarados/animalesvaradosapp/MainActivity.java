package com.animalesvarados.animalesvaradosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        final Button loginbtn = findViewById(R.id.go_cuenta);
        final Button regbtn = findViewById(R.id.go_invitado);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Go_cuentas();
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Go_invitado();
            }
        });
    }

    public Activity getActivity(){
        return this;
    }


    public void Go_cuentas(){
        Intent intent = new Intent(getActivity(), IngresoCuentaActivity.class);
        startActivity(intent);
    }


    public void Go_invitado(){

        //INGRESAR COMO INVITADO
        Log.d("Ingresando como invitado: ","true");

        Intent intent = new Intent(getActivity(), DrawerActivity.class);
        startActivity(intent);
    }
}
