package com.animalesvarados.animalesvaradosapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferenceConfig sharedPreferencesConfig;
    EditText txtUsername;
    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        //SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sharedPreferencesConfig = new SharedPreferenceConfig(getApplicationContext());

        if(sharedPreferencesConfig.read_login_status()){
            startActivity(new Intent(this,DrawerActivity.class));
            finish();
        }

    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public Activity getActivity(){
        return this;
    }

    public void onBtnLoginClicked(View view){
        //1.  Getting username and password (from the view)
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();

        Log.d("user",username);
        Log.d("password",password);
        Log.d("url",Constant.DB_URL);

        //2.  Creating a message using user input
        Map<String, String> message = new HashMap<>();
        message.put("email", username);
        message.put("password", password);

        //3.  Converting the message object to JSON string (jsonify)
        JSONObject jsonMessage = new JSONObject(message);

        //4.  Sending json message to the server
        //4.1. Install volley
        //4.2. Create request object
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
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            sharedPreferencesConfig.login_status(true);
                            finish();
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