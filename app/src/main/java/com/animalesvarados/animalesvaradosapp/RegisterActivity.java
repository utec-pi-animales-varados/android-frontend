package com.animalesvarados.animalesvaradosapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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


public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public Activity getActivity(){
        return this;
    }


    public void Registrarse(View view) {

        // 1. Getting username and password inputs from view
        EditText txtEmail = (EditText) findViewById(R.id.r_mail);
        EditText txtName = (EditText) findViewById(R.id.r_Name);
        EditText txtLastname = (EditText) findViewById(R.id.r_Apellido);
        EditText txtPassword = (EditText) findViewById(R.id.r_Password);
        EditText txtPhone = (EditText) findViewById(R.id.r_phone);

        final String email = txtEmail.getText().toString();
        final String name = txtName.getText().toString();
        String lastname = txtLastname.getText().toString();
        final String password = txtPassword.getText().toString();
        String phone = txtPhone.getText().toString();

        // 2. Creating a message from user input data
        Map<String, Object> message = new HashMap<>();
        message.put("deviceId","111111111111111");
        message.put("name", name);
        message.put("lastName", lastname);
        message.put("email", email);
        message.put("password", password);
        message.put("mobilephone",phone);

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
                            showMessage("¡" + name + ", tu usuario fue registrado con exito!");
                            logPostRegister(email,password);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            showMessage(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if( error instanceof AuthFailureError){
                            showMessage(email+" ya exite, intente con otro");
                        }
                        else {
                            showMessage(error.getMessage());
                        }
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    public void logPostRegister(String email, String contrasena){


        Map<String, String> message = new HashMap<>();
        message.put("username", email);
        message.put("password", contrasena);

        JSONObject jsonMessage = new JSONObject(message);


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constant.DB_URL.concat("/authenticate"),
                jsonMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO Qué hacer cuando el server responda
                        showMessage("Authorized!");
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
                    public void onErrorResponse(VolleyError error) {
                        //TODO Qué hacer cuando ocurra un error
                        showMessage("Unauthorized!!!");
                    }
                }
        );

        //5. Send Request to the Server
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }



}


