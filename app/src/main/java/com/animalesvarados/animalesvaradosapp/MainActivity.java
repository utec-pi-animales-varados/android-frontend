package com.animalesvarados.animalesvaradosapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.provider.Settings.Secure;

@SuppressLint("Registered")
public class MainActivity extends AppCompatActivity {

    int authenticateNumberOfIntents = 0;

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

        Constant.deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

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

        logInvitado(Constant.deviceId);

    }

    public void logInvitado (String deviceId){


        Map<String, String> message = new HashMap<>();
        message.put("deviceId", deviceId);

        JSONObject jsonMessage = new JSONObject(message);


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constant.DB_URL.concat("/authenticateGuest"),
                jsonMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            Log.d("response","success");
                            Constant.jwt = response.getString("jwt");
                            Constant.userId = response.getInt("user_id");
                            Intent intent = new Intent(getActivity(),DrawerActivity.class);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        if(authenticateNumberOfIntents < 3 ) {
                            Log.d("Error; ", "LLAMAR REGISTER");
                            RegistrarInvitado();
                            authenticateNumberOfIntents++;
                        }
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }


    public void RegistrarInvitado() {

        Map<String, Object> message = new HashMap<>();
        message.put("deviceId",Constant.deviceId);

        // 3. Converting the message object to JSON string (jsonify)
        JSONObject jsonMessage = new JSONObject(message);

        // 4. Sending json message to Server
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "http://107.180.91.147:8080/animales_varados-0.1/signup",
                jsonMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO
                        try {
                            //Intent intent = new Intent(getActivity(), LoginActivity.class);
                            //startActivity(intent);
                            logInvitado(Constant.deviceId);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Log.d("ERROR: ",e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if( error instanceof AuthFailureError){
                            Log.d("Ya registrado este user id","");
                        }
                        else {
                            Log.d("ERROR: ",error.getMessage()); }
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


}
