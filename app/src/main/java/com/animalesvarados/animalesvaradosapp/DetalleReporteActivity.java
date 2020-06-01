package com.animalesvarados.animalesvaradosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DetalleReporteActivity extends AppCompatActivity {

    int id_reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_reporte = getIntent().getExtras().getInt("id_reporte");
        Log.d("id_reporte",Integer.toString(id_reporte));
        setContentView(R.layout.activity_detallereporte);
        getReporte();
    }


    public void getReporte() {
        final TextView animal = findViewById(R.id.animal);
        final TextView fecha = findViewById(R.id.fecha);
        final TextView ubicacion = findViewById(R.id.ubicacion);

        JSONObject jsonMessage = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Constant.DB_URL.concat("/reportes/"+id_reporte),
                jsonMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String coords = response.getString("latitude")+", "+response.getString("longitude");
                            animal.setText(response.getString("id"));
                            fecha.setText(response.getString("date"));
                            ubicacion.setText(coords);
                            Log.d("TEST: ", response.toString());

                        } catch (JSONException e){
                            Log.d("Exception", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO Qué hacer cuando ocurra un error
                    }
                }
        ){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer ".concat(Constant.jwt));
                return params;
            }
        };

        //5. Send Request to the Server
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }



}
