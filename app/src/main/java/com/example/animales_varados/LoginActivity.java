package com.example.animales_varados;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public Activity getActivity(){
        return this;
    }

    public void onBtnLoginClicked(View view){
        //1.  Getting username and password (from the view)
        EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
        EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        //2.  Creating a message using user input
        Map<String, String> message = new HashMap<>();
        message.put("username", username);
        message.put("password", password);

        //3.  Converting the message object to JSON string (jsonify)
        JSONObject jsonMessage = new JSONObject(message);
        //Toast.makeText(this,jsonMessage.toString(),Toast.LENGTH_LONG).show();

        //4.  Sending json message to the server
        //4.1. Install volley
        //4.2. Create request object
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "http://10.0.2.2:8000/authenticate",
                jsonMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO Qué hacer cuando el server responda
                        showMessage("Authorized!");
                        try {
                            String token = response.getString("jwt");
                            //String username = response.getString("username");
                            //int user_id = response.getInt("user_id");
                            //String nombre = response.getString("nombre");
                            //goToExperienciasActivity(user_id,username,nombre);

                        } catch (JSONException e) {
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


    /*
    private void goToExperienciasActivity(int user_id,String username,String nombre) {
        Intent intent = new Intent(this, ExperienciasActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("username",username);
        intent.putExtra("nombre",nombre);
        startActivity(intent);
    }
     */

}